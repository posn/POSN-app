package com.posn.clouds.GoogleDrive;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveId;

import java.util.HashMap;

public class GoogleDriveClientUsage
   {
      private Context context;
      public GoogleApiClient mGoogleApiClient;

      public HashMap<String, DriveId> folderIds;


      public GoogleDriveClientUsage(Context context)
         {
            // set the activity context
            this.context = context;
            folderIds = new HashMap<>();
         }

      public void downloadFile(String folder, String fileName, String outputPath)
         {
            new GoogleDriveDownloadAsyncTask(this, folderIds.get(folder), fileName, outputPath).execute();
         }

      public void uploadFile(String folder, String fileName, String MIME_Type, String inputPath)
         {
            new GoogleDriveUploadAsyncTask(this, folderIds.get(folder), fileName, MIME_Type, inputPath).execute();
         }

      public void createStorageDirectories()
         {
            new GoogleDriveCreateDirectoriesAsyncTask(this).execute();
         }

   }
