package com.posn.managers;

import android.os.Parcel;
import android.os.Parcelable;

import com.posn.constants.Constants;
import com.posn.datatypes.ApplicationFile;
import com.posn.datatypes.Comment;
import com.posn.datatypes.WallPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * <p>This class stores the data for all the wall posts in a hashmap using the wall post ID as a key. Methods are included to read and write the data to and from a file.</p>
 * <p>Implements the methods defined by the ApplicationFile interface</p>
 * <p>Implements parcelable to easily pass this class between activities</p>
 **/
public class WallPostManager implements Parcelable, ApplicationFile
   {
      public HashMap<String, WallPost> wallPosts = new HashMap<>();

      public WallPostManager()
         {
         }

      public WallPost getWallPost(String wallPostID)
         {
            return wallPosts.get(wallPostID);
         }

      public void addWallPost(WallPost post)
         {
            wallPosts.put(post.postID, post);
         }


      public void addCommentToWallPost(Comment comment)
         {
            WallPost post = wallPosts.get(comment.postID);
            post.comments.add(comment);
            wallPosts.put(post.postID, post);
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
      public WallPostManager(Parcel in)
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

      public static final Parcelable.Creator<WallPostManager> CREATOR = new Parcelable.Creator<WallPostManager>()
         {
            public WallPostManager createFromParcel(Parcel in)
               {
                  return new WallPostManager(in);
               }

            public WallPostManager[] newArray(int size)
               {
                  return new WallPostManager[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }
