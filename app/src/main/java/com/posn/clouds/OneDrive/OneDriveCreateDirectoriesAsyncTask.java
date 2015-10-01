package com.posn.clouds.OneDrive;

import android.os.AsyncTask;
import android.os.Environment;

import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class OneDriveCreateDirectoriesAsyncTask extends AsyncTask<Boolean, Void, Void>
   {
      // declare variables
      OneDriveClientUsage oneDrive;
      public HashMap<String, String> folderIds;


      public OneDriveCreateDirectoriesAsyncTask(OneDriveClientUsage oneDrive)
         {
            this.oneDrive = oneDrive;
            folderIds = oneDrive.folderIds;
         }


      @Override
      protected Void doInBackground(Boolean... arg0)
         {
            System.out.println("CREATING");

            // create the parent folder
            createMainFolder("POSN");

            String parentFolder = folderIds.get("POSN");

            // create the sub folders
            createSubFolder(parentFolder, "archive");
            createSubFolder(parentFolder, "keys");
            createSubFolder(parentFolder, "multimedia");
            createSubFolder(parentFolder, "profile");
            createSubFolder(parentFolder, "wall");

            return null;
         }


      protected void onPostExecute(Void result)
         {
            //oneDrive.uploadFile("multimedia", "test.jpg", Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/multimedia" + "/test.jpg");
            //oneDrive.uploadFile("multimedia", "test2.txt", Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/wall" + "/user_notifications.txt");
            //oneDrive.downloadFile("multimedia", "test2.txt", Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/wall" + "/test2.txt");
            oneDrive.downloadFile("multimedia", "test.jpg", Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/wall" + "/test.jpg");
         }


      private void createMainFolder(String folderName)
         {
            try
               {
                  // create a new JSON Object to create a new folder
                  JSONObject body = new JSONObject();
                  body.put("name", folderName);

                  // create the folder
                  LiveOperation operation = oneDrive.mConnectClient.post("me/skydrive", body);

                  // get the result of creating the folder
                  JSONObject result = operation.getResult();

                  // check if the folder already exists
                  if (result.has("error"))
                     {
                        if (result.getJSONObject("error").optString("code").equals("resource_already_exists"))
                           {
                              // if it does then get the list of items in the parent folder to get the folder ID
                              LiveOperation fileList = oneDrive.mConnectClient.get("me/skydrive/files");

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
                  LiveOperation operation = oneDrive.mConnectClient.post(mainFolderID, body);

                  // get the result of creating the folder
                  JSONObject result = operation.getResult();


                  // check if the folder already exists
                  if (result.has("error"))
                     {
                        if (result.getJSONObject("error").optString("code").equals("resource_already_exists"))
                           {
                              // if it does then get the list of items in the parent folder to get the folder ID
                              LiveOperation fileList = oneDrive.mConnectClient.get(mainFolderID + "/files");

                              // get all of the json objects
                              JSONObject list = fileList.getResult();
                              System.out.println(list.toString());
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



