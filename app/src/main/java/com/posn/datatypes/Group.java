package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Group implements Parcelable
   {
      public String ID;
      public String name;

      public String groupFileLink;
      public String groupFileKey;

      public ArrayList<String> groupMembers = new ArrayList<>();

      public boolean selected;

      public Group()
         {
            selected = false;
         }

      public Group(String name)
         {
            this.name = name;
            selected = false;
         }

      public Group(String ID, String name, String groupFileLink, String groupFileKey)
         {
            this.name = name;
            this.ID = ID;
            this.groupFileLink = groupFileLink;
            this.groupFileKey = groupFileKey;

            groupMembers.clear();

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

                  JSONArray jsArray = new JSONArray(groupMembers);

                  obj.put("groupMembers", jsArray);

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

                  JSONArray groupMemberList = obj.getJSONArray("groupMembers");

                  for (int n = 0; n < groupMemberList.length(); n++)
                     {
                        String friendID = groupMemberList.getString(n);
                        groupMembers.add(friendID);
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
            if (!(o instanceof Group))
               {
                  return false;
               }
            Group other = (Group) o;
            System.out.println(name + " | " + other.name);
            return name.equalsIgnoreCase(other.name);
         }


      // Parcelling part
      public Group(Parcel in)
         {
            this.ID = in.readString();
            this.name = in.readString();
            this.groupFileLink = in.readString();
            this.groupFileKey = in.readString();

         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeString(this.ID);
            dest.writeString(this.name);
            dest.writeString(this.groupFileLink);
            dest.writeString(this.groupFileKey);
         }

      public static final Creator<Group> CREATOR = new Creator<Group>()
      {
         public Group createFromParcel(Parcel in)
            {
               return new Group(in);
            }

         public Group[] newArray(int size)
            {
               return new Group[size];
            }
      };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }