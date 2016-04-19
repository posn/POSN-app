package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.posn.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class Friend implements Parcelable
   {
      public int status;
      public String ID;
      public String name;

      public String phone;
      public String email;
      public String image_uri;
      public String publicKey;

      public boolean selected;

      // USER CREATED DATA

      // holds the list of groups that the user placed the friend in
      // used to create friend file
      public ArrayList<String> userGroups = new ArrayList<>();



      // FRIEND CREATED DATA

      // holds the list of group's the friend placed the user in
      public ArrayList<FriendGroup> friendGroups = new ArrayList<>();
      public String friendFileLink;
      public String friendFileKey;



      public Friend()
         {
            selected = false;
         }


      public Friend(RequestedFriend friend)
         {
            status = Constants.STATUS_TEMPORAL;
            ID = friend.ID;
            name = friend.name;
            publicKey = friend.publicKey;
            friendFileLink = friend.fileLink;
            friendFileKey = friend.fileKey;
            userGroups.addAll(friend.groups);
            selected = false;
         }


      public Friend(String name, String email, int status)
         {
            this.name = name;
            this.email = email;
            this.status = status;

            final HashCode hashCode = Hashing.sha1().hashString(email, Charset.defaultCharset());
            ID = hashCode.toString();

            phone = "0";
            image_uri = "asd";
            selected = false;
         }

      public JSONObject createJSONObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("id", ID);
                  obj.put("name", name);
                  obj.put("email", email);
                  obj.put("status", status);
                  obj.put("publicKey", publicKey);
                  obj.put("friendFileLink", friendFileLink);

                  JSONArray jsArray = new JSONArray(userGroups);
                  obj.put("groups", jsArray);
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
                  status = obj.getInt("status");
                  ID = obj.getString("id");
                  name = obj.getString("name");
                  email = obj.getString("email");

                  if (obj.has("publicKey"))
                     {
                        publicKey = obj.getString("publicKey");
                     }
                  if (obj.has("friendFileLink"))
                     {
                        friendFileLink = obj.getString("friendFileLink");
                     }

                  if (obj.has("groups"))
                     {
                        JSONArray groupMemberList = obj.getJSONArray("groups");
                        for (int n = 0; n < groupMemberList.length(); n++)
                           {
                              String groupID = groupMemberList.getString(n);
                              userGroups.add(groupID);
                           }
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
            if (!(o instanceof Friend))
               {
                  return false;
               }
            Friend other = (Friend) o;
            System.out.println(name + " | " + other.name);
            return name.equalsIgnoreCase(other.name);
         }


      // Parcelling part
      public Friend(Parcel in)
         {
            this.ID = in.readString();
            this.name = in.readString();
            this.email = in.readString();
            this.publicKey = in.readString();
            this.friendFileLink = in.readString();
            this.status = in.readInt();

            in.readStringList(userGroups);
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeString(this.ID);
            dest.writeString(this.name);
            dest.writeString(this.email);
            dest.writeString(this.publicKey);
            dest.writeString(this.friendFileLink);
            dest.writeInt(this.status);
            dest.writeStringList(userGroups);
         }

      public static final Parcelable.Creator<Friend> CREATOR = new Parcelable.Creator<Friend>()
         {
            public Friend createFromParcel(Parcel in)
               {
                  return new Friend(in);
               }

            public Friend[] newArray(int size)
               {
                  return new Friend[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }