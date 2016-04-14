package com.posn.clouds;

// This is an abstract class to generalize the access to the different cloud providers

import android.content.Intent;

public abstract class CloudProvider
   {

      public CloudProvider()
         {
         }

      public abstract void initializeCloud();

      public abstract void createStorageDirectoriesOnCloudAsyncTask();

      public abstract void downloadFileFromCloudAsyncTask(String folderName, String fileName, String devicePath);

      public abstract void uploadFileToCloudAsyncTask(String folderName, String fileName, String devicePath);

      public abstract void createStorageDirectoriesOnCloud();

      public abstract void downloadFileFromCloud(String folderName, String fileName, String devicePath);

      public abstract String uploadFileToCloud(String folderName, String fileName, String devicePath);

      public abstract void activityResult(int requestCode, int resultCode, Intent data);

      public abstract void onResume();
   }
