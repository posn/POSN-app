package com.posn.main.main.wall.asynctasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.constants.Constants;
import com.posn.datatypes.Comment;
import com.posn.datatypes.UserGroup;
import com.posn.datatypes.WallPost;
import com.posn.exceptions.POSNCryptoException;
import com.posn.managers.AppDataManager;
import com.posn.main.main.MainActivity;
import com.posn.main.main.wall.UserWallFragment;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


/**
 * This AsyncTask class implements the functionality to append new comments to the group walls that the post was shared with:
 * <ul><li>Handles if a user makes a comment on their own post (Needs to implement direct notifications to online users)
 * <li>Need to implement commenting on friends posts (how to send the comments)</ul>
 **/
public class NewCommentAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;

      private MainActivity main;
      private ArrayList<Comment> newCommentsList;
      private AppDataManager dataManager;
      private WallPost post;

      public NewCommentAsyncTask(UserWallFragment frag, WallPost post, ArrayList<Comment> newComments)
         {
            super();
            this.main = frag.main;
            this.newCommentsList = newComments;
            dataManager = main.dataManager;
            this.post = post;
         }


      // Before starting background thread Show Progress Dialog
      @Override
      protected void onPreExecute()
         {
            super.onPreExecute();
            pDialog = new ProgressDialog(main);
            if(newCommentsList.size() > 1)
               {
                  pDialog.setMessage("Adding Comments...");
               }
            else
               {
                  pDialog.setMessage("Adding Comment...");
               }
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }


      // Checking login in background
      protected String doInBackground(String... params)
         {
            try
               {
                  // add all the comments to the master wall post list
                  for(int i = 0; i < newCommentsList.size(); i++)
                     {
                        Comment newComment = newCommentsList.get(i);
                        // add post to wall post
                        dataManager.wallPostManager.addCommentToWallPost(newComment);
                     }

                  // check if the user commented on their own post
                  if (post.friendID.equals(dataManager.userManager.ID))
                     {
                        // Since its a user comment on their own post, update all the group walls that contain the post
                        for (Map.Entry<String, UserGroup> entry : dataManager.userGroupManager.userGroups.entrySet())
                           {
                              // get the group
                              UserGroup group = entry.getValue();

                              // check if that wall post ID is part of that group
                              if (group.wallPostList.contains(post.postID))
                                 {
                                    // create a new group wall file with the new comments and upload it to the cloud
                                    String fileName = "group_" + group.name + "_" + group.version + ".txt";
                                    String deviceFilepath = Constants.wallFilePath;
                                    dataManager.createGroupWallFile(group.ID, deviceFilepath, fileName);

                                    // upload group wall to cloud
                                    main.cloud.uploadFileToCloud(Constants.wallDirectory, fileName, deviceFilepath + "/" + fileName);
                                 }
                           }

                        // NEED TO SEND DIRECT NOTIFICATIONS TO ONLINE USERS
                     }
                  else
                     {
                        // need to send comment directly if the friend is online

                        // if offline, then do nothing for now
                        // This is where the implementation will go to send the comments to the friend who made the post
                        // The comment propagation scheme needs to be finalized
                     }

                  // save wall post list to device
                  dataManager.saveWallPostListAppFile();
               }
            catch (IOException | JSONException | POSNCryptoException error)
               {
                  error.printStackTrace();
               }

            return null;
         }


      // After completing background task Dismiss the progress dialog
      protected void onPostExecute(String file_url)
         {
            // dismiss the dialog once done
            pDialog.dismiss();
         }
   }