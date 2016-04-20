package com.posn.utility;


import com.posn.datatypes.Friend;
import com.posn.datatypes.FriendGroup;
import com.posn.datatypes.Post;
import com.posn.datatypes.User;
import com.posn.datatypes.UserGroup;
import com.posn.encryption.AsymmetricKeyManager;
import com.posn.encryption.SymmetricKeyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
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

      public static ArrayList<Post> loadGroupWallFile(String devicePath)
         {
            DeviceFileManager.loadJSONObjectFromFile(devicePath);
            ArrayList<Post> postArrayList = new ArrayList<>();
            try
               {
                  JSONObject object = DeviceFileManager.loadJSONObjectFromFile(devicePath);


                  JSONArray postList = object.getJSONArray("posts");

                  for (int i = 0; i < postList.length(); i++)
                     {
                        Post post = new Post();
                        post.parseJSONObject(postList.getJSONObject(i));

                        postArrayList.add(post);
                     }

                  return postArrayList;
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
            return null;
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

      public static boolean loadTemporalFriendFile(User user, Friend friend, String devicePath)
         {
            // read friend file in
            JSONObject object = DeviceFileManager.loadJSONObjectFromFile(devicePath);


            try
               {
                  String URI = object.getString("uri");

                  if (URI != null && URI != JSONObject.NULL)
                     {
                        // parse temporal file
                        String[] paths = URI.split("/");

                        String encryptedSymmetricKey = paths[0];

                        // decrypt symmetric key
                        String key = AsymmetricKeyManager.decrypt(user.privateKey, encryptedSymmetricKey);

                        // decrypt URI data
                        String encryptedURI = paths[1];

                        URI = SymmetricKeyManager.decrypt(key, encryptedURI);
                        paths = URI.split("/");

                        // get file link
                        friend.friendFileLink = URLDecoder.decode(paths[1], "UTF-8");

                        friend.friendFileKey = URLDecoder.decode(paths[2], "UTF-8");

                        // get nonces
                        String nonce = paths[3];

                        return true;
                     }
               }
            catch (JSONException | IOException e)
               {
                  e.printStackTrace();
               }
            return false;
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
