package com.posn.asynctasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.main.MainActivity;


public class InitializeAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;
      public AsyncResponseIntialize delegate = null;
      private MainActivity main;



      public InitializeAsyncTask(MainActivity activity)
         {
            super();
            main = activity;
         }


      // Before starting background thread Show Progress Dialog
      @Override
      protected void onPreExecute()
         {
            super.onPreExecute();
            pDialog = new ProgressDialog(main);
            pDialog.setMessage("Loading Friend Data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }


      // Checking login in background
      protected String doInBackground(String... params)
         {
            // get the group list file
            System.out.println("GETTING GROUPS!!!");
            main.groupList.loadGroupsFromFile(Constants.applicationDataFilePath + "/user_groups.txt");

            // get the friend list file
            System.out.println("GETTING FRIENDS!!!");
            main.masterFriendList.loadFriendsListFromFile(Constants.applicationDataFilePath + "/user_friends.txt");

            // get the wall post file
            System.out.println("GETTING WALL POSTS!!!");
            main.wallPostList.loadWallPostsFromFile(Constants.applicationDataFilePath + "/user_wall.txt");

            // get the notifications file
            System.out.println("GETTING NOTIFICATIONS!!!");
            main.notificationList.loadNotificationsFromFile(Constants.applicationDataFilePath + "/user_notifications.txt");

            // get the messages file
            System.out.println("GETTING MESSAGES!!!");
            main.conversationList.loadConversationsFromFile(Constants.applicationDataFilePath + "/user_messages.txt");

            // check/create cloud storage directories
            main.cloud.createStorageDirectoriesOnCloud();

           // String link = main.cloud.uploadFileToCloud("multimedia", "Test123.jpg", Constants.multimediaFilePath + "/test.jpg");
           // System.out.println(link);

            //FileManager.downloadFileFromURL(link, Constants.multimediaFilePath, "FromURL.jpg");
            return null;
         }


      // After completing background task Dismiss the progress dialog
      protected void onPostExecute(String file_url)
         {
            delegate.initializingFileDataFinished();

            // dismiss the dialog once done
            pDialog.dismiss();
         }








   }