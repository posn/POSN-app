package com.posn.utility;


import com.posn.datatypes.Friend;
import com.posn.datatypes.FriendGroup;
import com.posn.datatypes.WallPost;
import com.posn.datatypes.User;
import com.posn.datatypes.UserGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

public class CloudFileManager
   {
      public static void createGroupWallFile(UserGroup group, HashMap<String, WallPost> posts, String deviceDirectory, String fileName)
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
                        WallPost wallPost = posts.get(group.wallPostList.get(i));
                        postList.put(wallPost.createJSONObject());
                     }

                  object.put("posts", postList);

                  // need to encrypt data here
                  String jsonString = object.toString();
                  String encryptedData = SymmetricKeyManager.encrypt(group.groupFileKey, jsonString);

                  DeviceFileManager.writeStringToFile(encryptedData, deviceDirectory + "/" + fileName);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      public static ArrayList<WallPost> fetchAndLoadGroupWallFile(FriendGroup group, String deviceDirectory, String fileName)
         {
            ArrayList<WallPost> wallPostArrayList = new ArrayList<>();

            // download the wall file from the cloud
            DeviceFileManager.downloadFileFromURL(group.groupFileLink, deviceDirectory, fileName);

            // read in the encrypted data
            String encryptedString = DeviceFileManager.loadStringFromFile(deviceDirectory + "/" + fileName);

            // decrypt the file data
            String fileData = SymmetricKeyManager.decrypt(group.groupFileKey, encryptedString);

            try
               {
                  JSONObject object = new JSONObject(fileData);

                  JSONArray postList = object.getJSONArray("posts");

                  for (int i = 0; i < postList.length(); i++)
                     {
                        WallPost wallPost = new WallPost();
                        wallPost.parseJSONObject(postList.getJSONObject(i));

                        wallPostArrayList.add(wallPost);
                     }

                  return wallPostArrayList;
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
            return null;
         }

      public static void createTemporalFriendFile(String uri, String deviceDirectory, String fileName)
         {
            JSONObject object = new JSONObject();

            try
               {
                  if (uri != null)
                     object.put("uri", uri);
                  else
                     object.put("uri", JSONObject.NULL);

                  DeviceFileManager.writeJSONToFile(object, deviceDirectory + "/" + fileName);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      public static boolean loadTemporalFriendFile(User user, Friend friend, String deviceDirectory, String fileName)
         {
            // read friend file in
            JSONObject object = DeviceFileManager.loadJSONObjectFromFile(deviceDirectory + "/" + fileName);

            try
               {
                  String URI = object.getString("uri");

                  System.out.println("URI: " + URI);

                  if (!URI.equals("null"))
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

      public static void createFriendFile(User user, Friend friend, String deviceDirectory, String fileName)
         {
            JSONObject obj = new JSONObject();
            JSONArray groupList = new JSONArray();

            // need to add user file link and key
            try
               {
                  // ADD USER FILE LINK AND KEY

                  for (int i = 0; i < friend.userGroups.size(); i++)
                     {
                        UserGroup userGroup = user.userDefinedGroups.get(friend.userGroups.get(i));
                        groupList.put(userGroup.createFriendFileJSONObject());
                     }
                  obj.put("groups", groupList);

                  String jsonString = obj.toString();
                  String encryptedString = SymmetricKeyManager.encrypt(friend.userFriendFileKey, jsonString);

                  DeviceFileManager.writeStringToFile(encryptedString, deviceDirectory + "/" + fileName);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      public static void loadFriendFile(Friend friend, String deviceDirectory, String fileName)
         {
            // read friend file in
            String encyrptedString = DeviceFileManager.loadStringFromFile(deviceDirectory + "/" + fileName);

            // decrypt string
            String friendFileData = SymmetricKeyManager.decrypt(friend.friendFileKey, encyrptedString);

            // need to add user file link and key
            try
               {
                  // GET USER FILE LINK AND KEY


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
