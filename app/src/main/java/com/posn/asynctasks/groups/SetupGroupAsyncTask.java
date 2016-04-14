package com.posn.asynctasks.groups;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.datatypes.Group;
import com.posn.encryption.SymmetricKeyManager;
import com.posn.initial_setup.SetupGroupsActivity;
import com.posn.utility.CloudFileManager;
import com.posn.utility.IDGenerator;

import java.util.ArrayList;


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
                  String newGroup = groupList.get(i);

                  // generate group ID
                  String groupID = IDGenerator.generate(newGroup);

                  // generate group wall and archive key
                  String groupWallKey = SymmetricKeyManager.createRandomKey();

                  // create empty group wall file on device to upload to cloud
                  String fileName = "group_" + newGroup.toLowerCase() + "_0.txt";
                  String deviceFilepath = Constants.wallFilePath + "/" + fileName;
                  CloudFileManager.createGroupWallFile(deviceFilepath, groupWallKey);

                  // upload group wall to cloud and get direct link
                  String directLink = activity.cloud.uploadFileToCloud(Constants.wallDirectory, fileName, deviceFilepath);

                  // create new group object and add to group list
                  Group group = new Group(groupID, newGroup, directLink, groupWallKey);
                  activity.groupList.groups.put(groupID, group);
               }

            // save group list to device
            activity.groupList.saveGroupsToFile(Constants.applicationDataFilePath + Constants.groupListFile);

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