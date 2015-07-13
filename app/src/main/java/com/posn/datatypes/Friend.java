package com.posn.datatypes;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

public class Friend
   {
      public String id;
      public String name;

      public String phone;
      public String email;
      public String image_uri;
      public Bitmap bitmap;
      public boolean selected;
      public int status;

      public Friend()
         {
            selected = false;
         }

      public Friend(String name)
         {
            this.name = name;
            selected = false;
         }

      public Friend(String name, int status)
         {
            this.name = name;
            this.status = status;
            selected = false;
         }

      public JSONObject createJOSNObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("id", id);
                  obj.put("name", name);
                  obj.put("phone", phone);
                  obj.put("email", email);
                  obj.put("image_uri", image_uri);
                  obj.put("status", status);
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
                  id = obj.getString("id");
                  name = obj.getString("name");
                  phone = obj.getString("phone");
                  email = obj.getString("email");
                  image_uri = obj.getString("image_uri");
                  status = obj.getInt("status");
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }
   }