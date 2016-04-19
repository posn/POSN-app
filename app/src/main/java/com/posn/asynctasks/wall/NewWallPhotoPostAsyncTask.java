package com.posn.asynctasks.wall;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.datatypes.Post;
import com.posn.datatypes.UserGroup;
import com.posn.encryption.SymmetricKeyManager;
import com.posn.main.MainActivity;
import com.posn.main.wall.UserWallFragment;
import com.posn.main.wall.posts.PhotoPostItem;
import com.posn.utility.CloudFileManager;

import java.util.ArrayList;


public class NewWallPhotoPostAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;
      private String photopath;
      private UserWallFragment wallFrag;
      private ArrayList<String> groupIDs;
      private MainActivity main;

      public NewWallPhotoPostAsyncTask(UserWallFragment frag, ArrayList<String> groupIDs, String photopath)
         {
            super();
            wallFrag = frag;
            this.photopath = photopath;
            this.groupIDs = groupIDs;
            main = wallFrag.activity;
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
            // create a new wall post for a photo
            Post post = new Post(Constants.POST_TYPE_PHOTO, main.user.ID);

            // create a new symmetric key for the photo
            post.multimediaKey = SymmetricKeyManager.createRandomKey();

            // encrypt the photo and store on the device
            String filename = post.postID + ".jpg";
            String folder = Constants.multimediaFilePath;
            SymmetricKeyManager.encryptFile(post.multimediaKey, photopath, folder + "/" + filename);

            // upload the photo to the cloud
           post.multimediaLink = main.cloud.uploadFileToCloud(Constants.multimediaDirectory, filename, folder + "/" + filename);


            // add the post new the main wall post list
            main.masterWallPostList.wallPosts.put(post.postID, post);

            // save the main wall post list to the device
            main.masterWallPostList.saveWallPostsToFile();

            // add the new wall post to the listview
            wallFrag.listViewItems.add(0, new PhotoPostItem(main, main.user.firstName + " " + main.user.lastName, post, folder + "/" + filename));


            // go through all the groups and add the wall post to group walls
            for (int i = 0; i < groupIDs.size(); i++)
               {
                  // get the group from the group ID
                  UserGroup group = main.user.userDefinedGroups.get(groupIDs.get(i));

                  // add the post ID to the group
                  group.wallPostList.add(post.postID);

                  // save the group file to device
                  String fileName = "group_" + group.name + "_" + group.version + ".txt";
                  String deviceFilepath = Constants.applicationDataFilePath + "/" + fileName;
                  CloudFileManager.createGroupWallFile(group, main.masterWallPostList.wallPosts, deviceFilepath);

                  // upload the updated group wall file to the cloud
                  main.cloud.uploadFileToCloud(Constants.wallDirectory, fileName, deviceFilepath);

                  // update the group in the hashmap
                  main.user.userDefinedGroups.put(group.ID, group);
               }

            return null;
         }


      // After completing background task Dismiss the progress dialog
      protected void onPostExecute(String file_url)
         {
            wallFrag.sortWallPostList();

            // notify the adapter that the data changed
            wallFrag.adapter.notifyDataSetChanged();

            // dismiss the dialog once done
            pDialog.dismiss();
         }


   }