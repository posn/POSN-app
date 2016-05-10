package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import com.posn.utility.IDGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Comment implements Parcelable
   {
      public String commentID;
      public String postID;
      public String userID;
      public String date;
      public String comment;

      public Comment(){}

      public Comment(String userID, String postID, String comment)
         {
            this.userID = userID;

            this.postID = postID;

            // create comment ID
            this.commentID = IDGenerator.generate(userID + postID);

            // create post date
            Date currentDate = new Date();
            SimpleDateFormat dateformatDay = new SimpleDateFormat("MMM dd 'at' h:mmaa", Locale.US);
            this.date = dateformatDay.format(currentDate);

            this.comment = comment;
         }


      public JSONObject createJSONObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("commentID", commentID);
                  obj.put("userID", userID);
                  obj.put("postID", postID);
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
                  userID = obj.getString("userID");
                  postID = obj.getString("postID");
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
            return commentID.equalsIgnoreCase(other.commentID);
         }

      // Parcelling part
      public Comment(Parcel in)
         {
            this.commentID = in.readString();
            this.userID = in.readString();
            this.postID = in.readString();
            this.date = in.readString();
            this.comment = in.readString();
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeString(this.commentID);
            dest.writeString(this.userID);
            dest.writeString(this.postID);
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
