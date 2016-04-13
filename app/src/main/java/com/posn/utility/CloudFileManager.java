package com.posn.utility;


import com.posn.datatypes.Group;
import com.posn.datatypes.RequestedFriend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CloudFileManager
   {
      public static void createGroupWallFile(String devicePath, String encryptKey)
         {
            JSONObject object = new JSONObject();

            try
               {
                  object.put("version", 0);
                  object.put("archive_link", JSONObject.NULL);
                  object.put("archive_key", JSONObject.NULL);

                  JSONArray postList = new JSONArray();

                  object.put("posts", postList);

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

      public static void createFriendFile(RequestedFriend requestedFriend, HashMap<String, Group> groups, String path)
         {
            JSONObject obj = new JSONObject();
            JSONArray groupList = new JSONArray();

            try
               {
                  for (int i = 0; i < requestedFriend.groups.size(); i++)
                     {
                        Group group = groups.get(requestedFriend.groups.get(i));
                        groupList.put(group.createFriendFileJSONObject());
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
