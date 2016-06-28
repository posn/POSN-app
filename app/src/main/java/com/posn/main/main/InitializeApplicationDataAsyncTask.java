package com.posn.main.main;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.exceptions.POSNCryptoException;
import com.posn.main.main.MainActivity;
import com.posn.main.main.wall.asynctasks.GetFriendContentAsyncTask;
import com.posn.managers.AppDataManager;

import org.json.JSONException;

import java.io.IOException;


public class InitializeApplicationDataAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;
      private MainActivity main;
      private AppDataManager dataManager;


      public InitializeApplicationDataAsyncTask(MainActivity activity)
         {
            super();
            main = activity;
            dataManager = main.dataManager;
         }


      // Before starting background thread Show Progress Dialog
      @Override
      protected void onPreExecute()
         {
            super.onPreExecute();
            pDialog = new ProgressDialog(main);
            pDialog.setMessage("Loading Application Data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }


      // Checking login in background
      protected String doInBackground(String... params)
         {
            try
               {
                  // get the friendID list file
                  System.out.println("GETTING FRIENDS!!!");
                  dataManager.loadFriendListAppFile();

                  // get the user groups from file
                  System.out.println("GETTING USER GROUPS!!!");
                  dataManager.loadUserGroupListAppFile();


                  // get the wall post file
                  System.out.println("GETTING WALL POSTS!!!");
                  dataManager.loadWallPostListAppFile();

                  // get the notifications file
                  System.out.println("GETTING NOTIFICATIONS!!!");
                  dataManager.loadNotificationListAppFile();

                  // get the messages file
                  System.out.println("GETTING MESSAGES!!!");
                  dataManager.loadConversationListAppFile();

                  // check/create cloud storage directories
                  main.cloud.createStorageDirectoriesOnCloud();
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
            new GetFriendContentAsyncTask(main).execute();

            // dismiss the dialog once done
            pDialog.dismiss();
         }


   }