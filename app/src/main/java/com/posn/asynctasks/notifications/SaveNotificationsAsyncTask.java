package com.posn.asynctasks.notifications;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.posn.datatypes.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class SaveNotificationsAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;

      private Context context;
      private String filePath;

      private ArrayList<Notification> notificationData;


      public SaveNotificationsAsyncTask(Context context, String filePath, ArrayList<Notification> notificationData)
         {
            super();
            this.context = context;
            this.filePath = filePath;

            this.notificationData = notificationData;
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
            System.out.println("SAVING NOTIFICATIONS!!!");

            JSONArray notificationList = new JSONArray();

            try
               {
                  for(int i = 0; i < notificationData.size(); i++)
                     {
                        Notification notification = notificationData.get(i);
                        notificationList.put(notification.createJOSNObject());
                     }

                  JSONObject object = new JSONObject();
                  object.put("notifications", notificationList);

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