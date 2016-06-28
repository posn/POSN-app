package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import com.posn.utility.IDGeneratorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * <p>This class represents a comment the the user or friends have made on a post. Includes methods to create and parse wall post objects in a JSON format</p>
 * <p>Implements parcelable to easily pass wall posts between activities</p>
 **/
public class Comment implements Parcelable
   {
      public String commentID;
      public String postID;
      public String userID;
      public String date;
      public String commentText;

      public Comment(){}

      public Comment(String userID, String postID, String commentText)
         {
            this.userID = userID;

            this.postID = postID;

            // create comment ID
            this.commentID = IDGeneratorHelper.generate(userID + postID);

            // create post date
            Date currentDate = new Date();
            SimpleDateFormat dateformatDay = new SimpleDateFormat("MMM dd 'at' h:mmaa", Locale.US);
            this.date = dateformatDay.format(currentDate);

            this.commentText = commentText;
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
                  obj.put("comment", commentText);
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
                  commentText = obj.getString("comment");
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
            this.commentText = in.readString();
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeString(this.commentID);
            dest.writeString(this.userID);
            dest.writeString(this.postID);
            dest.writeString(this.date);
            dest.writeString(this.commentText);
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
