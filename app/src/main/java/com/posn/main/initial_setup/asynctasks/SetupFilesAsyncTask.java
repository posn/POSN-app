package com.posn.main.initial_setup.asynctasks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.posn.constants.Constants;
import com.posn.datatypes.UserGroup;
import com.posn.exceptions.POSNCryptoException;
import com.posn.main.initial_setup.SetupGroupsActivity;
import com.posn.managers.AppDataManager;
import com.posn.utility.SymmetricKeyHelper;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;


/**
 * This AsyncTask class implements the functionality to create the initial application data files when the user signs up
 * <ul><li>Creates the group wall files and uploads them to the cloud
 * <li>Creates the application files for: main wall, user defined groups, user, notifications, conversations, and friends</ul>
 **/
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
                  String deviceFileKey = SymmetricKeyHelper.createKeyFromString(activity.password);

                  AppDataManager dataManager = new AppDataManager(activity.userManager, deviceFileKey);

                  // loop and create all the new groups
                  for (int i = 0; i < groupList.size(); i++)
                     {
                        // create a new user group in the data manager
                        UserGroup group = dataManager.userGroupManager.createNewUserGroup(groupList.get(i));

                        // create the group wall file on the device
                        String fileName = "group_" + group.name + "_" + group.version + ".txt";
                        String directory = Constants.wallFilePath;
                        dataManager.createGroupWallFile(group.ID, directory, fileName);

                        // upload group wall to cloud and get direct link
                        group.groupFileLink = activity.cloud.uploadFileToCloud(Constants.wallDirectory, fileName, Constants.wallFilePath + "/" + fileName);

                        // create new group object and add to group list
                        dataManager.userGroupManager.updateUserGroup(group);
                     }

                  // save the user group list to a file
                  dataManager.saveUserGroupListAppFile();

                  // save the user data to a file
                  dataManager.saveUserAppFile();

                  // save the friend list file
                  dataManager.saveFriendListAppFile(false);

                  // save the wall post file
                  dataManager.saveWallPostListAppFile();

                  // save the notifications file
                  dataManager.saveNotificationListAppFile();

                  // save the messages file
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