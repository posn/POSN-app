package com.posn.utility;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CloudFileManager
   {
      public static void createGroupWallFile(String devicePath, String encryptKey)
         {
            JSONObject object = new JSONObject();

            try
               {
                  object.put("version", 0);
                  object.put("archive_link",JSONObject.NULL);
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


   }
