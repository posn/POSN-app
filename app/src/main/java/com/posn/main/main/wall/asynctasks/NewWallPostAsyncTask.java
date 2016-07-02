package com.posn.main.main.wall.asynctasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.constants.Constants;
import com.posn.datatypes.UserGroup;
import com.posn.datatypes.WallPost;
import com.posn.exceptions.POSNCryptoException;
import com.posn.managers.AppDataManager;
import com.posn.main.main.MainActivity;
import com.posn.main.main.wall.UserWallFragment;
import com.posn.utility.SymmetricKeyHelper;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This AsyncTask class implements the functionality to process a new wall post:
 * <ul><li>If the post is a photo, then the photo is encrypted and uploads it to the cloud
 * <li>Creates a new wall post object, saves the data in the wall hashmap and to the app file
 * <li>Adds the new wall post to the appropriate groups and saves the user group data</ul>
 **/
public class NewWallPostAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;
      private String postData;
      private int postType;
      private UserWallFragment wallFrag;
      private ArrayList<String> groupIDs;
      private MainActivity main;

      private AppDataManager dataManager;
      private WallPost wallPost;

      public NewWallPostAsyncTask(UserWallFragment frag, ArrayList<String> groupIDs, int postType, String postData)
         {
            super();
            wallFrag = frag;
            this.postType = postType;
            this.postData = postData;
            this.groupIDs = groupIDs;
            main = wallFrag.main;
            dataManager = wallFrag.dataManager;
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

      protected String doInBackground(String... params)
         {
            try
               {
                  // check if the post is a status or photo post
                  if(postType == Constants.POST_TYPE_STATUS)
                     {
                        // create a new status wall post
                        wallPost = new WallPost(Constants.POST_TYPE_STATUS, dataManager.userManager.ID, postData);
                     }
                  else if(postType == Constants.POST_TYPE_PHOTO)
                     {
                        // create a new wall post for a photo
                        wallPost = new WallPost(Constants.POST_TYPE_PHOTO, dataManager.userManager.ID);

                        // encrypt and upload the photo the the cloud
                        encryptAndUploadPhotoToCloud(postData);
                     }

                  // add the new post to the wall post list
                  dataManager.wallPostManager.addWallPost(wallPost);

                  // save the wall post list to the device
                  dataManager.saveWallPostListAppFile();

                  // add the new post to the appropriate groups
                  addWallPostToUserGroups();

                  // save the group data to the device
                  dataManager.saveUserGroupListAppFile();
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
            // add the new wall post to the listview
            wallFrag.addNewWallPost(wallPost);

            // dismiss the dialog once done
            pDialog.dismiss();
         }

      private void encryptAndUploadPhotoToCloud(String photoPath) throws POSNCryptoException
         {
            // create a new symmetric key for the photo
            wallPost.multimediaKey = SymmetricKeyHelper.createRandomKey();

            // encrypt the photo and store on the device
            String filename = wallPost.postID + ".jpg";
            String folder = Constants.multimediaFilePath;
            SymmetricKeyHelper.encryptFile(wallPost.multimediaKey, photoPath, folder + "/" + filename);

            // upload the photo to the cloud
            wallPost.multimediaLink = main.cloud.uploadFileToCloud(Constants.multimediaDirectory, filename, folder + "/" + filename);
         }

      private void addWallPostToUserGroups() throws IOException, POSNCryptoException, JSONException
         {
            // go through all the groups and add the wall post to group walls
            for (int i = 0; i < groupIDs.size(); i++)
               {
                  // get the group from the group ID
                  UserGroup group = dataManager.userGroupManager.getUserGroup(groupIDs.get(i));

                  // add the post ID to the group
                  group.wallPostList.add(wallPost.postID);

                  // save the group file to device
                  String fileName = "group_" + group.name + "_" + group.version + ".txt";
                  String deviceFilepath = Constants.wallFilePath;
                  dataManager.createGroupWallFile(group.ID, deviceFilepath, fileName);

                  // upload the updated group wall file to the cloud
                  main.cloud.uploadFileToCloud(Constants.wallDirectory, fileName, deviceFilepath + "/" + fileName);

                  // update the group in the hashmap
                  dataManager.userGroupManager.updateUserGroup(group);
               }
         }

   }