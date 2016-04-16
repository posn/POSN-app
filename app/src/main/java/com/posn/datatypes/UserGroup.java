package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserGroup implements Parcelable
   {
      public String ID;
      public String name;

      public String groupFileLink;
      public String groupFileKey;

      public int version;

      // used to hold the post IDs for all the posts in the group's wall.
      // used to create wall files
      public ArrayList<String> wallPostList = new ArrayList<>();

      public boolean selected;

      public UserGroup()
         {
            version = 0;
            selected = false;
         }


      public UserGroup(String ID, String name, String groupFileLink, String groupFileKey)
         {
            this.name = name;
            this.ID = ID;
            this.groupFileLink = groupFileLink;
            this.groupFileKey = groupFileKey;
            selected = false;
         }

      public JSONObject createJSONObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("id", ID);
                  obj.put("name", name);
                  obj.put("groupFileLink", groupFileLink);
                  obj.put("groupFileKey", groupFileKey);
                  obj.put("version", version);

                  JSONArray jsArray = new JSONArray(wallPostList);

                  obj.put("wallposts", jsArray);

               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }

            return obj;
         }

      public JSONObject createFriendFileJSONObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("id", ID);
                  obj.put("groupFileLink", groupFileLink);
                  obj.put("groupFileKey", groupFileKey);
                  obj.put("version", 0);

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
                  ID = obj.getString("id");
                  name = obj.getString("name");
                  groupFileLink = obj.getString("groupFileLink");
                  groupFileKey = obj.getString("groupFileKey");
                  version = obj.getInt("version");

                  JSONArray groupMemberList = obj.getJSONArray("wallposts");

                  for (int n = 0; n < groupMemberList.length(); n++)
                     {
                        String wallPostID = groupMemberList.getString(n);
                        wallPostList.add(wallPostID);
                     }
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      @Override
      public boolean equals(Object o)
         {
            if (!(o instanceof UserGroup))
               {
                  return false;
               }
            UserGroup other = (UserGroup) o;
            return name.equalsIgnoreCase(other.name);
         }


      // Parcelling part
      public UserGroup(Parcel in)
         {
            this.ID = in.readString();
            this.name = in.readString();
            this.groupFileLink = in.readString();
            this.groupFileKey = in.readString();
            this.version = in.readInt();

         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeString(this.ID);
            dest.writeString(this.name);
            dest.writeString(this.groupFileLink);
            dest.writeString(this.groupFileKey);
            dest.writeInt(this.version);
         }

      public static final Creator<UserGroup> CREATOR = new Creator<UserGroup>()
      {
         public UserGroup createFromParcel(Parcel in)
            {
               return new UserGroup(in);
            }

         public UserGroup[] newArray(int size)
            {
               return new UserGroup[size];
            }
      };

      @Override
      public int describeContents()
         {
            return 0;
         }


   }