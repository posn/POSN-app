package com.posn.asynctasks.messages;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.posn.asynctasks.friends.AsyncResponseFriends;
import com.posn.datatypes.Friend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class LoadMessagesListAsyncTask extends AsyncTask<String, String, String>
   {
      static final int STATUS_ACCEPTED = 1;
      static final int STATUS_REQUEST = 2;
      static final int STATUS_PENDING = 3;

      private ProgressDialog pDialog;

      private Context context;
      private String filePath;

      private HashMap<String, Friend> friendList = new HashMap<>();
      private ArrayList<Friend> friendRequestsList = new ArrayList<>();

      public AsyncResponseFriends delegate = null;


      public LoadMessagesListAsyncTask(Context context, String filePath)
         {
            super();
            this.context = context;
            this.filePath = filePath;
         }


      // Before starting background thread Show Progress Dialog
      @Override
      protected void onPreExecute()
         {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Loading Data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }


      // Checking login in background
      protected String doInBackground(String... params)
         {
            System.out.println("GETTING FRIENDS!!!");

            File wallFile = new File(filePath);

            String line, fileContents;

            // open the file
            try
               {
                  BufferedReader br = new BufferedReader(new FileReader(wallFile));

                  StringBuilder sb = new StringBuilder();
                  while ((line = br.readLine()) != null)
                     {
                        sb.append(line);
                     }

                  br.close();
                  fileContents = sb.toString();

                  JSONObject data = new JSONObject(fileContents);

                  JSONArray friendsList = data.getJSONArray("friends");

                  for (int n = 0; n < friendsList.length(); n++)
                     {
                        Friend friend = new Friend();
                        friend.parseJOSNObject(friendsList.getJSONObject(n));

                        if (friend.status == STATUS_ACCEPTED || friend.status == STATUS_PENDING)
                           {
                              friendList.put(friend.id, friend);
                           }
                        else
                           {
                              friendRequestsList.add(friend);
                           }
                     }
               }
            catch (FileNotFoundException e)
               {
                  e.printStackTrace();
               }
            catch (IOException e)
               {
                  e.printStackTrace();
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }

            return null;
         }


      // After completing background task Dismiss the progress dialog
      protected void onPostExecute(String file_url)
         {
            System.out.println("NUM FRIENDS123: " + friendList.size() + " | " + friendRequestsList.size());

            delegate.loadingFriendsFinished(friendList, friendRequestsList);


            // dismiss the dialog once done
            pDialog.dismiss();
         }
   }