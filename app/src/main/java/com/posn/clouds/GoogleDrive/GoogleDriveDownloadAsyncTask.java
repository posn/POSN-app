package com.posn.clouds.GoogleDrive;

import android.os.AsyncTask;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class GoogleDriveDownloadAsyncTask extends AsyncTask<Boolean, Void, Void>
   {
      // declare variables
      GoogleDriveClientUsage googleDrive;
      DriveId srcFolder;
      String fileName;
      String outputPath;

      public GoogleDriveDownloadAsyncTask(GoogleDriveClientUsage googleDrive, DriveId srcFolder, String fileName, String outputPath)
         {
            this.googleDrive = googleDrive;
            this.srcFolder = srcFolder;
            this.fileName = fileName;
            this.outputPath = outputPath;
         }


      @Override
      protected Void doInBackground(Boolean... arg0)
         {
            // get the folder where the file is located
            DriveFolder folder = Drive.DriveApi.getFolder(googleDrive.mGoogleApiClient, srcFolder);

            // check if the file exists
            Query query = new Query.Builder()
                              .addFilter(Filters.contains(SearchableField.TITLE, fileName))
                              .build();

            // execute query and wait for result
            DriveApi.MetadataBufferResult metaData = folder.queryChildren(googleDrive.mGoogleApiClient, query).await();
            DriveApi.DriveContentsResult driveContentsResult;

            // if the query is not empty then download the file
            if (metaData.getMetadataBuffer().getCount() != 0)
               {
                  // get the file from Drive
                  DriveFile file = Drive.DriveApi.getFile(googleDrive.mGoogleApiClient, metaData.getMetadataBuffer().get(0).getDriveId());

                  // set the file to be read
                  driveContentsResult = file.open(googleDrive.mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();

                  try
                     {
                        // get all the Drive file data contents
                        DriveContents driveContents = driveContentsResult.getDriveContents();

                        InputStream input = driveContents.getInputStream();

                        // open a new file on device
                        OutputStream output = new BufferedOutputStream(new FileOutputStream(new File(outputPath)));
                        int bufferSize = 1024;
                        byte[] buffer = new byte[bufferSize];

                        // read all the Drive file data into a buffer
                        int len = 0;
                        while ((len = input.read(buffer)) != -1)
                           {
                              // write data to the new file
                              output.write(buffer, 0, len);
                           }

                        // close streams
                        input.close();
                        output.close();

                        // discard file changes
                        driveContents.discard(googleDrive.mGoogleApiClient);

                        System.out.println("Downloaded file: " + (driveContentsResult.getStatus().isSuccess() ? "True" : "False"));
                     }
                  catch (IOException e)
                     {
                        e.printStackTrace();
                     }
               }

            // release the Metadata
            metaData.release();

            return null;
         }


      protected void onPostExecute(Void result)
         {

         }

   }



