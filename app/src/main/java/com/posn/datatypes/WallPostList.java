package com.posn.datatypes;

import android.os.AsyncTask;

import com.posn.utility.FileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class WallPostList
   {
      public ArrayList<Post> wallPosts;

      public WallPostList()
         {
            wallPosts = new ArrayList<>();
         }

      public void loadWallPostsFromFile(String fileName)
         {
            try
               {
                  JSONObject data = FileManager.loadJSONObjectFromFile(fileName);

                  JSONArray wallPostsArray = data.getJSONArray("posts");

                  for (int n = 0; n < wallPostsArray.length(); n++)
                     {
                        Post post = new Post();
                        post.parseJSONObject(wallPostsArray.getJSONObject(n));

                        wallPosts.add(post);
                     }
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      public void saveWallPostsToFileAsyncTask(final String devicePath)
      {
         new AsyncTask<Void, Void, Void>()
         {
            protected Void doInBackground(Void... params)
               {
                  saveWallPostsToFile(devicePath);
                  return null;
               }
         }.execute();
      }

      public void saveWallPostsToFile(String devicePath)
         {
            JSONArray wallPostList = new JSONArray();

            try
               {
                  for(int i = 0; i < wallPosts.size(); i++)
                     {
                        Post post = wallPosts.get(i);
                        wallPostList.put(post.createJSONObject());
                     }

                  JSONObject object = new JSONObject();
                  object.put("posts", wallPostList);

                  FileManager.writeJSONToFile(object, devicePath);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }
   }
