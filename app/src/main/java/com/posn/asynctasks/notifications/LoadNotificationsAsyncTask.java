package com.posn.asynctasks.notifications;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.posn.datatypes.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class LoadNotificationsAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;

      private Context context;
      private String filePath;

      private ArrayList<Notification> notificationData = new ArrayList<>();

      public AsyncResponseNotifications delegate = null;


      public LoadNotificationsAsyncTask(Context context, String filePath)
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
            System.out.println("GETTING NOTIFICATIONS!!!");

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

                  JSONArray notifications = data.getJSONArray("notifications");

                  for (int n = 0; n < notifications.length(); n++)
                     {
                        Notification notification = new Notification();
                        notification.parseJOSNObject(notifications.getJSONObject(n));

                        notificationData.add(notification);
                     }
               }
            catch (IOException | JSONException e)
               {
                  e.printStackTrace();
               }

            return null;
         }


      // After completing background task Dismiss the progress dialog
      protected void onPostExecute(String file_url)
         {
            delegate.loadingNotificationsFinished(notificationData);

            // dismiss the dialog once done
            pDialog.dismiss();
         }
   }