package com.posn.asynctasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.datatypes.Friend;
import com.posn.datatypes.FriendGroup;
import com.posn.datatypes.WallPost;
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
            boolean processFriendWallFiles;

            // loop through all of the friends
            for (Map.Entry<String, Friend> entry : main.masterFriendList.currentFriends.entrySet())
               {
                  // bool value to process the friend file, set to false if the temporal file has not been updated
                  processFriendWallFiles = true;

                  // get the friend from the hash map
                  Friend friend = entry.getValue();

                  //check if the temporal file needs to be checked
                  if (friend.status == Constants.STATUS_TEMPORAL)
                     {
                        // get the temporal file from the cloud
                        String deviceFilepath = Constants.wallFilePath;
                        String fileName = "temporal_file.txt";
                        DeviceFileManager.downloadFileFromURL(friend.friendFileLink, deviceFilepath, fileName);

                        // attempt to get the updated friend file link
                        boolean fetchedFriendFile = CloudFileManager.loadTemporalFriendFile(main.user, friend, deviceFilepath, fileName);

                        // update friend status
                        if (fetchedFriendFile)
                           {
                              // change the friend status to accepted
                              friend.status = Constants.STATUS_ACCEPTED;

                              // get the most up to date friend file
                              fileName = friend.ID + "_friend_user_file.txt";
                              deviceFilepath = Constants.wallFilePath;
                              DeviceFileManager.downloadFileFromURL(friend.friendFileLink, deviceFilepath, fileName);

                              // load the friend file
                              CloudFileManager.loadFriendFile(friend, deviceFilepath, fileName);

                              // update the friendlist
                              main.masterFriendList.currentFriends.put(friend.ID, friend);
                              main.masterFriendList.saveFriendsListToFile();

                           }
                        processFriendWallFiles = fetchedFriendFile;
                     }

                  // check if the friend file should be processed
                  if (processFriendWallFiles)
                     {
                        System.out.println("UPDATING WALL FILES!!!");

                        // loop through all friend groups
                        for (int i = 0; i < friend.friendGroups.size(); i++)
                           {
                              FriendGroup group = friend.friendGroups.get(i);

                              // download the group's wall file and get the list of posts
                              String fileName = "wall_file.txt";
                              String deviceFilepath = Constants.wallFilePath;
                              ArrayList<WallPost> wallWallPosts = CloudFileManager.fetchAndLoadGroupWallFile(group, deviceFilepath, fileName);

                              if (wallWallPosts != null)
                                 {
                                    // loop through all wall posts
                                    for (WallPost wallPost : wallWallPosts)
                                       {
                                          // check if the post should be added to the main wall post list
                                          if (!main.masterWallPostList.wallPosts.containsKey(wallPost.postID))
                                             {
                                                // add the post to the post list
                                                main.masterWallPostList.wallPosts.put(wallPost.postID, wallPost);

                                                // check if there is multimedia
                                                if (wallPost.type == Constants.POST_TYPE_PHOTO)
                                                   {
                                                      // download the multimedia from the cloud
                                                      DeviceFileManager.downloadFileFromURL(wallPost.multimediaLink, Constants.multimediaFilePath, wallPost.postID + ".jpg");
                                                   }
                                             }
                                       }
                                 }
                           }
                     }
               }

            // save the updated post list to the device
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