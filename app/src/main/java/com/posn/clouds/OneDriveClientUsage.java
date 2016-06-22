package com.posn.clouds;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
import com.posn.Constants;
import com.posn.main.MainActivity;

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

public class OneDriveClientUsage extends CloudProvider
   {
      public final String[] SCOPES = {"wl.signin", "wl.basic", "wl.offline_access", "wl.skydrive_update", "wl.contacts_create",};

      public HashMap<String, String> folderIds;

      private Context context;

      public LiveAuthClient mAuthClient;
      public LiveConnectClient mConnectClient;
      public LiveConnectSession mSession;


      public OneDriveClientUsage(Context context)
         {
            // set the activity context
            this.context = context;

            mAuthClient = new LiveAuthClient(context, Constants.ONEDRIVE_CLIENT_ID);
            folderIds = new HashMap<>();
         }

      @Override
      public void initializeCloud()
         {
            // initialize onedrive
            mAuthClient.initialize(Arrays.asList(SCOPES), new LiveAuthListener()
            {
               @Override
               public void onAuthError(LiveAuthException exception, Object userState)
                  {
                     //mInitializeDialog.dismiss();
                     showToast(exception.getMessage());
                  }

               @Override
               public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState)
                  {
                     // mInitializeDialog.dismiss();

                     if (status == LiveStatus.CONNECTED)
                        {
                           showToast("Skydrive Connected!");
                           mSession = session;
                           mConnectClient = new LiveConnectClient(session);
                           isConnected = true;
                        }
                     else
                        {
                           showToast("Initialize did not connect. Please try login in.");

                           mAuthClient.login((MainActivity) context, Arrays.asList(SCOPES), new LiveAuthListener()
                           {
                              @Override
                              public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState)
                                 {
                                    if (status == LiveStatus.CONNECTED)
                                       {
                                          showToast("Skydrive Connected!");
                                          mSession = session;
                                          mConnectClient = new LiveConnectClient(session);
                                          isConnected = true;
                                       }
                                    else
                                       {
                                          showToast("Login did not connect. Status is " + status + ".");
                                       }
                                 }

                              @Override
                              public void onAuthError(LiveAuthException exception, Object userState)
                                 {
                                    showToast(exception.getMessage());
                                 }
                           });
                        }
                  }
            });


         }

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


      @Override
      public void downloadFileFromCloud(String folderName, String fileName, String devicePath)
         {
            // initialize variables
            int i = 0;
            boolean found = false;

            try
               {
                  // get all the files in the folder
                  LiveOperation operation = mConnectClient.get(folderIds.get(folderName) + "/files");

                  // get the files into a JSON array
                  JSONObject result = operation.getResult();
                  JSONArray data = result.optJSONArray("data");

                  // look through all the files and search for the desired file to be downloaded
                  while (!found && i < data.length())
                     {
                        // get the file/folder json object
                        JSONObject object = data.getJSONObject(i);

                        // check the name if its the correct folder
                        if (object.getString("name").equals(fileName))
                           {
                              // download the file
                              LiveDownloadOperation downloadOpperation = mConnectClient.download(object.getString("id") + "/content");

                              // get the file data stream
                              InputStream input = downloadOpperation.getStream();

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
                              found = true;
                           }
                        i++;
                     }

               }

            catch (IOException | JSONException | LiveOperationException ex)
               {
                  // print error and try to download the file again
                  System.out.println("Error downloading: " + ex.getMessage());
               }
         }

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


      @Override
      public void activityResult(int requestCode, int resultCode, Intent data)
         {
         }

      @Override
      public void onResume()
         {
         }


      private void showToast(String message)
         {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
         }

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
                        if (result.getJSONObject("error").optString("code").equals("resource_already_exists"))
                           {
                              // if it does then get the list of items in the parent folder to get the folder ID
                              LiveOperation fileList = mConnectClient.get(mainFolderID + "/files");

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
                                    if (object.getString("name").equals(subFolder))
                                       {
                                          folderIds.put(subFolder, object.optString("id"));
                                          found = true;
                                       }
                                    i++;
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
   }
