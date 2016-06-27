package com.posn.clouds;

import android.content.Intent;

/**
 * This interface class defines functions that all cloud providers need to have
 **/
public interface CloudProvider
   {
      void initializeCloud();

      void createStorageDirectoriesOnCloudAsyncTask();
      void downloadFileFromCloudAsyncTask(String folderName, String fileName, String devicePath);
      void uploadFileToCloudAsyncTask(String folderName, String fileName, String devicePath);

      void createStorageDirectoriesOnCloud();
      void downloadFileFromCloud(String folderName, String fileName, String devicePath);
      String uploadFileToCloud(String folderName, String fileName, String devicePath);
      void removeFileOnCloud(String folderName, String fileName);

      void activityResult(int requestCode, int resultCode, Intent data);
      void onResume();
   }
