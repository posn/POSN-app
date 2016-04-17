package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Notification implements Parcelable
   {
      public int type;
      public String friend;
      public String date;

      public boolean selected;

      public Notification()
         {
         }


      public Notification(int type, String friend, String date)
         {
            this.type = type;
            this.friend = friend;
            this.date = date;
         }

      public JSONObject createJSONObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("type", type);
                  obj.put("friend", friend);
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
                  friend = obj.getString("friend");
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
            this.friend = in.readString();
            this.date = in.readString();
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeInt(this.type);
            dest.writeString(this.friend);
            dest.writeString(this.date);
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