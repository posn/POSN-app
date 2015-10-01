package com.posn.clouds.OneDrive;

import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.live.LiveDownloadOperation;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class OneDriveDownloadAsyncTask extends AsyncTask<Boolean, Void, Void>
   {
      // declare variables
      OneDriveClientUsage oneDrive;
      String srcFolderID;
      String fileName;
      String outputPath;

      public OneDriveDownloadAsyncTask(OneDriveClientUsage oneDrive, String srcFolderID, String fileName, String outputPath)
         {
            this.oneDrive = oneDrive;
            this.srcFolderID = srcFolderID;
            this.fileName = fileName;
            this.outputPath = outputPath;
         }


      @Override
      protected Void doInBackground(Boolean... arg0)
         {
            // initialize variables
            int i = 0;
            boolean found = false;

            try
               {
                  // get all the files in the folder
                  LiveOperation operation = oneDrive.mConnectClient.get(srcFolderID + "/files");

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
                              LiveDownloadOperation downloadOpperation = oneDrive.mConnectClient.download(object.getString("id") + "/content");

                              // get the file data stream
                              InputStream input = downloadOpperation.getStream();

                              // create a new file on the device
                              File f = new File(outputPath);

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

            return null;
         }


      protected void onPostExecute(Void result)
         {

         }

   }



