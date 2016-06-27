package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * <p>This class represents a group that a friend has place the user in (this data is fetched from the friend file).
 *    Includes methods to create and parse wall post objects in a JSON format</p>
 * <p>Implements parcelable to easily pass wall posts between activities</p>
 **/
public class FriendGroup implements Parcelable
   {
      public String ID;
      public String groupFileLink;
      public String groupFileKey;
      public int version;

      public FriendGroup()
         {

         }


      public JSONObject createJSONObject()
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
                  groupFileLink = obj.getString("groupFileLink");
                  groupFileKey = obj.getString("groupFileKey");
                  version = obj.getInt("version");
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      @Override
      public boolean equals(Object o)
         {
            if (!(o instanceof FriendGroup))
               {
                  return false;
               }
            FriendGroup other = (FriendGroup) o;
            return ID.equalsIgnoreCase(other.ID);
         }


      // Parcelling part
      public FriendGroup(Parcel in)
         {
            this.ID = in.readString();
            this.groupFileLink = in.readString();
            this.groupFileKey = in.readString();
            this.version = in.readInt();
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeString(this.ID);
            dest.writeString(this.groupFileLink);
            dest.writeString(this.groupFileKey);
            dest.writeInt(this.version);
         }

      public static final Creator<FriendGroup> CREATOR = new Creator<FriendGroup>()
         {
            public FriendGroup createFromParcel(Parcel in)
               {
                  return new FriendGroup(in);
               }

            public FriendGroup[] newArray(int size)
               {
                  return new FriendGroup[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }


   }