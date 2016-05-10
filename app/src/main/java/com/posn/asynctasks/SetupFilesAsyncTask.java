package com.posn.asynctasks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.datatypes.ConversationList;
import com.posn.datatypes.FriendList;
import com.posn.datatypes.WallPost;
import com.posn.datatypes.UserGroup;
import com.posn.datatypes.NotificationList;
import com.posn.datatypes.WallPostList;
import com.posn.utility.SymmetricKeyManager;
import com.posn.initial_setup.SetupGroupsActivity;
import com.posn.utility.CloudFileManager;
import com.posn.utility.IDGenerator;

import java.util.ArrayList;
import java.util.HashMap;


public class SetupFilesAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;

      private SetupGroupsActivity activity;
      private ArrayList<String> groupList;
      private Intent intent;


      public SetupFilesAsyncTask(SetupGroupsActivity act, ArrayList<String> groups, Intent i)
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
            pDialog.setMessage("Creating Groups and Files...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }


      // Checking login in background
      protected String doInBackground(String... params)
         {
            for (int i = 0; i < groupList.size(); i++)
               {
                  UserGroup group = new UserGroup();
                  group.name = groupList.get(i);

                  // generate group ID
                  group.ID = IDGenerator.generate(group.name);

                  // generate group wall and archive key
                  group.groupFileKey = SymmetricKeyManager.createRandomKey();

                  // create empty group wall file on device to upload to cloud
                  String fileName = "group_" + group.name + "_" + group.version + ".txt";
                  String deviceFilepath = Constants.wallFilePath;
                  CloudFileManager.createGroupWallFile(group, new HashMap<String, WallPost>(), deviceFilepath, fileName);

                  // upload group wall to cloud and get direct link
                  group.groupFileLink = activity.cloud.uploadFileToCloud(Constants.wallDirectory, fileName, deviceFilepath + "/" + fileName);

                  // create new group object and add to group list
                  activity.user.userDefinedGroups.put(group.ID, group);
               }

            // generate symmetric key from user password
            String deviceFileKey = SymmetricKeyManager.createKeyFromString(activity.password);

            // save group list to device
            activity.user.deviceFileKey = deviceFileKey;
            activity.user.saveUserToFile();

            // get the friend list file
            FriendList friendList = new FriendList(deviceFileKey);
            friendList.saveFriendsListToFile();

            // get the wall post file
            WallPostList wallPostList = new WallPostList(deviceFileKey);
            wallPostList.saveWallPostsToFile();

            // get the notifications file
            NotificationList notificationList = new NotificationList(deviceFileKey);
            notificationList.saveNotificationsToFile();

            // get the messages file
            ConversationList conversationList = new ConversationList(deviceFileKey);
            conversationList.saveConversationListToFile();

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