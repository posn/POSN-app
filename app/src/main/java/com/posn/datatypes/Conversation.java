package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * <p>This class represents a conversation object (Note: not fully implemented). May need to add an array of messages/link to a file containing the messages.
 *    Includes methods to create and parse wall post objects in a JSON format</p>
 * <p>Implements parcelable to easily pass wall posts between activities</p>
 **/
public class Conversation implements Parcelable
   {
      public int type;

      public String friend;
      public String date;
      public String lastMessage;


      public Conversation()
         {
         }


      public Conversation(String friend, String date, String lastMessage)
         {
            this.friend = friend;
            this.date = date;
            this.lastMessage = lastMessage;
         }

      public JSONObject createJSONObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("friendID", friend);
                  obj.put("date", date);
                  obj.put("lastMessage", lastMessage);
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
                  friend = obj.getString("friendID");
                  date = obj.getString("date");
                  lastMessage = obj.getString("lastMessage");
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      // Parcelling part
      public Conversation(Parcel in)
         {
            this.friend = in.readString();
            this.date = in.readString();
            this.lastMessage = in.readString();
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeString(this.friend);
            dest.writeString(this.date);
            dest.writeString(this.lastMessage);
         }

      public static final Parcelable.Creator<Conversation> CREATOR = new Parcelable.Creator<Conversation>()
         {
            public Conversation createFromParcel(Parcel in)
               {
                  return new Conversation(in);
               }

            public Conversation[] newArray(int size)
               {
                  return new Conversation[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }