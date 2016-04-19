package com.posn.utility;


import com.posn.datatypes.Post;
import com.posn.datatypes.RequestedFriend;
import com.posn.datatypes.User;
import com.posn.datatypes.UserGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CloudFileManager
   {
      public static void createGroupWallFile(UserGroup group, HashMap<String, Post> posts, String devicePath)
         {
            JSONObject object = new JSONObject();

            try
               {
                  object.put("version", group.version);
                  object.put("archive_link", JSONObject.NULL);
                  object.put("archive_key", JSONObject.NULL);

                  JSONArray postList = new JSONArray();

                  for (int i = 0; i < group.wallPostList.size(); i++)
                     {
                        Post post = posts.get(group.wallPostList.get(i));
                        postList.put(post.createJSONObject());
                     }

                  object.put("posts", postList);

                  // need to encrypt data here

                  DeviceFileManager.writeJSONToFile(object, devicePath);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      public static void createTemporalFriendFile(String uri, String devicePath)
         {
            JSONObject object = new JSONObject();

            try
               {
                  if (uri != null)
                     object.put("uri", uri);
                  else
                     object.put("uri", JSONObject.NULL);

                  DeviceFileManager.writeJSONToFile(object, devicePath);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      public static void createFriendFile(User user, RequestedFriend requestedUser, String path)
         {
            JSONObject obj = new JSONObject();
            JSONArray groupList = new JSONArray();

            // need to add user file link and key
            try
               {
                  for (int i = 0; i < requestedUser.groups.size(); i++)
                     {
                        UserGroup userGroup = user.userDefinedGroups.get(requestedUser.groups.get(i));
                        groupList.put(userGroup.createFriendFileJSONObject());
                     }
                  obj.put("groups", groupList);
                  DeviceFileManager.writeJSONToFile(obj, path);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }

         }


   }
