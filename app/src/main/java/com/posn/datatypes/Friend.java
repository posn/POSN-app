package com.posn.datatypes;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

public class Friend implements Parcelable
   {
      public String id;
      public String name;

      public String phone;
      public String email;
      public String image_uri;
      public Bitmap bitmap;
      public boolean selected;
      public int status;

      public Friend()
         {
            selected = false;
         }

      public Friend(String name)
         {
            this.name = name;
            selected = false;
         }

      /*
            public Friend(String name, int status)
               {
                  this.name = name;
                  this.status = status;
                  id = "0";
                  phone = "0";
                  email = "0";
                  image_uri = "asd";
                  selected = false;
               }
      */
      public Friend(String name, String email, int status)
         {
            this.name = name;
            this.email = email;
            this.status = status;

            final HashCode hashCode = Hashing.sha1().hashString(email, Charset.defaultCharset());
            id = hashCode.toString();

            phone = "0";
            image_uri = "asd";
            selected = false;
         }

      public JSONObject createJSONObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("id", id);
                  obj.put("name", name);
                  obj.put("phone", phone);
                  obj.put("email", email);
                  obj.put("image_uri", image_uri);
                  obj.put("status", status);
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
                  id = obj.getString("id");
                  name = obj.getString("name");
                  phone = obj.getString("phone");
                  email = obj.getString("email");
                  image_uri = obj.getString("image_uri");
                  status = obj.getInt("status");
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
            this.id = in.readString();
            this.name = in.readString();
            this.phone = in.readString();
            this.email = in.readString();
            this.image_uri = in.readString();
            this.status = in.readInt();
         }



      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeString(this.id);
            dest.writeString(this.name);
            dest.writeString(this.phone);
            dest.writeString(this.email);
            dest.writeString(this.image_uri);
            dest.writeInt(this.status);
         }

      public static final Parcelable.Creator <Friend> CREATOR = new Parcelable.Creator<Friend>()
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