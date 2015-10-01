package com.posn.clouds.OneDrive;

import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.OverwriteOption;

import org.json.JSONObject;

import java.io.File;


public class OneDriveUploadAsyncTask extends AsyncTask<Boolean, Void, Void>
   {
      // declare variables
      OneDriveClientUsage oneDrive;
      String destFolderID;
      String fileName;
      String inputPath;

      public OneDriveUploadAsyncTask(OneDriveClientUsage oneDrive, String destFolderID, String fileName, String inputPath)
         {
            this.oneDrive = oneDrive;
            this.destFolderID = destFolderID;
            this.fileName = fileName;
            this.inputPath = inputPath;
         }


      @Override
      protected Void doInBackground(Boolean... arg0)
         {
            // open the file to be uploaded
            File file = new File(inputPath);

            try
               {
                  // upload the file to the specified folder
                  LiveOperation live = oneDrive.mConnectClient.upload(destFolderID, fileName, file, OverwriteOption.Overwrite);

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
                        // upload succeed, set the upload has been performed
                        Log.i("Sky drive", "File Uploaded");
                     }
               }
            catch (LiveOperationException e)
               {
                  // upload failed, try to upload again
                  e.printStackTrace();
               }

            return null;
         }

      protected void onPostExecute(Void result)
         {

         }

   }



