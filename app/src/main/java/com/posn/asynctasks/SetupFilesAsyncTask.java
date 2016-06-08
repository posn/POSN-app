package com.posn.asynctasks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.datatypes.UserGroup;
import com.posn.exceptions.POSNCryptoException;
import com.posn.initial_setup.SetupGroupsActivity;
import com.posn.utility.POSNDataManager;
import com.posn.utility.SymmetricKeyManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;


public class SetupFilesAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;

      private SetupGroupsActivity activity;
      private ArrayList<String> groupList;
      private Intent intent;


      public SetupFilesAsyncTask(SetupGroupsActivity act, ArrayList<String> groups, Intent i)
         {
            super();
            activity = act;
            groupList = groups;
            intent = i;
         }


      // Before starting background thread Show Progress Dialog
      @Override
      protected void onPreExecute()
         {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Creating Groups and Files...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }


      // Checking login in background
      protected String doInBackground(String... params)
         {
            try
               {
                  // generate symmetric key from user password
                  String deviceFileKey = SymmetricKeyManager.createKeyFromString(activity.password);

                  POSNDataManager dataManager = new POSNDataManager(activity.user, deviceFileKey);

                  // loop and create all the new groups
                  for (int i = 0; i < groupList.size(); i++)
                     {
                        UserGroup group = dataManager.userGroupList.createNewUserGroup(groupList.get(i));

                        String fileName = "group_" + group.name + "_" + group.version + ".txt";
                        String directory = Constants.wallFilePath;

                        dataManager.createGroupWallFile(group.ID, directory, fileName);

                        // upload group wall to cloud and get direct link
                        group.groupFileLink = activity.cloud.uploadFileToCloud(Constants.wallDirectory, fileName, Constants.wallFilePath + "/" + fileName);

                        // create new group object and add to group list
                        dataManager.userGroupList.updateUserGroup(group);
                     }

                  // save the user group list to a file
                  dataManager.saveUserGroupListAppFile();

                  // save the user data to a file
                  dataManager.saveUserAppFile();

                  // get the friend list file
                  dataManager.saveFriendListAppFile();

                  // get the wall post file
                  dataManager.saveWallPostListAppFile();

                  // get the notifications file
                  dataManager.saveNotificationListAppFile();

                  // get the messages file
                  dataManager.saveConversationListAppFile();
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

            // start new activity
            activity.startActivity(intent);
         }


   }