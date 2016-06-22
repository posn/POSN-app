package com.posn.datatypes;


import android.os.Parcel;
import android.os.Parcelable;

import com.posn.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p></p>This class represents a data owner user and his or her data. Methods are included to read and write the data to and from a file.</p>
 * <p>Implements parcelable to easily pass this class between activities</p>
 **/
public class User implements Parcelable, ApplicationFile
   {
      // user personal information
      public String ID = null;
      public String firstName = null;
      public String lastName = null;
      public String email = null;
      public String phoneNumber = null;
      public String birthday = null;
      public String gender = null;

      // which cloud provider is being used
      public int cloudProvider;


      // public and private key
      public String publicKey = null;
      public String privateKey = null;

      // hashmap of all of the groups the user has created for his/her friends


      /**
       * Constructor to create new User object
       **/
      public User()
         {
         }



      /**
       * Creates a JSON formatted string from the user data
       *
       * @return JSON formatted string containing user data
       * @throws JSONException
       **/
      @Override
      public String createApplicationFileContents() throws JSONException
         {
            // add personal information to the JSON object
            JSONObject user = new JSONObject();
            user.put("ID", ID);
            user.put("firstname", firstName);
            user.put("lastname", lastName);
            user.put("email", email);
            user.put("phone", phoneNumber);
            user.put("birthday", birthday);
            user.put("gender", gender);
            user.put("cloudprovider", cloudProvider);

            // add public/private key to the JSON object
            user.put("publicKey", publicKey);
            user.put("privateKey", privateKey);

            JSONArray groupList = new JSONArray();

            /*
            // add all of the groups into the JSON array
            for (Map.Entry<String, UserGroup> entry : userDefinedGroups.entrySet())
               {
                  UserGroup userGroup = entry.getValue();
                  groupList.put(userGroup.createJSONObject());
               }
*/
            // create new JSON object and put the JSON array into it
            user.put("groups", groupList);

            // return the JSON formatted string
            return user.toString();
         }

      @Override
      public String getDirectoryPath()
         {
            return Constants.applicationDataFilePath;
         }

      @Override
      public String getFileName()
         {
            return Constants.userFile;
         }

      /**
       * Parses the user info data file from a JSON formatted string
       *
       * @param fileContents file contents stored as a JSON formatted string
       * @throws JSONException
       **/
      @Override
      public void parseApplicationFileContents(String fileContents) throws JSONException
         {
            // create a JSON object from the string and parse the user data
            JSONObject data = new JSONObject(fileContents);
            ID = data.getString("ID");
            firstName = data.getString("firstname");
            lastName = data.getString("lastname");
            email = data.getString("email");
            phoneNumber = data.getString("phone");
            birthday = data.getString("birthday");
            gender = data.getString("gender");
            cloudProvider = data.getInt("cloudprovider");

            publicKey = data.getString("publicKey");
            privateKey = data.getString("privateKey");

            /*
            // get array of groups
            JSONArray groupList = data.getJSONArray("groups");

            // loop through array and parse individual groups
            for (int n = 0; n < groupList.length(); n++)
               {
                  // parse the group
                  UserGroup userGroup = new UserGroup();
                  userGroup.parseJSONObject(groupList.getJSONObject(n));

                  // add the group to the hashmap
                  userDefinedGroups.put(userGroup.ID, userGroup);
               }*/
         }


/*
      public ArrayList<String> getUserWallPostIDs()
         {
            ArrayList<String> list = new ArrayList<>();

            for (Map.Entry<String, UserGroup> entry : userDefinedGroups.entrySet())
               {
                  list.addAll(entry.getValue().wallPostList);
               }
            return list;
         }
*/

      // Parcelling part
      public User(Parcel in)
         {
            this.ID = in.readString();
            this.firstName = in.readString();
            this.lastName = in.readString();
            this.email = in.readString();
            this.phoneNumber = in.readString();
            this.birthday = in.readString();
            this.gender = in.readString();
            this.cloudProvider = in.readInt();
            this.publicKey = in.readString();
            this.privateKey = in.readString();

            /*
            //initialize your map before
            int size = in.readInt();
            for (int i = 0; i < size; i++)
               {
                  String key = in.readString();
                  UserGroup value = in.readParcelable(UserGroup.class.getClassLoader());
                  userDefinedGroups.put(key, value);
               }*/
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeString(this.ID);
            dest.writeString(this.firstName);
            dest.writeString(this.lastName);
            dest.writeString(this.email);
            dest.writeString(this.phoneNumber);
            dest.writeString(this.birthday);
            dest.writeString(this.gender);
            dest.writeInt(this.cloudProvider);

            dest.writeString(this.publicKey);
            dest.writeString(this.privateKey);

            /*
            dest.writeInt(userDefinedGroups.size());
            for (Map.Entry<String, UserGroup> entry : userDefinedGroups.entrySet())
               {
                  dest.writeString(entry.getKey());
                  dest.writeParcelable(entry.getValue(), flags);
               }*/
         }

      public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>()
         {
            public User createFromParcel(Parcel in)
               {
                  return new User(in);
               }

            public User[] newArray(int size)
               {
                  return new User[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }
