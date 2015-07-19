package com.posn.asynctasks.wall;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.posn.datatypes.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class LoadWallPostsAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;

      private Context context;
      private String filePath;

      private ArrayList<Post> wallPostsData = new ArrayList<>();
      //private ArrayList<Friend> friendRequestsList = new ArrayList<>();

      public AsyncResponseWall delegate = null;


      public LoadWallPostsAsyncTask(Context context, String filePath)
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
            pDialog.setMessage("Loading Wall Data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }


      // Checking login in background
      protected String doInBackground(String... params)
         {
            System.out.println("GETTING WALL POSTS!!!");

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

                  JSONArray wallPosts = data.getJSONArray("posts");

                  for (int n = 0; n < wallPosts.length(); n++)
                     {
                        Post post = new Post();
                        post.parseJOSNObject(wallPosts.getJSONObject(n));

                        wallPostsData.add(post);
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
            delegate.loadingWallFinished(wallPostsData);

            //adapter.notifyDataSetChanged();

            // dismiss the dialog once done
            pDialog.dismiss();
         }
   }