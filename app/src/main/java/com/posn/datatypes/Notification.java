package com.posn.datatypes;

import org.json.JSONException;
import org.json.JSONObject;

public class Notification
   {
      public int type;

      public String friend;
      public String date;
      public String image_uri;

      public boolean selected;

      public Notification()
         {
         }


      public Notification(int type, String friend, String date)
         {
            this.type = type;
            this.friend = friend;
            this.date = date;
         }

      public JSONObject createJSONObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("type", type);
                  obj.put("friend", friend);
                  obj.put("date", date);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }

            return obj;
         }

      public void parseJSONObject(JSONObject obj)
         {
            try
               {
                  type = obj.getInt("type");
                  friend = obj.getString("friend");
                  date = obj.getString("date");
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }
   }