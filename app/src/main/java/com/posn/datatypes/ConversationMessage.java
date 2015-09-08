package com.posn.datatypes;

import org.json.JSONException;
import org.json.JSONObject;

public class ConversationMessage
   {

      public int type;
      public long time;
      public String message;

      public ConversationMessage()
         {

         }

      public ConversationMessage(int type, String comment)
         {
            super();
            this.type = type;
            this.message = comment;
         }


      public JSONObject createJOSNObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("type", type);
                  obj.put("time", time);
                  obj.put("message", message);
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
                  time = obj.getLong("time");
                  message = obj.getString("message");
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }


   }
