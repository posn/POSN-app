package com.posn.datatypes;

import org.json.JSONException;
import org.json.JSONObject;

public class Post
   {
      public int type;

      public String friend;
      public String date;
      public String image_uri;
      public String content;

      public boolean selected;

      public Post()
         {
         }


      public Post(int type, String friend, String date, String content)
         {
            this.type = type;
            this.friend = friend;
            this.date = date;
            this.content = content;
         }

      public JSONObject createJOSNObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("type", type);
                  obj.put("friend", friend);
                  obj.put("date", date);
                  obj.put("content", content);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }

            return obj;
         }

      public void parseJOSNObject(JSONObject obj)
         {
            try
               {
                  type = obj.getInt("type");
                  friend = obj.getString("friend");
                  date = obj.getString("date");
                  content = obj.getString("content");
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }
   }