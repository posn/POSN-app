package com.posn.datatypes;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * <p>This class represents a chat message (Note: not fully implemented). Includes methods to create and parse wall post objects in a JSON format</p>
 * <p>Implements parcelable to easily pass wall posts between activities</p>
 **/
public class Message
   {

      public int type;
      public Date date;
      public String message;

      public Message()
         {

         }

      public Message(int type, Date date, String comment)
         {
            super();
            this.type = type;
            this.date = date;
            this.message = comment;
         }


      public JSONObject createJSONObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("type", type);

                  SimpleDateFormat dateformat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa", Locale.US);
                  String datetime  = dateformat.format(date);
                  obj.put("date", datetime);

                  obj.put("message", message);
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

                  String datetime = obj.getString("date");
                  SimpleDateFormat dateformat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa", Locale.US);
                  date  = dateformat.parse(datetime);

                  message = obj.getString("message");
               }
            catch (JSONException | ParseException e)
               {
                  e.printStackTrace();
               }
         }

      public String getHeaderDateString()
         {
            SimpleDateFormat dateformat = new SimpleDateFormat("E, MM/dd/yyyy", Locale.US);
            return dateformat.format(date);
         }

      public String getKeyDateString()
         {
            SimpleDateFormat dateformat = new SimpleDateFormat("MMddyyyy", Locale.US);
            return dateformat.format(date);
         }

      public String getTimeString()
         {
            SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm aa", Locale.US);
            return dateformat.format(date);
         }


   }
