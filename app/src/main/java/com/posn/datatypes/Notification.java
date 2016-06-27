package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * <p>This class represents a notification (Note: not fully implemented). Includes methods to create and parse wall post objects in a JSON format</p>
 * <p>Implements parcelable to easily pass wall posts between activities</p>
 **/
public class Notification implements Parcelable
   {
      public int type;
      public String friendID;
      public String date;

      public boolean selected;

      public Notification()
         {
         }


      public Notification(int type, String friendID, String date)
         {
            this.type = type;
            this.friendID = friendID;
            this.date = date;
         }

      public JSONObject createJSONObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("type", type);
                  obj.put("friendID", friendID);
                  obj.put("date", date);
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
                  friendID = obj.getString("friendID");
                  date = obj.getString("date");
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      // Parcelling part
      public Notification(Parcel in)
         {
            this.type = in.readInt();
            this.friendID = in.readString();
            this.date = in.readString();
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeInt(this.type);
            dest.writeString(this.friendID);
            dest.writeString(this.date);
         }

      public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>()
         {
            public Notification createFromParcel(Parcel in)
               {
                  return new Notification(in);
               }

            public Notification[] newArray(int size)
               {
                  return new Notification[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }