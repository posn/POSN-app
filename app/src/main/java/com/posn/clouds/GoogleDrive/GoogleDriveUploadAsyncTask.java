package com.posn.clouds.GoogleDrive;

import android.os.AsyncTask;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;


public class GoogleDriveUploadAsyncTask extends AsyncTask<Boolean, Void, Void>
   {
      // declare variables
      GoogleDriveClientUsage googleDrive;
      DriveId destFolder;
      String fileName;
      String MIME_Type;
      String inputPath;
      String directLink = null;

      public GoogleDriveUploadAsyncTask(GoogleDriveClientUsage googleDrive, DriveId destFolder, String fileName, String MIME_Type, String inputPath)
         {
            this.googleDrive = googleDrive;
            this.destFolder = destFolder;
            this.fileName = fileName;
            this.MIME_Type = MIME_Type;
            this.inputPath = inputPath;
         }


      @Override
      protected Void doInBackground(Boolean... arg0)
         {
            // get the folder where the file is located
            DriveFolder folder = Drive.DriveApi.getFolder(googleDrive.mGoogleApiClient, destFolder);

            Query query = new Query.Builder()
                              .addFilter(Filters.contains(SearchableField.TITLE, fileName))
                              .build();

            // execute query and wait for result
            DriveApi.MetadataBufferResult metaData = folder.queryChildren(googleDrive.mGoogleApiClient, query).await();

            // check if the file exists
            DriveApi.DriveContentsResult driveContentsResult;

            // if the query is empty then create a subfolder
            if (metaData.getMetadataBuffer().getCount() == 0)
               {
                  // if the file does not exist, then create a new file
                  driveContentsResult = Drive.DriveApi.newDriveContents(googleDrive.mGoogleApiClient).await();
               }
            else
               {
                  // if the file exists, then get the file
                  DriveFile file = Drive.DriveApi.getFile(googleDrive.mGoogleApiClient, metaData.getMetadataBuffer().get(0).getDriveId());

                  // get the direct link for the file
                  DriveResource.MetadataResult meta = file.getMetadata(googleDrive.mGoogleApiClient).await();
                  directLink = meta.getMetadata().getWebContentLink();

                  // open the file and edit the contents
                  driveContentsResult = file.open(googleDrive.mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();
               }

            // get the content from file
            DriveContents driveContents = driveContentsResult.getDriveContents();

            OutputStream outputStream = driveContents.getOutputStream();

            // open the file and get the file size
            File myFile = new File(inputPath);
            int size = (int) myFile.length();
            byte[] bytes = new byte[size];

            // write all of the file contents to the Drive file
            try
               {
                  // read all the file data in
                  BufferedInputStream input = new BufferedInputStream(new FileInputStream(new File(inputPath)));
                  input.read(bytes, 0, bytes.length);

                  // write data to Drive file
                  outputStream.write(bytes);

                  // close streams
                  outputStream.close();
                  input.close();
               }
            catch (IOException e)
               {
                  e.printStackTrace();
               }

            // if the file is a new file, upload as a new file
            if (metaData.getMetadataBuffer().getCount() == 0)
               {
                  // set metadata
                  MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                                    .setTitle(fileName)
                                                    .setMimeType(MIME_Type)
                                                    .build();

                  // create the file in Drive
                  DriveFolder.DriveFileResult finalResult = folder.createFile(googleDrive.mGoogleApiClient, changeSet, driveContents).await();

                  // get the direct download link for the file
                  // Need to do in a while loop with sleep because Google Drive has a delay for the file to register after the upload
                  while (directLink == null)
                     {
                        try
                           {
                              Thread.sleep(2000, 0);
                           }
                        catch (InterruptedException e)
                           {
                              System.out.println(e.getMessage());
                           }

                        // get the file meta data to acquire the link
                        DriveResource.MetadataResult meta = finalResult.getDriveFile().getMetadata(googleDrive.mGoogleApiClient).await();
                        directLink = meta.getMetadata().getWebContentLink();
                     }
               }
            else
               {
                  // update the Drive file with new contents
                  com.google.android.gms.common.api.Status status = driveContents.commit(googleDrive.mGoogleApiClient, null).await();
                  //System.out.println("Updated file: " + (status.getStatus().isSuccess() ? "True" : "False"));
               }

            //System.out.println("Google LINK: " + directLink);

            // release the metadata
            metaData.release();

            return null;
         }

      protected void onPostExecute(Void result)
         {

         }
   }



