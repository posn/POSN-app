package com.posn.datatypes;

import com.posn.Constants;
import com.posn.utility.IDGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Post
   {
      // data variables
      public int type;
      public String postID;
      public String friendID;
      public String date;
      public String textContent = null;

      public String multimediaLink = null;
      public String multimediaKey = null;

      // user interface variables
      public boolean selected;

      public Post()
         {
         }


      public Post(int type, String friendID, String textContent)
         {
            // create POST ID
            postID = IDGenerator.generate(this.friendID);

            // create post date
            Date currentDate = new Date();
            SimpleDateFormat dateformatDay = new SimpleDateFormat("MMM dd", Locale.US);
            SimpleDateFormat dateformatTime = new SimpleDateFormat("h:mmaa", Locale.US);

            this.date = dateformatDay.format(currentDate) + " at " + dateformatTime.format(currentDate).toLowerCase();

            this.type = type;
            this.friendID = friendID;
            this.textContent = textContent;
         }

      public Post(int type, String friendID, String multimediaKey, String multimediaLink)
         {
            // create POST ID
            postID = IDGenerator.generate(this.friendID);

            // create post date
            Date currentDate = new Date();
            SimpleDateFormat dateformatDay = new SimpleDateFormat("MMM dd", Locale.US);
            SimpleDateFormat dateformatTime = new SimpleDateFormat("h:mmaa", Locale.US);

            this.date = dateformatDay.format(currentDate) + " at " + dateformatTime.format(currentDate).toLowerCase();

            this.type = type;
            this.friendID = friendID;
            this.multimediaKey = multimediaKey;
            this.multimediaLink = multimediaLink;
         }

      public JSONObject createJSONObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("type", type);
                  obj.put("postID", postID);
                  obj.put("friendID", friendID);
                  obj.put("date", date);

                  // store the content based on the content type
                  if (type == Constants.POST_TYPE_STATUS)
                     {
                        obj.put("textContent", textContent);
                     }
                  else
                     {
                        obj.put("multimediaKey", multimediaKey);
                        obj.put("multimediaLink", multimediaLink);
                     }

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
                  postID = obj.getString("postID");
                  friendID = obj.getString("friendID");
                  date = obj.getString("date");

                  if (type == Constants.POST_TYPE_STATUS)
                     {
                        textContent = obj.getString("textContent");
                     }
                  else
                     {
                        multimediaKey = obj.getString("multimediaKey");
                        multimediaLink = obj.getString("multimediaLink");
                     }
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }
   }