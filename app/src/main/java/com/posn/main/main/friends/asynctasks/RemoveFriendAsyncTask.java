package com.posn.main.main.friends.asynctasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.constants.Constants;
import com.posn.datatypes.Friend;
import com.posn.datatypes.UserGroup;
import com.posn.exceptions.POSNCryptoException;
import com.posn.main.main.MainActivity;
import com.posn.main.main.friends.UserFriendsFragment;
import com.posn.managers.AppDataManager;
import com.posn.utility.SymmetricKeyHelper;

import org.json.JSONException;

import java.io.IOException;


/**
 * This AsyncTask class implements the functionality to completely remove a friend from the user's friend list:
 * <ul><li>Removes the friend from the group and invokes the archiving process</ul>
 **/
public class RemoveFriendAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;

      private MainActivity main;
      private AppDataManager dataManager;
      private UserFriendsFragment fragment;
      private Friend removedFriend;

      public RemoveFriendAsyncTask(UserFriendsFragment fragment, Friend removedFriend)
         {
            super();
            this.fragment = fragment;
            main = fragment.activity;
            this.removedFriend = removedFriend;
            dataManager = main.dataManager;
         }


      @Override
      protected void onPreExecute()
         {
            super.onPreExecute();
            pDialog = new ProgressDialog(main);
            pDialog.setMessage("Updating Group...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }

      protected String doInBackground(String... params)
         {
            try
               {
                  // loop through all the groups the friend is in
                  for( String groupID : removedFriend.userGroups)
                     {
                        // get the user group object
                        UserGroup group = dataManager.userGroupManager.getUserGroup(groupID);

                        // remove the friend from the friends list
                        group.friendsList.remove(removedFriend.ID);

                        // update the group object in the data manager
                        dataManager.userGroupManager.updateUserGroup(group);

                        // archive the group
                        archiveGroupWallFile(group);

                        // update the group members friend files
                        updateGroupMembers(group);
                     }

                  // remove the friend from the friends list
                  dataManager.friendManager.currentFriends.remove(removedFriend.ID);

                  // delete the removed friend's friend file in the cloud
                  String fileName = removedFriend.ID + "_friend_file.txt";
                  main.cloud.removeFileOnCloud(Constants.friendDirectory, fileName);

                  // save the changes to the user group and friend lists
                  dataManager.saveUserGroupListAppFile();
                  dataManager.saveFriendListAppFile(false);
               }
            catch (IOException | JSONException | POSNCryptoException e)
               {
                  e.printStackTrace();
               }

            return null;
         }

      protected void onPostExecute(String file_url)
         {
            // update the friends list
            fragment.updateFriendList();

            // update the user group fragment
            main.tabsAdapter.notifyUserGroupFragmentOnNewDataChange();

            // dismiss the dialog once done
            pDialog.dismiss();
         }


      /**
       * This method updates the group members specified in the group member list by creating an updated friend file
       **/
      private void updateGroupMembers(UserGroup group) throws JSONException, IOException, POSNCryptoException
         {
            // loop through all the members
            for (String friendID : group.friendsList)
               {
                  // get the friend object from the friend manager
                  Friend friend = dataManager.friendManager.getFriend(friendID);

                  // create a new friend file with the new group
                  String fileName = friend.ID + "_friend_file.txt";
                  String deviceFilepath = Constants.friendsFilePath;
                  dataManager.createFriendFile(friend.ID, deviceFilepath, fileName);

                  // upload group wall to cloud and get direct link
                  main.cloud.uploadFileToCloud(Constants.friendDirectory, fileName, deviceFilepath + "/" + fileName);
               }
         }


      /**
       * This method handles the archive process by creating a new wall file thats encrypted with a new key and moving the old wall into the archive folder in the cloud
       **/
      private void archiveGroupWallFile(UserGroup group) throws IOException, JSONException, POSNCryptoException
         {
            // create the archive file (its just a wall file, except with a different file name and cloud location)
            String fileName = "archive_" + group.name + "_" + group.version + ".txt";
            String deviceFilepath = Constants.archiveFilePath;
            dataManager.createGroupWallFile(group.ID, deviceFilepath, fileName);

            // set the new archive link to uploaded file link
            group.archiveFileLink = main.cloud.uploadFileToCloud(Constants.archiveDirectory, fileName, deviceFilepath + "/" + fileName);

            // remove the old wall file in the cloud
            fileName = "group_" + group.name + "_" + group.version + ".txt";
            main.cloud.removeFileOnCloud(Constants.wallDirectory, fileName);

            // increment the version number
            group.version = group.version + 1;

            // set the new archive key to the old wall file key
            group.archiveFileKey = group.groupFileKey;

            // create a new key for the group
            group.groupFileKey = SymmetricKeyHelper.createRandomKey();

            // update the group in the data manager
            dataManager.userGroupManager.updateUserGroup(group);

            // create a new group wall file with the updated information
            fileName = "group_" + group.name + "_" + group.version + ".txt";
            deviceFilepath = Constants.wallFilePath;
            dataManager.createGroupWallFile(group.ID, deviceFilepath, fileName);

            // upload the updated group wall file to the cloud
            group.groupFileLink = main.cloud.uploadFileToCloud(Constants.wallDirectory, fileName, deviceFilepath + "/" + fileName);

            // update the group in the data manager so the new link is added
            dataManager.userGroupManager.updateUserGroup(group);
         }
   }

