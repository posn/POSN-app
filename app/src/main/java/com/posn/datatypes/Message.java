package com.posn.datatypes;

import org.json.JSONException;
import org.json.JSONObject;

public class Message
   {
      public int type;

      public String friend;
      public String date;
      public String email;
      public String image_uri;
      public String content;
      public String lastMessage;

      public boolean selected;

      public Message()
         {
         }


      public Message(String friend, String date, String lastMessage)
         {
            this.friend = friend;
            this.date = date;
            this.lastMessage = lastMessage;
         }

      public JSONObject createJOSNObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("friend", friend);
                  obj.put("date", date);
                  obj.put("lastMessage", lastMessage);
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
                  friend = obj.getString("friend");
                  date = obj.getString("date");
                  lastMessage = obj.getString("lastMessage");
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }
   }