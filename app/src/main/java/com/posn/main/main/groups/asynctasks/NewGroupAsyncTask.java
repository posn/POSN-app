package com.posn.main.main.groups.asynctasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.constants.Constants;
import com.posn.datatypes.UserGroup;
import com.posn.exceptions.POSNCryptoException;
import com.posn.managers.AppDataManager;
import com.posn.main.main.MainActivity;

import org.json.JSONException;

import java.io.IOException;


/**
 * This AsyncTask class implements the functionality to create a new friendID group:
 * <ul><li>Takes in the new group name and creates a new group object in the grouplist
 * <li>The group wall file is created and uploaded to the cloud</ul>
 **/
public class NewGroupAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;

      private MainActivity main;
      private String newGroupName;
      private AppDataManager dataManager;

      public NewGroupAsyncTask(MainActivity mainActivity, String groupName)
         {
            super();
            main = mainActivity;
            newGroupName = groupName;
            dataManager = main.dataManager;
         }


      // Before starting background thread Show Progress Dialog
      @Override
      protected void onPreExecute()
         {
            super.onPreExecute();
            pDialog = new ProgressDialog(main);
            pDialog.setMessage("Creating Group...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }


      // Checking login in background
      protected String doInBackground(String... params)
         {
            try
               {
                  // create new group object
                  UserGroup group = dataManager.userGroupManager.createNewUserGroup(newGroupName);

                  // create empty group wall file on device to upload to cloud
                  String fileName = "group_" + group.name + "_" + group.version + ".txt";
                  String deviceFilepath = Constants.wallFilePath;
                  dataManager.createGroupWallFile(group.ID, deviceFilepath, fileName);

                  // upload group wall to cloud and get direct link
                  group.groupFileLink = main.cloud.uploadFileToCloud(Constants.wallDirectory, fileName, deviceFilepath + "/" + fileName);

                  // create new group object and add to group list
                  dataManager.userGroupManager.updateUserGroup(group);

                  // save group list to device
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
            // dismiss the dialog once done
            pDialog.dismiss();
         }
   }