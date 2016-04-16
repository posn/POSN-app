package com.posn.asynctasks.groups;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.datatypes.UserGroup;
import com.posn.encryption.SymmetricKeyManager;
import com.posn.main.MainActivity;
import com.posn.utility.CloudFileManager;
import com.posn.utility.IDGenerator;


public class NewGroupAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;

      private MainActivity main;
      private String newGroupName;

      public NewGroupAsyncTask(MainActivity mainActivity, String groupName)
         {
            super();
            main = mainActivity;
            newGroupName = groupName;
         }


      // Before starting background thread Show Progress Dialog
      @Override
      protected void onPreExecute()
         {
            super.onPreExecute();
            pDialog = new ProgressDialog(main);
            pDialog.setMessage("Creating Group...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }


      // Checking login in background
      protected String doInBackground(String... params)
         {
            // create new group object
            UserGroup group = new UserGroup();

            // generate group ID
            group.ID = IDGenerator.generate(newGroupName);
            group.name = newGroupName;

            // generate group wall and archive key
            group.groupFileLink = SymmetricKeyManager.createRandomKey();

            // create empty group wall file on device to upload to cloud
            String fileName = "group_" + group.name + "_" + group.version + ".txt";
            String deviceFilepath = Constants.wallFilePath + "/" + fileName;
            CloudFileManager.createGroupWallFile(group, main.masterWallPostList.wallPosts, deviceFilepath);

            // upload group wall to cloud and get direct link
            group.groupFileLink = main.cloud.uploadFileToCloud(Constants.wallDirectory, fileName, deviceFilepath);

            // create new group object and add to group list
            main.userGroupList.groups.put(group.ID, group);

            // save group list to device
            main.userGroupList.saveGroupsToFile(Constants.applicationDataFilePath + Constants.groupListFile);

            return null;
         }


      // After completing background task Dismiss the progress dialog
      protected void onPostExecute(String file_url)
         {
            // dismiss the dialog once done
            pDialog.dismiss();
         }
   }