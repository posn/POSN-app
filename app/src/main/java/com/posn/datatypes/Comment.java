package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Comment implements Parcelable
   {
      public String commentID;
      public String postID;
      public String name;
      public String date;
      public String comment;

      public Comment(){}

      public Comment(String name, String date, String comment)
         {
            this.name = name;
            this.date = date;
            this.comment = comment;
         }


      public JSONObject createJSONObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("commentID", commentID);
                  obj.put("name", name);
                  obj.put("date", date);
                  obj.put("comment", comment);
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
                  commentID = obj.getString("commentID");
                  name = obj.getString("name");
                  date = obj.getString("date");
                  comment = obj.getString("comment");
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      @Override
      public boolean equals(Object o)
         {
            if (!(o instanceof Friend))
               {
                  return false;
               }
            Comment other = (Comment) o;
            return name.equalsIgnoreCase(other.name);
         }

      // Parcelling part
      public Comment(Parcel in)
         {
            this.commentID = in.readString();
            this.name = in.readString();
            this.date = in.readString();
            this.comment = in.readString();
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeString(this.commentID);
            dest.writeString(this.name);
            dest.writeString(this.date);
            dest.writeString(this.comment);
         }

      public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>()
         {
            public Comment createFromParcel(Parcel in)
               {
                  return new Comment(in);
               }

            public Comment[] newArray(int size)
               {
                  return new Comment[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }

   }
