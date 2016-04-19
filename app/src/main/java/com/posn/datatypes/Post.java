package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import com.posn.Constants;
import com.posn.utility.IDGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Post implements Parcelable
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
            System.out.println("DATE: " + currentDate.toString());

            SimpleDateFormat dateformatDay = new SimpleDateFormat("MMM dd 'at' h:mmaa", Locale.US);
            this.date = dateformatDay.format(currentDate);

            this.type = type;
            this.friendID = friendID;
            this.textContent = textContent;
         }

      public Post(int type, String friendID)
         {
            // create POST ID
            postID = IDGenerator.generate(this.friendID);

            // create post date
            Date currentDate = new Date();
            SimpleDateFormat dateformatDay = new SimpleDateFormat("MMM dd 'at' h:mmaa", Locale.US);
            this.date = dateformatDay.format(currentDate);

            this.type = type;
            this.friendID = friendID;
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

      // Parcelling part
      public Post(Parcel in)
         {
            this.type = in.readInt();
            this.postID = in.readString();
            this.friendID = in.readString();
            this.date = in.readString();

            if (type == Constants.POST_TYPE_STATUS)
               {
                  this.textContent = in.readString();
               }
            else
               {
                  this.multimediaKey = in.readString();
                  this.multimediaLink = in.readString();
               }
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeInt(this.type);
            dest.writeString(this.postID);
            dest.writeString(this.friendID);
            dest.writeString(this.date);

            if (type == Constants.POST_TYPE_STATUS)
               {
                  dest.writeString(this.textContent);
               }
            else
               {
                  dest.writeString(this.multimediaKey);
                  dest.writeString(this.multimediaLink);
               }
         }

      public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>()
         {
            public Post createFromParcel(Parcel in)
               {
                  return new Post(in);
               }

            public Post[] newArray(int size)
               {
                  return new Post[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }