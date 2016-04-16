package com.posn.datatypes;


import android.os.Parcel;
import android.os.Parcelable;

import com.posn.encryption.SymmetricKeyManager;
import com.posn.utility.DeviceFileManager;

import org.json.JSONException;
import org.json.JSONObject;

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

      public User()
         {

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


      public void saveUserToFile(String password, final String devicePath)
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

               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }

            // encrypt fileContents
            String key = SymmetricKeyManager.createKeyFromString(password);
            fileContents = SymmetricKeyManager.encrypt(key, user.toString());

            DeviceFileManager.writeStringToFile(fileContents, devicePath);
         }


      public boolean loadUserFromFile(String pass, final String devicePath)
         {
            try
               {
                  String encryptedData = DeviceFileManager.loadStringFromFile(devicePath);

                  // decrypt the file contents
                  String key = SymmetricKeyManager.createKeyFromString(pass);
                  String fileContents = SymmetricKeyManager.decrypt(key, encryptedData);

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


                  return true;
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }


            return false;
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

//            this.publicKey = in.readString();

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
