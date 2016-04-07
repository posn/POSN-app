package com.posn.asynctasks.conversations;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.posn.datatypes.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class LoadMessagesAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;

      private Context context;
      private String filePath;

      private HashMap<String,ArrayList<Message>> messagesList = new HashMap<>();

      public AsyncResponseConversation delegate = null;


      public LoadMessagesAsyncTask(Context context, String filePath)
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
            pDialog.setMessage("Loading Conversation...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }


      // Checking login in background
      protected String doInBackground(String... params)
         {
            System.out.println("GETTING CONVERSATION!!!");

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

                  JSONArray messageList = data.getJSONArray("conversations");

                  for (int n = 0; n < messageList.length(); n++)
                     {
                        Message message = new Message();
                        message.parseJSONObject(messageList.getJSONObject(n));

                        String key = message.getKeyDateString();

                        if(messagesList.containsKey(key))
                           {
                              messagesList.get(key).add(message);
                           }
                        else
                           {
                              ArrayList<Message> conversation = new ArrayList<>();
                              conversation.add(message);
                              messagesList.put(key, conversation);
                           }
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
            //System.out.println("NUM FRIENDS123: " + friendList.size() + " | " + friendRequestsList.size());

            delegate.loadingConversationFinished(messagesList);


            // dismiss the dialog once done
            pDialog.dismiss();
         }
   }