package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import com.posn.Constants;
import com.posn.utility.DeviceFileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class WallCommentList implements Parcelable
   {
      public HashMap<String, Comment> comments;
      private String deviceFileKey;

      public WallCommentList(String deviceFileKey)
         {
            this.deviceFileKey = deviceFileKey;
            comments = new HashMap<>();
         }

      public void loadWallCommentsFromFile()
         {
            comments.clear();

            try
               {
                  //String encryptedData = DeviceFileManager.loadStringFromFile(Constants.applicationDataFilePath + "/" + Constants.wallListFile);

                  // decrypt the file contents
                  //String fileContents = SymmetricKeyManager.decrypt(deviceFileKey, encryptedData);

                  JSONObject data = DeviceFileManager.loadJSONObjectFromFile(Constants.applicationDataFilePath + "/" + Constants.wallListFile);

                  JSONArray wallCommentsArray = data.getJSONArray("comments");

                  for (int n = 0; n < wallCommentsArray.length(); n++)
                     {
                        Comment comment = new Comment();
                        comment.parseJSONObject(wallCommentsArray.getJSONObject(n));

                        comments.put(comment.commentID, comment);
                     }
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }


      public void saveWallCommentsToFile()
         {
            JSONArray wallCommentList = new JSONArray();

            try
               {
                  for (Map.Entry<String, Comment> entry : comments.entrySet())
                     {
                        Comment comment = entry.getValue();
                        wallCommentList.put(comment.createJSONObject());
                     }

                  JSONObject object = new JSONObject();
                  object.put("comments", wallCommentList);


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
      public WallCommentList(Parcel in)
         {
            //initialize your map before
            int size = in.readInt();
            for(int i = 0; i < size; i++){
               String key = in.readString();
               Comment value = in.readParcelable(Comment.class.getClassLoader());
               comments.put(key,value);
            }
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeInt(comments.size());
            for(Map.Entry<String,Comment> entry : comments.entrySet()){
               dest.writeString(entry.getKey());
               dest.writeParcelable(entry.getValue(),flags);
            }
         }

      public static final Creator<WallCommentList> CREATOR = new Creator<WallCommentList>()
         {
            public WallCommentList createFromParcel(Parcel in)
               {
                  return new WallCommentList(in);
               }

            public WallCommentList[] newArray(int size)
               {
                  return new WallCommentList[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }
