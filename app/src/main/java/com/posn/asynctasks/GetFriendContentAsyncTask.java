package com.posn.asynctasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.datatypes.Friend;
import com.posn.datatypes.FriendGroup;
import com.posn.datatypes.Post;
import com.posn.main.MainActivity;
import com.posn.utility.CloudFileManager;
import com.posn.utility.DeviceFileManager;

import java.util.ArrayList;
import java.util.Map;


public class GetFriendContentAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;
      public AsyncResponseIntialize delegate = null;
      private MainActivity main;


      public GetFriendContentAsyncTask(MainActivity activity)
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
            pDialog.setMessage("Getting Updated Friend Content...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }


      // Checking login in background
      protected String doInBackground(String... params)
         {
            boolean processFriendFile = true;

            // loop through all of the friends
            for (Map.Entry<String, Friend> entry : main.masterFriendList.currentFriends.entrySet())
               {
                  processFriendFile = true;

                  Friend friend = entry.getValue();

                  //check if the temporal file needs to be checked
                  if (friend.status == Constants.STATUS_TEMPORAL)
                     {
                        System.out.println("Name: " + friend.name);
                        System.out.println("TEMP LINK: " + friend.friendFileLink);

                        // get the temporal file
                        String deviceFilepath = Constants.wallFilePath;
                        String fileName = "temporal_file.txt";
                        DeviceFileManager.downloadFileFromURL(friend.friendFileLink, deviceFilepath, fileName);

                        // attempt to get the updated friend file link
                        processFriendFile = CloudFileManager.loadTemporalFriendFile(main.user, friend, deviceFilepath + "/" + fileName);

                        // update friend status
                        friend.status = Constants.STATUS_ACCEPTED;
                     }

                  if (processFriendFile)
                     {
                        // get updated friend file
                        String fileName = friend.ID + "_friend_user_file.txt";
                        String deviceFilepath = Constants.wallFilePath;
                        DeviceFileManager.downloadFileFromURL(friend.friendFileLink, deviceFilepath, fileName);

                        CloudFileManager.loadFriendFile(friend, deviceFilepath + "/" + fileName);

                        for (int i = 0; i < friend.friendGroups.size(); i++)
                           {
                              FriendGroup group = friend.friendGroups.get(i);

                              // download wall files
                              fileName = "wall_file.txt";
                              deviceFilepath = Constants.wallFilePath;
                              DeviceFileManager.downloadFileFromURL(group.groupFileLink, deviceFilepath, fileName);

                              ArrayList<Post> wallPosts = CloudFileManager.loadGroupWallFile(deviceFilepath + "/" + fileName);

                              // loop through all wall posts and check if they should be added to the main wall post list
                              for (Post post : wallPosts)
                                 {
                                    if (!main.masterWallPostList.wallPosts.containsKey(post.postID))
                                       {
                                          main.masterWallPostList.wallPosts.put(post.postID, post);

                                          if(post.type == Constants.POST_TYPE_PHOTO)
                                             {
                                                DeviceFileManager.downloadFileFromURL(post.multimediaLink, Constants.multimediaFilePath, post.postID + ".jpg");
                                             }
                                       }
                                 }
                           }
                     }


               }

            main.masterWallPostList.saveWallPostsToFile();

            return null;
         }


      // After completing background task Dismiss the progress dialog
      protected void onPostExecute(String file_url)
         {
            main.initializingFileDataFinished();
            // dismiss the dialog once done
            pDialog.dismiss();
         }


   }