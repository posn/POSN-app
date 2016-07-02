package com.posn.main.main.groups.asynctasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.constants.Constants;
import com.posn.datatypes.Friend;
import com.posn.datatypes.UserGroup;
import com.posn.exceptions.POSNCryptoException;
import com.posn.main.main.MainActivity;
import com.posn.main.main.groups.UserGroupsFragment;
import com.posn.managers.AppDataManager;
import com.posn.utility.SymmetricKeyHelper;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;


/**
 * This AsyncTask class implements the functionality to add and remove friends from a user defined group:
 * <ul><li>If added - adds the new group information to all the new members friend file
 * <li>If removed - removes the group information from all the removed members friend file and invokes the archiving process</ul>
 **/
public class ManageGroupAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;

      private MainActivity main;
      private AppDataManager dataManager;
      private UserGroupsFragment fragment;
      private UserGroup modifiedGroup;

      public ManageGroupAsyncTask(UserGroupsFragment fragment, UserGroup modifiedGroup)
         {
            super();
            this.fragment = fragment;
            main = fragment.activity;
            this.modifiedGroup = modifiedGroup;
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
                  // get the friend IDs of the members who are removed from the group
                  ArrayList<String> removedGroupMembers = createRemovedGroupMembersArrayList();

                  // process the revoked group members by updating their friend file and changing the affected group wall files and updated the affected friends
                  boolean hasRemovedMembers = processRemovedGroupMembers(removedGroupMembers);

                  // check if any members have been removed
                  if (hasRemovedMembers)
                     {
                        // if so, then process all group members with the new group wall file
                        updateGroupMembers(modifiedGroup.friendsList);
                     }
                  // otherwise no members have been removed
                  else
                     {
                        // get only the friend IDs of the members who are added from the group
                        ArrayList<String> newGroupMembers = createNewGroupMembersArrayList();

                        // process the new group members by adding the group info to their friend file
                        updateGroupMembers(newGroupMembers);

                        // update the user group with the new member list to the user group manager
                        dataManager.userGroupManager.updateUserGroup(modifiedGroup);
                     }


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
            // update the user group fragment
            fragment.updateUserGroupListView();

            // dismiss the dialog once done
            pDialog.dismiss();
         }


      /**
       * This method updates the group members specified in the group member list by creating an updated friend file
       **/
      private void updateGroupMembers(ArrayList<String> groupMemberList) throws JSONException, IOException, POSNCryptoException
         {
            // loop through all the members
            for (String friendID : groupMemberList)
               {
                  // get the friend object from the friend manager
                  Friend friend = dataManager.friendManager.getFriend(friendID);

                  // check if the user group ID already exists or not
                  if (!friend.userGroups.contains(modifiedGroup.ID))
                     {
                        // add the group ID to the friend's groups
                        friend.userGroups.add(modifiedGroup.ID);

                        // update the friend object in the friend manager
                        dataManager.friendManager.updateFriend(friend);
                     }

                  // create a new friend file with the new group
                  String fileName = friend.ID + "_friend_file.txt";
                  String deviceFilepath = Constants.friendsFilePath;
                  dataManager.createFriendFile(friend.ID, deviceFilepath, fileName);

                  // upload group wall to cloud and get direct link
                  main.cloud.uploadFileToCloud(Constants.friendDirectory, fileName, deviceFilepath + "/" + fileName);
               }
         }


      /**
       * This method removes the members of the group by creating a new friend file and archiving the old group wall
       **/
      private boolean processRemovedGroupMembers(ArrayList<String> removedGroupMembersList) throws JSONException, IOException, POSNCryptoException
         {
            if (removedGroupMembersList.size() > 0)
               {
                  // loop through all the new members
                  for (String friendID : removedGroupMembersList)
                     {
                        // get the friend object from the friend manager
                        Friend friend = dataManager.friendManager.getFriend(friendID);

                        // remove the group ID from the friend's group
                        friend.userGroups.remove(modifiedGroup.ID);

                        // update the friend object in the friend manager
                        dataManager.friendManager.updateFriend(friend);

                        // create a new friend file without the group
                        String fileName = friend.ID + "_friend_file.txt";
                        String deviceFilepath = Constants.friendsFilePath;
                        dataManager.createFriendFile(friend.ID, deviceFilepath, fileName);

                        // upload group wall to cloud and get direct link
                        main.cloud.uploadFileToCloud(Constants.friendDirectory, fileName, deviceFilepath + "/" + fileName);
                     }

                  // archive the old group wall and create a new wall file
                  archiveGroupWallFile();

                  return true;
               }
            else
               {
                  return false;
               }
         }


      /**
       * This method handles the archive process by creating a new wall file thats encrypted with a new key and moving the old wall into the archive folder in the cloud
       **/
      private void archiveGroupWallFile() throws IOException, JSONException, POSNCryptoException
         {
            // create the archive file (its just a wall file, except with a different file name and cloud location)
            String fileName = "archive_" + modifiedGroup.name + "_" + modifiedGroup.version + ".txt";
            String deviceFilepath = Constants.archiveFilePath;
            dataManager.createGroupWallFile(modifiedGroup.ID, deviceFilepath, fileName);

            // set the new archive link to uploaded file link
            modifiedGroup.archiveFileLink = main.cloud.uploadFileToCloud(Constants.archiveDirectory, fileName, deviceFilepath + "/" + fileName);

            // remove the old wall file in the cloud
            fileName = "group_" + modifiedGroup.name + "_" + modifiedGroup.version + ".txt";
            main.cloud.removeFileOnCloud(Constants.wallDirectory, fileName);

            // increment the version number
            modifiedGroup.version = modifiedGroup.version + 1;

            // set the new archive key to the old wall file key
            modifiedGroup.archiveFileKey = modifiedGroup.groupFileKey;

            // create a new key for the group
            modifiedGroup.groupFileKey = SymmetricKeyHelper.createRandomKey();

            // update the group in the data manager
            dataManager.userGroupManager.updateUserGroup(modifiedGroup);

            // create a new group wall file with the updated information
            fileName = "group_" + modifiedGroup.name + "_" + modifiedGroup.version + ".txt";
            deviceFilepath = Constants.wallFilePath;
            dataManager.createGroupWallFile(modifiedGroup.ID, deviceFilepath, fileName);

            // upload the updated group wall file to the cloud
            modifiedGroup.groupFileLink = main.cloud.uploadFileToCloud(Constants.wallDirectory, fileName, deviceFilepath + "/" + fileName);

            // update the group in the data manager so the new link is added
            dataManager.userGroupManager.updateUserGroup(modifiedGroup);
         }


      /**
       * This method creates a list of members who have been removed from the group
       **/
      private ArrayList<String> createRemovedGroupMembersArrayList()
         {
            // get the original and updated member lists
            ArrayList<String> originalMemberList = dataManager.userGroupManager.getUserGroup(modifiedGroup.ID).friendsList;
            ArrayList<String> updatedMemberList = modifiedGroup.friendsList;

            // create a new list to hold the removed members
            ArrayList<String> removedMembers = new ArrayList<>();

            // loop through all the original members and check if they are in the updated list or not
            for (int i = 0; i < originalMemberList.size(); i++)
               {
                  String friendID = originalMemberList.get(i);

                  // check if an original member is part of the updated list
                  if (!updatedMemberList.contains(friendID))
                     {
                        // Not there, so add to removed member list
                        removedMembers.add(friendID);
                     }
               }

            return removedMembers;
         }


      /**
       * This method creates a list of members who have been added to the group
       **/
      private ArrayList<String> createNewGroupMembersArrayList()
         {
            // get the original and updated member lists
            ArrayList<String> originalMemberList = dataManager.userGroupManager.getUserGroup(modifiedGroup.ID).friendsList;
            ArrayList<String> updatedMemberList = modifiedGroup.friendsList;

            // create a new list to hold the new members
            ArrayList<String> newMembers = new ArrayList<>();

            // loop through all the updated members and check if they are in the original list or not
            for (int i = 0; i < updatedMemberList.size(); i++)
               {
                  String friendID = updatedMemberList.get(i);

                  // check if an updated member is part of the original list
                  if (!originalMemberList.contains(friendID))
                     {
                        // Not there, so add to new member list
                        newMembers.add(friendID);
                     }
               }

            return newMembers;
         }
   }