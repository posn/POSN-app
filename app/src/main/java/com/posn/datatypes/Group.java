package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Calendar;

public class Group implements Parcelable
   {
      public String id;
      public String name;

      public String groupFileLink;
      public String groupFileKey;

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

      public Group(String name, String groupFileLink, String groupFileKey)
         {
            this.name = name;

            // create group ID based on group name and created time
            Calendar c = Calendar.getInstance();
            String timeDate = c.toString();

            final HashCode hashCode = Hashing.sha256().hashString(name + timeDate, Charset.defaultCharset());
            id = hashCode.toString();

            this.groupFileLink = groupFileLink;
            this.groupFileKey = groupFileKey;

            selected = false;
         }

      public JSONObject createJOSNObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("id", id);
                  obj.put("name", name);
                  obj.put("groupFileLink", groupFileLink);
                  obj.put("groupFileKey", groupFileKey);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }

            return obj;
         }

      public void parseJOSNObject(JSONObject obj)
         {
            try
               {
                  id = obj.getString("id");
                  name = obj.getString("name");
                  groupFileLink = obj.getString("groupFileLink");
                  groupFileKey = obj.getString("groupFileKey");
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
            this.id = in.readString();
            this.name = in.readString();
            this.groupFileLink = in.readString();
            this.groupFileKey = in.readString();

         }



      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeString(this.id);
            dest.writeString(this.name);
            dest.writeString(this.groupFileLink);
            dest.writeString(this.groupFileKey);
         }

      public static final Creator <Group> CREATOR = new Creator<Group>()
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