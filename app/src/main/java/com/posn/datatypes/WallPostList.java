package com.posn.datatypes;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import com.posn.Constants;
import com.posn.utility.DeviceFileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class WallPostList implements Parcelable
   {
      public HashMap<String, WallPost> wallPosts;
      private String deviceFileKey;

      public WallPostList(String deviceFileKey)
         {

            this.deviceFileKey = deviceFileKey;
            wallPosts = new HashMap<>();
         }

      public void loadWallPostsFromFile()
         {
            wallPosts.clear();

            try
               {
                  //String encryptedData = DeviceFileManager.loadStringFromFile(Constants.applicationDataFilePath + "/" + Constants.wallListFile);

                  // decrypt the file contents
                  //String fileContents = SymmetricKeyManager.decrypt(deviceFileKey, encryptedData);

                  JSONObject data = DeviceFileManager.loadJSONObjectFromFile(Constants.applicationDataFilePath + "/" + Constants.wallListFile);

                  JSONArray wallPostsArray = data.getJSONArray("posts");

                  for (int n = 0; n < wallPostsArray.length(); n++)
                     {
                        WallPost wallPost = new WallPost();
                        wallPost.parseJSONObject(wallPostsArray.getJSONObject(n));

                        wallPosts.put(wallPost.postID, wallPost);
                     }
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      public void saveWallPostsToFileAsyncTask()
      {
         new AsyncTask<Void, Void, Void>()
         {
            protected Void doInBackground(Void... params)
               {
                  saveWallPostsToFile();
                  return null;
               }
         }.execute();
      }

      public void saveWallPostsToFile()
         {
            JSONArray wallPostList = new JSONArray();

            try
               {
                  for (Map.Entry<String, WallPost> entry : wallPosts.entrySet())
                     {
                        WallPost wallPost = entry.getValue();
                        wallPostList.put(wallPost.createJSONObject());
                     }

                  JSONObject object = new JSONObject();
                  object.put("posts", wallPostList);


                  //fileContents = SymmetricKeyManager.encrypt(deviceFileKey, user.toString());

                 // DeviceFileManager.writeStringToFile(fileContents, Constants.applicationDataFilePath + "/" + Constants.wallListFile);

                  DeviceFileManager.writeJSONToFile(object, Constants.applicationDataFilePath + "/" + Constants.wallListFile);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }


      // Parcelling part
      public WallPostList(Parcel in)
         {
            //initialize your map before
            int size = in.readInt();
            for(int i = 0; i < size; i++){
               String key = in.readString();
               WallPost value = in.readParcelable(WallPost.class.getClassLoader());
               wallPosts.put(key,value);
            }
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeInt(wallPosts.size());
            for(Map.Entry<String,WallPost> entry : wallPosts.entrySet()){
               dest.writeString(entry.getKey());
               dest.writeParcelable(entry.getValue(),flags);
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
