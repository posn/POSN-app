package com.posn.asynctasks.groups;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.datatypes.Group;
import com.posn.encryption.SymmetricKeyManager;
import com.posn.main.MainActivity;
import com.posn.utility.CloudFileManager;
import com.posn.utility.IDGenerator;


public class NewGroupAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;

      private MainActivity main;
      private String newGroup;

      public NewGroupAsyncTask(MainActivity mainActivity, String groupName)
         {
            super();
            main = mainActivity;
            newGroup = groupName;
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
            // generate group ID
            String groupID = IDGenerator.generate(newGroup);

            // generate group wall and archive key
            String groupWallKey = SymmetricKeyManager.createRandomKey();

            // create empty group wall file on device to upload to cloud
            String fileName = "group_" + newGroup.toLowerCase() + "_0.txt";
            String deviceFilepath = Constants.wallFilePath + "/" + fileName;
            CloudFileManager.createGroupWallFile(deviceFilepath, groupWallKey);

            // upload group wall to cloud and get direct link
            String directLink = main.cloud.uploadFileToCloud(Constants.wallDirectory, fileName, deviceFilepath);

            // create new group object and add to group list
            Group group = new Group(groupID, newGroup, directLink, groupWallKey);
            main.groupList.groups.put(groupID, group);

            // save group list to device
            main.groupList.saveGroupsToFile(Constants.applicationDataFilePath + Constants.groupListFile);

            return null;
         }


      // After completing background task Dismiss the progress dialog
      protected void onPostExecute(String file_url)
         {
            // dismiss the dialog once done
            pDialog.dismiss();
         }
   }