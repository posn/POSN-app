package com.posn.asynctasks.messages;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.posn.datatypes.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class SaveMessagesAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;

      private Context context;
      private String filePath;

      private ArrayList<Message> messageList;


      public SaveMessagesAsyncTask(Context context, String filePath, ArrayList<Message> messageData)
         {
            super();
            this.context = context;
            this.filePath = filePath;

            this.messageList = messageData;
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
            System.out.println("SAVING MESSAGES!!!");

            JSONArray messagesList = new JSONArray();

            try
               {
                  for (int i = 0; i < messageList.size(); i++)
                     {
                        Message message = messageList.get(i);
                        messagesList.put(message.createJOSNObject());
                     }

                  JSONObject object = new JSONObject();
                  object.put("messages", messagesList);

                  String jsonStr = object.toString();

                  FileWriter fw = new FileWriter(filePath);
                  BufferedWriter bw = new BufferedWriter(fw);
                  bw.write(jsonStr);
                  bw.close();

               }
            catch (JSONException | IOException e)
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