package com.posn.asynctasks.groups;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.datatypes.Post;
import com.posn.datatypes.UserGroup;
import com.posn.encryption.SymmetricKeyManager;
import com.posn.initial_setup.SetupGroupsActivity;
import com.posn.utility.CloudFileManager;
import com.posn.utility.IDGenerator;

import java.util.ArrayList;
import java.util.HashMap;


public class SetupGroupAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;

      private SetupGroupsActivity activity;
      private ArrayList<String> groupList;
      private Intent intent;

      public SetupGroupAsyncTask(SetupGroupsActivity act, ArrayList<String> groups, Intent i)
         {
            super();
            activity = act;
            groupList = groups;
            intent = i;
         }


      // Before starting background thread Show Progress Dialog
      @Override
      protected void onPreExecute()
         {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Creating Groups...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }


      // Checking login in background
      protected String doInBackground(String... params)
         {
            for(int i = 0; i < groupList.size(); i++)
               {
                  UserGroup userGroup = new UserGroup();
                  userGroup.name = groupList.get(i);

                  // generate group ID
                  userGroup.ID = IDGenerator.generate(userGroup.name);

                  // generate group wall and archive key
                  userGroup.groupFileKey = SymmetricKeyManager.createRandomKey();

                  // create empty group wall file on device to upload to cloud
                  String fileName = "group_" + userGroup.name.toLowerCase() + "_0.txt";
                  String deviceFilepath = Constants.wallFilePath + "/" + fileName;
                  CloudFileManager.createGroupWallFile(userGroup, new HashMap<String, Post>(), deviceFilepath);

                  // upload group wall to cloud and get direct link
                  userGroup.groupFileLink = activity.cloud.uploadFileToCloud(Constants.wallDirectory, fileName, deviceFilepath);

                  // create new group object and add to group list
                  activity.userGroupList.groups.put(userGroup.ID, userGroup);
               }

            // save group list to device
            activity.userGroupList.saveGroupsToFile(Constants.applicationDataFilePath + Constants.groupListFile);

            return null;
         }


      // After completing background task Dismiss the progress dialog
      protected void onPostExecute(String file_url)
         {
            // dismiss the dialog once done
            pDialog.dismiss();

            // start new activity
            activity.startActivity(intent);
         }
   }