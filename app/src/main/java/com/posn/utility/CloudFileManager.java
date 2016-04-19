package com.posn.utility;


import com.posn.datatypes.Friend;
import com.posn.datatypes.FriendGroup;
import com.posn.datatypes.Post;
import com.posn.datatypes.User;
import com.posn.datatypes.UserGroup;
import com.posn.encryption.SymmetricKeyManager;

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

      public static void createFriendFile(User user, Friend friend, String path)
         {
            JSONObject obj = new JSONObject();
            JSONArray groupList = new JSONArray();

            // need to add user file link and key
            try
               {
                  for (int i = 0; i < friend.userGroups.size(); i++)
                     {
                        UserGroup userGroup = user.userDefinedGroups.get(friend.userGroups.get(i));
                        groupList.put(userGroup.createFriendFileJSONObject());
                     }
                  obj.put("groups", groupList);

                  String jsonString = obj.toString();
                  String encryptedString = SymmetricKeyManager.encrypt(friend.userFriendFileKey, jsonString);

                  DeviceFileManager.writeStringToFile(encryptedString, path);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      public static void loadFriendFile(Friend friend, String path)
         {
            // read friend file in
            String encyrptedString = DeviceFileManager.loadStringFromFile(path);
System.out.println("KEY!!!!!!!!!!!!!!!!!!!!!! " + friend.friendFileKey);
            // decrypt string
            String friendFileData = SymmetricKeyManager.decrypt(friend.friendFileKey, encyrptedString);

            // need to add user file link and key
            try
               {
                  JSONObject obj = new JSONObject(friendFileData);
                  JSONArray groupList = obj.getJSONArray("groups");

                  for (int i = 0; i < groupList.length(); i++)
                     {
                        FriendGroup friendGroup = new FriendGroup();

                        friendGroup.parseJSONObject(groupList.getJSONObject(i));

                        friend.friendGroups.add(friendGroup);
                     }
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }


   }
