package com.posn.asynctasks.unused;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.posn.datatypes.Friend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SaveFriendsListAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;

      private Context context;
      private String filePath;

      private HashMap<String, Friend> friendList;
      private ArrayList<Friend> friendRequestsList;


      public SaveFriendsListAsyncTask(Context context, String filePath, HashMap<String, Friend> friendList, ArrayList<Friend> friendRequestsList)
         {
            super();
            this.context = context;
            this.filePath = filePath;

            this.friendList = friendList;
            this.friendRequestsList = friendRequestsList;
         }


      // Before starting background thread Show Progress Dialog
      @Override
      protected void onPreExecute()
         {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Saving Data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }


      // Checking login in background
      protected String doInBackground(String... params)
         {
            System.out.println("SAVING FRIENDS!!!");

            File wallFile = new File(filePath);

            String line, fileContents;

            JSONArray friendsList = new JSONArray();

            try
               {
                  for (Map.Entry<String, Friend> entry : friendList.entrySet())
                     {
                        Friend friend = entry.getValue();
                        friendsList.put(friend.createJSONObject());                     }

                  for(int i = 0; i < friendRequestsList.size(); i++)
                     {
                        Friend friend = friendRequestsList.get(i);
                        friendsList.put(friend.createJSONObject());
                     }

                  JSONObject object = new JSONObject();
                  object.put("friends", friendsList);

                  String jsonStr = object.toString();

                  FileWriter fw = new FileWriter(filePath);
                  BufferedWriter bw = new BufferedWriter(fw);
                  bw.write(jsonStr);
                  bw.close();

               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
            catch (IOException e)
               {
                  e.printStackTrace();
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