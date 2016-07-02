package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * <p>This class represents a user defined group for his/her friends. Includes methods to create and parse wall post objects in a JSON format</p>
 * <p>Implements parcelable to easily pass wall posts between activities</p>
 **/
public class UserGroup implements Parcelable
   {
      // group data
      public String ID;
      public String name;

      // current group file
      public String groupFileLink;
      public String groupFileKey;

      // previous archive
      public String archiveFileLink = null;
      public String archiveFileKey = null;

      public int version;

      public boolean selected = false;

      // used to hold the post IDs for all the posts in the group's wall.
      // used to create wall files
      public ArrayList<String> wallPostList = new ArrayList<>();

      // user to hold the friend IDs for all the friends who are in the group
      // used in the UserGroupFragment
      public ArrayList<String> friendsList = new ArrayList<>();

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
            this.version = 0;
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
                  obj.put("archiveFileLink", archiveFileLink);
                  obj.put("archiveFileKey", archiveFileKey);
                  obj.put("version", version);

                  JSONArray jsArray = new JSONArray(wallPostList);
                  obj.put("wallposts", jsArray);

                  jsArray = new JSONArray(friendsList);
                  obj.put("friendsList", jsArray);
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
                  obj.put("version", version);

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
                  if(obj.has("archiveFileLink"))
                     {
                        archiveFileLink = obj.getString("archiveFileLink");
                        archiveFileKey = obj.getString("archiveFileKey");
                     }
                  else
                     {
                        archiveFileLink = null;
                        archiveFileKey = null;
                     }

                  version = obj.getInt("version");

                  JSONArray jsonArray = obj.getJSONArray("wallposts");

                  for (int n = 0; n < jsonArray.length(); n++)
                     {
                        String wallPostID = jsonArray.getString(n);
                        wallPostList.add(wallPostID);
                     }

                  jsonArray = obj.getJSONArray("friendsList");

                  for (int n = 0; n < jsonArray.length(); n++)
                     {
                        String friendID = jsonArray.getString(n);
                        friendsList.add(friendID);
                     }
                  System.out.println("NUM FRIENDS IN GROUP: " +  friendsList.size());

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
            this.archiveFileLink = in.readString();
            this.archiveFileKey = in.readString();
            this.version = in.readInt();
            in.readStringList(this.wallPostList);
            in.readStringList(this.friendsList);
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeString(this.ID);
            dest.writeString(this.name);
            dest.writeString(this.groupFileLink);
            dest.writeString(this.groupFileKey);
            dest.writeString(this.archiveFileLink);
            dest.writeString(this.archiveFileKey);
            dest.writeInt(this.version);
            dest.writeStringList(this.wallPostList);
            dest.writeStringList(this.friendsList);
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