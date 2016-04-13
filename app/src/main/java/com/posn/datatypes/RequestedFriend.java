package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RequestedFriend implements Parcelable
   {
      public int status; // can be pending (user sent the request) or request (user responds)

      public String ID;
      public String name;
      public String email;
      public String publicKey;
      public String fileLink;   // can be friend file or temporal file
      public String nonce;
      public String nonce2;

      // holds the list of groups as group IDs
      public ArrayList<String> groups = new ArrayList<>();


      public boolean selected;

      public RequestedFriend()
         {
            selected = false;
         }

      public RequestedFriend(String name)
         {
            this.name = name;
            selected = false;
         }


      public RequestedFriend(String name, String email, int status)
         {
            this.name = name;
            this.email = email;
            this.status = status;
            selected = false;
         }

      public JSONObject createJSONObject()
         {
            JSONObject obj = new JSONObject();

            try
               {
                  obj.put("status", status);

                  obj.put("id", ID);
                  obj.put("name", name);
                  obj.put("email", email);
                  obj.put("publicKey", publicKey);
                  obj.put("fileLink", fileLink);
                  obj.put("nonce", nonce);
                  obj.put("nonce2", nonce2);

                  JSONArray jsArray = new JSONArray(groups);
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

                  if (obj.has("id"))
                     {
                        ID = obj.getString("id");
                     }
                  name = obj.getString("name");
                  email = obj.getString("email");
                  if (obj.has("publicKey"))
                     {
                        publicKey = obj.getString("publicKey");
                     }
                  if (obj.has("fileLink"))
                     {
                        fileLink = obj.getString("fileLink");
                     }

                  nonce = obj.getString("nonce");
                  if(obj.has("nonce2"))
                     {
                        nonce2 = obj.getString("nonce2");
                     }

                  JSONArray groupMemberList = obj.getJSONArray("groups");

                  for (int n = 0; n < groupMemberList.length(); n++)
                     {
                        String groupID = groupMemberList.getString(n);
                        groups.add(groupID);
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
            if (!(o instanceof RequestedFriend))
               {
                  return false;
               }
            RequestedFriend other = (RequestedFriend) o;
            return (name.equalsIgnoreCase(other.name));
         }


      // Parcelling part
      public RequestedFriend(Parcel in)
         {
            this.ID = in.readString();
            this.name = in.readString();
            this.email = in.readString();
            this.publicKey = in.readString();
            this.fileLink = in.readString();
            this.status = in.readInt();
            this.nonce = in.readString();
            this.nonce2 = in.readString();

            in.readStringList(groups);
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeString(this.ID);
            dest.writeString(this.name);
            dest.writeString(this.email);
            dest.writeString(this.publicKey);
            dest.writeString(this.fileLink);
            dest.writeInt(this.status);
            dest.writeString(this.nonce);
            dest.writeString(this.nonce2);
            dest.writeStringList(groups);
         }

      public static final Creator<RequestedFriend> CREATOR = new Creator<RequestedFriend>()
         {
            public RequestedFriend createFromParcel(Parcel in)
               {
                  return new RequestedFriend(in);
               }

            public RequestedFriend[] newArray(int size)
               {
                  return new RequestedFriend[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }