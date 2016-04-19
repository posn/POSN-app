package com.posn.datatypes;


import android.os.Parcel;
import android.os.Parcelable;

import com.posn.Constants;
import com.posn.encryption.SymmetricKeyManager;
import com.posn.utility.DeviceFileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class User implements Parcelable
   {
      public String ID = null;
      public String firstName = null;
      public String lastName = null;
      public String email = null;
      public String phoneNumber = null;
      public String birthday = null;
      public String gender = null;
      public String cloudProvider = null;

      public String publicKey = null;
      public String privateKey = null;

      public HashMap<String, UserGroup> userDefinedGroups = new HashMap<>();

      public String deviceFileKey;


      public User(String deviceFileKey)
         {
            this.deviceFileKey = deviceFileKey;
         }

      public void print()
         {
            System.out.println("ID: " + ID);
            System.out.println("FIRST: " + firstName);
            System.out.println("LAST: " + lastName);
            System.out.println("EMAIL: " + email);
            System.out.println("PHONE: " + phoneNumber);
            System.out.println("BIRTHDAY: " + birthday);
            System.out.println("GENDER: " + gender);

            System.out.println("PUB: " + publicKey);
            System.out.println("PRIVATE: " + privateKey);

         }


      public void saveUserToFile()
         {
            String fileContents;

            // store personal data
            JSONObject user = new JSONObject();
            try
               {
                  user.put("ID", ID);
                  user.put("firstname", firstName);
                  user.put("lastname", lastName);
                  user.put("email", email);
                  user.put("phone", phoneNumber);
                  user.put("birthday", birthday);
                  user.put("gender", gender);
                  user.put("cloudprovider", cloudProvider);

                  user.put("publicKey", publicKey);
                  user.put("privateKey", privateKey);

                  JSONArray groupList = new JSONArray();

                  // add all of the groups into the JSON array
                  for (Map.Entry<String, UserGroup> entry : userDefinedGroups.entrySet())
                     {
                        UserGroup userGroup = entry.getValue();
                        groupList.put(userGroup.createJSONObject());
                     }

                  // create new JSON object and put the JSON array into it
                  JSONObject object = new JSONObject();
                  object.put("groups", groupList);

               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }

            // encrypt fileContents
            fileContents = SymmetricKeyManager.encrypt(deviceFileKey, user.toString());

            DeviceFileManager.writeStringToFile(fileContents, Constants.applicationDataFilePath + "/" + Constants.userFile);
         }


      public boolean loadUserFromFile()
         {
            try
               {
                  String encryptedData = DeviceFileManager.loadStringFromFile(Constants.applicationDataFilePath + "/" + Constants.userFile);

                  // decrypt the file contents
                  String fileContents = SymmetricKeyManager.decrypt(deviceFileKey, encryptedData);

                  JSONObject data = new JSONObject(fileContents);

                  ID = data.getString("ID");
                  firstName = data.getString("firstname");
                  lastName = data.getString("lastname");
                  email = data.getString("email");
                  phoneNumber = data.getString("phone");
                  birthday = data.getString("birthday");
                  gender = data.getString("gender");
                  cloudProvider = data.getString("cloudprovider");

                  publicKey = data.getString("publicKey");
                  privateKey = data.getString("privateKey");

                  // need to decrypt private key

                  // get array of friends
                  JSONArray groupList = data.getJSONArray("groups");

                  // loop through array and parse individual friends
                  for (int n = 0; n < groupList.length(); n++)
                     {
                        // parse the friend
                        UserGroup userGroup = new UserGroup();
                        userGroup.parseJSONObject(groupList.getJSONObject(n));

                        // put into request or current friend list based on status
                        userDefinedGroups.put(userGroup.ID, userGroup);
                     }


                  return true;
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }


            return false;
         }


      public ArrayList<UserGroup> getUserGroupsArrayList()
         {
            ArrayList<UserGroup> list = new ArrayList<>();

            for (Map.Entry<String, UserGroup> entry : userDefinedGroups.entrySet())
               {
                  list.add(entry.getValue());
               }

            Collections.sort(list, new Comparator<UserGroup>()
               {
                  @Override public int compare(UserGroup lhs, UserGroup rhs)
                     {
                        return lhs.name.compareTo(rhs.name);
                     }
               });

            return list;
         }


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
            this.cloudProvider = in.readString();
            this.publicKey = in.readString();
            this.privateKey = in.readString();

            //initialize your map before
            int size = in.readInt();
            for (int i = 0; i < size; i++)
               {
                  String key = in.readString();
                  UserGroup value = in.readParcelable(UserGroup.class.getClassLoader());
                  userDefinedGroups.put(key, value);
               }

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
            dest.writeString(this.cloudProvider);

            dest.writeString(this.publicKey);
            dest.writeString(this.privateKey);

            dest.writeInt(userDefinedGroups.size());
            for (Map.Entry<String, UserGroup> entry : userDefinedGroups.entrySet())
               {
                  dest.writeString(entry.getKey());
                  dest.writeParcelable(entry.getValue(), flags);
               }
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
