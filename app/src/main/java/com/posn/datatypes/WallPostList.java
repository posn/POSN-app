package com.posn.datatypes;

import android.os.AsyncTask;

import com.posn.utility.DeviceFileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class WallPostList
   {
      public HashMap<String, Post> wallPosts;

      public WallPostList()
         {
            wallPosts = new HashMap<>();
         }

      public void loadWallPostsFromFile(String fileName)
         {
            wallPosts.clear();

            try
               {
                  JSONObject data = DeviceFileManager.loadJSONObjectFromFile(fileName);

                  JSONArray wallPostsArray = data.getJSONArray("posts");

                  for (int n = 0; n < wallPostsArray.length(); n++)
                     {
                        Post post = new Post();
                        post.parseJSONObject(wallPostsArray.getJSONObject(n));

                        wallPosts.put(post.postID, post);
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
                  for (Map.Entry<String, Post> entry : wallPosts.entrySet())
                     {
                        Post post = entry.getValue();
                        wallPostList.put(post.createJSONObject());
                     }

                  JSONObject object = new JSONObject();
                  object.put("posts", wallPostList);

                  DeviceFileManager.writeJSONToFile(object, devicePath);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }
   }
