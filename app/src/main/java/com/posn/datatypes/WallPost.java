package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import com.posn.Constants;
import com.posn.utility.IDGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * <p>This class represents a wall post. Includes methods to create and parse wall post objects in a JSON format</p>
 * <p>Implements parcelable to easily pass wall posts between activities</p>
 **/
public class WallPost implements Parcelable
   {
      // data variables
      public int type;
      public String postID;
      public String friendID;
      public String date;
      public String textContent = null;

      public String multimediaLink = null;
      public String multimediaKey = null;

      public ArrayList<Comment> comments = new ArrayList<>();


      public WallPost()
         {
         }


      public WallPost(int type, String friendID, String textContent)
         {
            // create POST ID
            postID = IDGenerator.generate(this.friendID);

            // create post date
            Date currentDate = new Date();
            SimpleDateFormat dateformatDay = new SimpleDateFormat("MMM dd 'at' h:mmaa", Locale.US);
            this.date = dateformatDay.format(currentDate);

            this.type = type;
            this.friendID = friendID;
            this.textContent = textContent;
         }

      public WallPost(int type, String friendID)
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

                  // store the comments
                  JSONArray commentList = new JSONArray();
                  for(int i = 0; i < comments.size(); i++)
                     {
                        Comment comment = comments.get(i);
                        commentList.put(comment.createJSONObject());
                     }
                  obj.put("comments", commentList);
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

                  // get the comments
                  JSONArray commentList = obj.getJSONArray("comments");
                  for(int i = 0; i < commentList.length(); i++)
                     {
                        Comment comment = new Comment();
                        comment.parseJSONObject(commentList.getJSONObject(i));
                        comments.add(comment);
                     }
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      // Parcelling part
      public WallPost(Parcel in)
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

            in.readList(this.comments, Comment.class.getClassLoader());
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

            dest.writeList(this.comments);
         }

      public static final Parcelable.Creator<WallPost> CREATOR = new Parcelable.Creator<WallPost>()
         {
            public WallPost createFromParcel(Parcel in)
               {
                  return new WallPost(in);
               }

            public WallPost[] newArray(int size)
               {
                  return new WallPost[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }