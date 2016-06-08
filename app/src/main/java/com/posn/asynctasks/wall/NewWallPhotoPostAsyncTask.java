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
import com.posn.main.wall.posts.PhotoPostItem;
import com.posn.utility.POSNDataManager;
import com.posn.utility.SymmetricKeyManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;


public class NewWallPhotoPostAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;
      private String photopath;
      private UserWallFragment wallFrag;
      private ArrayList<String> groupIDs;
      private MainActivity main;

      private POSNDataManager dataManager;

      public NewWallPhotoPostAsyncTask(UserWallFragment frag, ArrayList<String> groupIDs, String photopath)
         {
            super();
            wallFrag = frag;
            this.photopath = photopath;
            this.groupIDs = groupIDs;
            main = wallFrag.activity;
            dataManager = wallFrag.activity.dataManager;
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
                  // create a new wall post for a photo
                  WallPost wallPost = new WallPost(Constants.POST_TYPE_PHOTO, dataManager.user.ID);

                  // create a new symmetric key for the photo
                  wallPost.multimediaKey = SymmetricKeyManager.createRandomKey();

                  // encrypt the photo and store on the device
                  String filename = wallPost.postID + ".jpg";
                  String folder = Constants.multimediaFilePath;
                  SymmetricKeyManager.encryptFile(wallPost.multimediaKey, photopath, folder + "/" + filename);

                  // upload the photo to the cloud
                  wallPost.multimediaLink = main.cloud.uploadFileToCloud(Constants.multimediaDirectory, filename, folder + "/" + filename);


                  // add the post new the main wall post list
                  dataManager.masterWallPostList.wallPosts.put(wallPost.postID, wallPost);

                  // save the main wall post list to the device
                  dataManager.saveWallPostListAppFile();

                  // add the new wall post to the listview
                  wallFrag.listViewItems.add(0, new PhotoPostItem(wallFrag, dataManager.user.firstName + " " + dataManager.user.lastName, wallPost));


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
                        dataManager.userGroupList.updateUserGroup(group);
                     }

                  main.dataManager.saveUserGroupListAppFile();
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
            wallFrag.noWallPostsText.setVisibility(View.GONE);

            wallFrag.sortWallPostList();

            // notify the adapter that the data changed
            wallFrag.adapter.notifyDataSetChanged();

            // dismiss the dialog once done
            pDialog.dismiss();
         }


   }