package com.posn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.posn.datatypes.ConversationList;
import com.posn.datatypes.FriendList;
import com.posn.datatypes.Group;
import com.posn.datatypes.NotificationList;
import com.posn.datatypes.WallPostList;
import com.posn.encryption.SymmetricKeyManager;
import com.posn.initial_setup.SetupGroupsActivity;
import com.posn.utility.CloudFileManager;
import com.posn.utility.IDGenerator;

import java.util.ArrayList;


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

            // get the friend list file
            FriendList friendList = new FriendList();
            friendList.saveFriendsListToFile(Constants.applicationDataFilePath + "/user_friends.txt");

            // get the wall post file
            WallPostList wallPostList = new WallPostList();
            wallPostList.saveWallPostsToFile(Constants.applicationDataFilePath + "/user_wall.txt");

            // get the notifications file
            NotificationList notificationList = new NotificationList();
            notificationList.saveNotificationsToFile(Constants.applicationDataFilePath + "/user_notifications.txt");

            // get the messages file
            ConversationList conversationList = new ConversationList();
            conversationList.saveConversationListToFile(Constants.applicationDataFilePath + "/user_messages.txt");

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