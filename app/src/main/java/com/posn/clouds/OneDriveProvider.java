package com.posn.clouds;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveDownloadOperation;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveStatus;
import com.microsoft.live.OverwriteOption;
import com.posn.constants.Constants;
import com.posn.main.main.MainActivity;
import com.posn.utility.UserInterfaceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This class implements the OneDrive cloud functionality and uses the Cloud Provider interface
 **/
public class OneDriveProvider implements CloudProvider
   {
      // String array for the application access permissions
      private final String[] SCOPES = {"wl.signin", "wl.basic", "wl.offline_access", "wl.skydrive_update", "wl.contacts_create",};

      // hash map to hold the folder IDs of the storage directories
      private HashMap<String, String> folderIds;

      private Context context;
      private LiveConnectClient mConnectClient;
      private OnConnectedCloudListener connectedListener;


      public OneDriveProvider(Context context, OnConnectedCloudListener connectedListener)
         {
            // set the activity context
            this.context = context;

            this.connectedListener = connectedListener;
            folderIds = new HashMap<>();
         }

      /**
       * This method connects the application to the user's OneDrive account
       * The user will be prompted to log into their account and accept the permissions.
       * Note: the user only needs to log in once
       **/
      @Override
      public void initializeCloud()
         {
            final LiveAuthClient mAuthClient = new LiveAuthClient(context, Constants.ONEDRIVE_CLIENT_ID);

            // initialize OneDrive
            mAuthClient.initialize(Arrays.asList(SCOPES), new LiveAuthListener()
               {
                  @Override
                  public void onAuthError(LiveAuthException exception, Object userState)
                     {
                        UserInterfaceHelper.showToast(context, exception.getMessage());
                     }

                  @Override
                  public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState)
                     {
                        // check if the connection has been established
                        if (status == LiveStatus.CONNECTED)
                           {
                              UserInterfaceHelper.showToast(context, "OneDrive Connected!");
                              mConnectClient = new LiveConnectClient(session);

                              // call the on connected listener method
                              connectedListener.OnConnected();
                           }
                        else
                           {
                              // connection was not made, so have the user login
                              UserInterfaceHelper.showToast(context, "Initialize did not connect. Please try login in.");

                              mAuthClient.login((MainActivity) context, Arrays.asList(SCOPES), new LiveAuthListener()
                                 {
                                    @Override
                                    public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState)
                                       {
                                          if (status == LiveStatus.CONNECTED)
                                             {
                                                UserInterfaceHelper.showToast(context, "OneDrive Connected!");
                                                mConnectClient = new LiveConnectClient(session);

                                                // call the on connected listener method
                                                connectedListener.OnConnected();
                                             }
                                          else
                                             {
                                                UserInterfaceHelper.showToast(context, "Login did not connect. Status is " + status + ".");
                                             }
                                       }

                                    @Override
                                    public void onAuthError(LiveAuthException exception, Object userState)
                                       {
                                          UserInterfaceHelper.showToast(context, exception.getMessage());
                                       }
                                 });
                           }
                     }
               });


         }

      /**
       * This method creates the cloud storage directories in a asynctask (can be called from the main UI thread)
       **/
      @Override
      public void createStorageDirectoriesOnCloudAsyncTask()
         {
            new AsyncTask<Void, Void, Void>()
               {
                  protected Void doInBackground(Void... params)
                     {
                        createStorageDirectoriesOnCloud();
                        return null;
                     }
               }.execute();
         }

      /**
       * This method downloads a file from the user's cloud in a asynctask (can be called from the main UI thread)
       **/
      @Override
      public void downloadFileFromCloudAsyncTask(final String folderName, final String fileName, final String devicePath)
         {
            new AsyncTask<Void, Void, Void>()
               {
                  protected Void doInBackground(Void... params)
                     {
                        downloadFileFromCloud(folderName, fileName, devicePath);
                        return null;
                     }

               }.execute();
         }

      /**
       * This method uploads a file to the user's cloud in a asynctask (can be called from the main UI thread)
       **/
      @Override
      public void uploadFileToCloudAsyncTask(final String folderName, final String fileName, final String devicePath)
         {
            new AsyncTask<Void, Void, Void>()
               {
                  protected Void doInBackground(Void... params)
                     {
                        uploadFileToCloud(folderName, fileName, devicePath);
                        return null;
                     }
               }.execute();
         }


      /**
       * This method downloads a file from the user's cloud (can not be called from the main UI thread)
       **/
      @Override
      public void downloadFileFromCloud(String folderName, String fileName, String devicePath)
         {
            try
               {
                  // fetch the file ID
                  String fileID = fetchFileId(folderName, fileName);

                  // check the name if its the correct folder
                  if (fileID != null)
                     {
                        // download the file
                        LiveDownloadOperation downloadOperation = mConnectClient.download(fileID + "/content");

                        // get the file data stream
                        InputStream input = downloadOperation.getStream();

                        // create a new file on the device
                        File f = new File(devicePath);

                        // copy the file stream into the file
                        OutputStream stream = new BufferedOutputStream(new FileOutputStream(f));

                        byte[] buffer = new byte[1024];
                        int len;

                        while ((len = input.read(buffer)) != -1)
                           {
                              stream.write(buffer, 0, len);
                           }

                        // close the output stream
                        stream.close();
                        input.close();

                        // set the file has been found and downloaded
                        Log.i("Sky drive", "File Downloaded");
                     }
               }
            catch (IOException | JSONException | LiveOperationException ex)
               {
                  System.out.println("Error downloading: " + ex.getMessage());
               }
         }


      /**
       * This method uploads a file to the user's cloud and returns the direct download link (can not be called from the main UI thread)
       * Overwrites an existing file with the same name
       **/
      @Override
      public String uploadFileToCloud(String folderName, String fileName, String devicePath)
         {
            String directLink = null;

            // open the file to be uploaded
            File file = new File(devicePath);

            try
               {
                  // upload the file to the specified folder
                  LiveOperation live = mConnectClient.upload(folderIds.get(folderName), fileName, file, OverwriteOption.Overwrite);

                  // get the status of the upload
                  JSONObject object = live.getResult();

                  // check if the file was uploaded or not
                  if (object.has("error"))
                     {
                        // upload fail, try to upload again
                        Log.i("Sky drive", "File Failed to Upload");
                     }
                  else
                     {
                        if (object.has("source"))
                           {
                              // get the direct download link:
                              directLink = object.getString("source");
                           }

                        // upload succeed, set the upload has been performed
                        Log.i("Sky drive", "File Uploaded");
                     }
               }
            catch (LiveOperationException | JSONException e)
               {
                  // upload failed, try to upload again
                  e.printStackTrace();
               }

            return directLink;
         }

      /**
       * This method removes a file from the user's cloud (can not be called from the main UI thread)
       **/
      @Override
      public void removeFileOnCloud(String folderName, String fileName)
         {
            try
               {
                  // get the file Id
                  String fileID = fetchFileId(folderName, fileName);

                  if (fileID != null)
                     {
                        mConnectClient.delete(fileID);
                     }
                  else
                     {
                        System.out.println("Error Deleting!!!!!");
                     }
               }
            catch (LiveOperationException | JSONException e)
               {
                  e.printStackTrace();
               }
         }

      /**
       * This method creates the cloud storage directories (can not be called from the main UI thread)
       **/
      @Override
      public void createStorageDirectoriesOnCloud()
         {
            // create the parent folder
            createMainFolder("POSN");

            String parentFolder = folderIds.get("POSN");

            // create the sub folders
            for (int i = 0; i < Constants.NUM_DIRECTORIES; i++)
               {
                  createSubFolder(parentFolder, Constants.directoryNames[i]);
               }
         }

      /**
       * This method is not required for OneDrive
       **/
      @Override
      public void activityResult(int requestCode, int resultCode, Intent data)
         {
         }


      /**
       * This method is not required for OneDrive
       **/
      @Override
      public void onResume()
         {
         }


      /**
       * This method creates the root folder for the application on the cloud (can not be called from the main UI thread)
       **/
      private void createMainFolder(String folderName)
         {
            try
               {
                  // create a new JSON Object to create a new folder
                  JSONObject body = new JSONObject();
                  body.put("name", folderName);

                  // create the folder
                  LiveOperation operation = mConnectClient.post("me/skydrive", body);

                  // get the result of creating the folder
                  JSONObject result = operation.getResult();

                  // check if the folder already exists
                  if (result.has("error"))
                     {
                        if (result.getJSONObject("error").optString("code").equals("resource_already_exists"))
                           {
                              // if it does then get the list of items in the parent folder to get the folder ID
                              LiveOperation fileList = mConnectClient.get("me/skydrive/files");

                              // get all of the json objects
                              JSONObject list = fileList.getResult();
                              JSONArray data = list.optJSONArray("data");

                              // loop through the objects to find the right folder
                              int i = 0;
                              boolean found = false;

                              while (!found && i < data.length())
                                 {
                                    // get the file/folder json object
                                    JSONObject object = data.getJSONObject(i);

                                    // check the name if its the correct folder
                                    if (object.getString("name").equals(folderName))
                                       {
                                          folderIds.put(folderName, object.optString("id"));
                                          found = true;
                                       }
                                    i++;
                                 }
                           }
                     }
                  else
                     {
                        folderIds.put(folderName, result.optString("id"));
                     }
               }
            catch (JSONException | LiveOperationException ex)
               {
                  System.out.println("Error building folder: " + ex.getMessage());
               }
         }

      /**
       * This method creates new directories within the root folder on the cloud (can not be called from the main UI thread)
       **/
      private void createSubFolder(String mainFolderID, String subFolder)
         {
            try
               {
                  // create a new JSON Object to create a new folder
                  JSONObject body = new JSONObject();
                  body.put("name", subFolder);

                  // create the folder
                  LiveOperation operation = mConnectClient.post(mainFolderID, body);

                  // get the result of creating the folder
                  JSONObject result = operation.getResult();

                  // check if the folder already exists
                  if (result.has("error"))
                     {
                        if (result.getJSONObject("error").optString("code").equals("resource_already_exists") && !folderIds.containsKey(subFolder))
                           {
                              // if it does then get the list of items in the parent folder to get the folder ID
                              LiveOperation fileList = mConnectClient.get(mainFolderID + "/files");

                              // get all of the json objects
                              JSONObject list = fileList.getResult();
                              JSONArray data = list.optJSONArray("data");

                              // loop through the objects to find the right folder
                              for (int i = 0; i < data.length(); i++)
                                 {
                                    // get the file/folder json object
                                    JSONObject object = data.getJSONObject(i);
                                    folderIds.put(object.getString("name"), object.optString("id"));
                                 }
                           }
                     }
                  else
                     {
                        folderIds.put(subFolder, result.optString("id"));
                     }
               }
            catch (JSONException | LiveOperationException ex)
               {
                  System.out.println("Error building folder: " + ex.getMessage());
               }
         }


      /**
       * This method gets the file ID for a given file name in a given folder
       **/
      private String fetchFileId(String folderName, String fileName) throws LiveOperationException, JSONException
         {
            // get all the files in the folder
            LiveOperation operation = mConnectClient.get(folderIds.get(folderName) + "/files");

            // get the files into a JSON array
            JSONObject result = operation.getResult();
            JSONArray data = result.optJSONArray("data");

            // look through all the files and search for the desired file to be downloaded
            for (int i = 0; i < data.length(); i++)
               {
                  // get the file/folder json object
                  JSONObject object = data.getJSONObject(i);

                  // check the name if its the correct folder
                  if (object.getString("name").equals(fileName))
                     {
                        return object.getString("id");
                     }
               }

            return null;
         }
   }
