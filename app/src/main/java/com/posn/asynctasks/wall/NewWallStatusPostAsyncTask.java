package com.posn.asynctasks.wall;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;

import com.posn.Constants;
import com.posn.datatypes.UserGroup;
import com.posn.datatypes.WallPost;
import com.posn.exceptions.POSNCryptoException;
import com.posn.main.MainActivity;
import com.posn.main.wall.UserWallFragment;
import com.posn.main.wall.posts.StatusPostItem;
import com.posn.main.AppDataManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;


public class NewWallStatusPostAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;
      private String status;
      private UserWallFragment wallFrag;
      private ArrayList<String> groupIDs;
      private MainActivity main;
      private AppDataManager dataManager;

      public NewWallStatusPostAsyncTask(UserWallFragment frag, ArrayList<String> groupIDs, String status)
         {
            super();
            wallFrag = frag;
            this.status = status;
            this.groupIDs = groupIDs;
            main = wallFrag.main;
            dataManager = main.dataManager;
         }


      // Before starting background thread Show Progress Dialog
      @Override
      protected void onPreExecute()
         {
            super.onPreExecute();
            pDialog = new ProgressDialog(main);
            pDialog.setMessage("Creating New Post...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }


      // Checking login in background
      protected String doInBackground(String... params)
         {
            try
               {
                  // create a new wall post
                  WallPost wallPost = new WallPost(Constants.POST_TYPE_STATUS, dataManager.user.ID, status);

                  // add the post new the main wall post list
                  dataManager.masterWallPostList.wallPosts.put(wallPost.postID, wallPost);

                  // save the main wall post list to the device
                  dataManager.saveWallPostListAppFile();

                  // add the new wall post to the listview
                  wallFrag.listViewItems.add(0, new StatusPostItem(wallFrag, dataManager.user.firstName + " " + dataManager.user.lastName, wallPost));

                  // go through all the groups and add the wall post to group walls
                  for (int i = 0; i < groupIDs.size(); i++)
                     {
                        // get the group from the group ID
                        UserGroup group = dataManager.userGroupList.getUserGroup(groupIDs.get(i));

                        // add the post ID to the group
                        group.wallPostList.add(wallPost.postID);

                        // save the group file to device
                        String fileName = "group_" + group.name + "_" + group.version + ".txt";
                        String deviceFilepath = Constants.wallFilePath;
                        dataManager.createGroupWallFile(group.ID, deviceFilepath, fileName);

                        // upload the updated group wall file to the cloud
                        main.cloud.uploadFileToCloud(Constants.wallDirectory, fileName, deviceFilepath + "/" + fileName);

                        // update the group in the hashmap
                        dataManager.userGroupList.userGroups.put(group.ID, group);
                     }

                  dataManager.saveUserGroupListAppFile();
               }
            catch (JSONException | IOException | POSNCryptoException error)
               {
                  error.printStackTrace();
               }
            return null;
         }


      // After completing background task Dismiss the progress dialog
      protected void onPostExecute(String file_url)
         {
            wallFrag.noWallPostsText.setVisibility(View.GONE);
            wallFrag.sortWallPostList();

            // notify the adapter that the data changed
            wallFrag.adapter.notifyDataSetChanged();

            // dismiss the dialog once done
            pDialog.dismiss();
         }
   }