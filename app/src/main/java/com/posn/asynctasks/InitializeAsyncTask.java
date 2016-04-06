package com.posn.asynctasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.datatypes.Friend;
import com.posn.datatypes.Message;
import com.posn.datatypes.Notification;
import com.posn.datatypes.Post;
import com.posn.main.MainActivity;
import com.posn.utility.FileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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
            // get the friend list file
            System.out.println("GETTING FRIENDS!!!");
            loadFriendsListFile(Constants.wallFilePath + "/user_friends.txt");

            // get the wall post file
            System.out.println("GETTING WALL POSTS!!!");
            loadWallPostsFile(Constants.wallFilePath + "/user_wall.txt");

            // get the notifications file
            System.out.println("GETTING NOTIFICATIONS!!!");
            loadNotificationsFile(Constants.wallFilePath + "/user_notifications.txt");

            // get the messages file
            System.out.println("GETTING MESSAGES!!!");
            loadMessagesFile(Constants.messagesFilePath + "/user_messages.txt");

            // check/create cloud storage directories
            main.cloud.createStorageDirectoriesOnCloud();

            String link = main.cloud.uploadFileToCloud("multimedia", "Test123.jpg", Constants.multimediaFilePath + "/test.jpg");
            System.out.println(link);
            return null;
         }


      // After completing background task Dismiss the progress dialog
      protected void onPostExecute(String file_url)
         {
            delegate.initializingFileDataFinished();

            // dismiss the dialog once done
            pDialog.dismiss();
         }

      private void loadFriendsListFile(String fileName)
         {
            String fileContents = FileManager.loadFileFromDevice(fileName);

            try
               {
                  JSONObject data = new JSONObject(fileContents);

                  JSONArray friendsList = data.getJSONArray("friends");

                  for (int n = 0; n < friendsList.length(); n++)
                     {
                        Friend friend = new Friend();
                        friend.parseJOSNObject(friendsList.getJSONObject(n));

                        if (friend.status == Constants.STATUS_ACCEPTED || friend.status == Constants.STATUS_PENDING)
                           {
                              main.masterFriendList.put(friend.id, friend);
                           }
                        else
                           {
                              main.masterRequestsList.add(friend);
                           }
                     }
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      private void loadWallPostsFile(String fileName)
         {
            String fileContents = FileManager.loadFileFromDevice(fileName);
            try
               {
                  JSONObject data = new JSONObject(fileContents);

                  JSONArray wallPosts = data.getJSONArray("posts");

                  for (int n = 0; n < wallPosts.length(); n++)
                     {
                        Post post = new Post();
                        post.parseJOSNObject(wallPosts.getJSONObject(n));

                        main.wallPostData.add(post);
                     }
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      private void loadNotificationsFile(String fileName)
         {
            // open the file
            String fileContents = FileManager.loadFileFromDevice(fileName);

            try
               {
                  JSONObject data = new JSONObject(fileContents);

                  JSONArray notifications = data.getJSONArray("notifications");

                  for (int n = 0; n < notifications.length(); n++)
                     {
                        Notification notification = new Notification();
                        notification.parseJOSNObject(notifications.getJSONObject(n));

                        main.notificationData.add(notification);
                     }
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      private void loadMessagesFile(String fileName)
         {
            // open the file
            String fileContents = FileManager.loadFileFromDevice(fileName);

            try
               {
                  JSONObject data = new JSONObject(fileContents);

                  JSONArray messageList = data.getJSONArray("messages");

                  for (int n = 0; n < messageList.length(); n++)
                     {
                        Message message = new Message();
                        message.parseJOSNObject(messageList.getJSONObject(n));

                        main.messageData.add(message);
                     }
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }
   }