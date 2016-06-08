package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import com.posn.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class WallPostList implements Parcelable, ApplicationFile
   {
      public HashMap<String, WallPost> wallPosts = new HashMap<>();

      public WallPostList()
         {
         }

      public WallPost getWallPost(String wallPostID)
         {
            return wallPosts.get(wallPostID);
         }


      @Override
      public String createApplicationFileContents() throws JSONException
         {
            JSONArray wallPostList = new JSONArray();

            for (Map.Entry<String, WallPost> entry : wallPosts.entrySet())
               {
                  WallPost wallPost = entry.getValue();
                  wallPostList.put(wallPost.createJSONObject());
               }

            JSONObject object = new JSONObject();
            object.put("posts", wallPostList);

            return object.toString();
         }

      @Override
      public void parseApplicationFileContents(String fileContents) throws JSONException
         {
            wallPosts.clear();

            JSONObject data = new JSONObject(fileContents);

            JSONArray wallPostsArray = data.getJSONArray("posts");

            for (int n = 0; n < wallPostsArray.length(); n++)
               {
                  WallPost wallPost = new WallPost();
                  wallPost.parseJSONObject(wallPostsArray.getJSONObject(n));

                  wallPosts.put(wallPost.postID, wallPost);
               }
         }

      @Override
      public String getDirectoryPath()
         {
            return Constants.applicationDataFilePath;
         }

      @Override
      public String getFileName()
         {
            return Constants.wallListFile;
         }


      // Parcelling part
      public WallPostList(Parcel in)
         {
            //initialize your map before
            int size = in.readInt();
            for (int i = 0; i < size; i++)
               {
                  String key = in.readString();
                  WallPost value = in.readParcelable(WallPost.class.getClassLoader());
                  wallPosts.put(key, value);
               }
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeInt(wallPosts.size());
            for (Map.Entry<String, WallPost> entry : wallPosts.entrySet())
               {
                  dest.writeString(entry.getKey());
                  dest.writeParcelable(entry.getValue(), flags);
               }
         }

      public static final Parcelable.Creator<WallPostList> CREATOR = new Parcelable.Creator<WallPostList>()
         {
            public WallPostList createFromParcel(Parcel in)
               {
                  return new WallPostList(in);
               }

            public WallPostList[] newArray(int size)
               {
                  return new WallPostList[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }
