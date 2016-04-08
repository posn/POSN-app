package com.posn.application;

import android.app.Application;
import android.os.Environment;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.posn.datatypes.Friend;
import com.posn.encryption.AsymmetricKeyManager;
import com.posn.utility.DeviceFileManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;


public class POSNApplication extends Application
   {

      // storage directory paths
      // public String personalInfoFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/";
      public String archiveFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/archive";
      public String encryptionKeyFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/keys";
      public String multimediaFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/multimedia";
      public String profileFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/profile";
      public String wallFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/wall";
      public String messagesFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/messages";

      // profile information data
      private String firstName = null;
      private String lastName = null;
      private String emailAddress = null;
      private String phoneNumber = null;
      private String birthday = null;
      private String gender = null;
      private String cloudProvider = null;

      public String getId()
         {
            return id;
         }

      public void setId(String id)
         {
            this.id = id;
         }

      private String id = null;

      // password data
      String password = null;

      // Friends Tab data
      public Friend newFriendRequest = null;
      public Friend newAcceptedFriend = null;


      @Override
      public void onCreate()
         {
            super.onCreate();
         }


      public String getFirstName()
         {
            return firstName;
         }


      public void setFirstName(String firstName)
         {
            this.firstName = firstName;
         }


      public String getLastName()
         {
            return lastName;
         }


      public void setLastName(String lastName)
         {
            this.lastName = lastName;
         }


      public String getEmailAddress()
         {
            return emailAddress;
         }


      public void setEmailAddress(String emailAddress)
         {
            this.emailAddress = emailAddress;
         }


      public String getPhoneNumber()
         {
            return phoneNumber;
         }


      public void setPhoneNumber(String phoneNumber)
         {
            this.phoneNumber = phoneNumber;
         }


      public String getBirthday()
         {
            return birthday;
         }


      public void setBirthday(String birthday)
         {
            this.birthday = birthday;
         }


      public String getGender()
         {
            return gender;
         }


      public void setGender(String gender)
         {
            this.gender = gender;
         }


      public String getPassword()
         {
            return this.password;
         }


      public void setPassword(String pass)
         {
            this.password = pass;
         }


      public void savePersonalInformation()
         {
            String fileContents;

            // store personal data
            JSONObject user = new JSONObject();
            try
               {
                  user.put("firstname", firstName);
                  user.put("lastname", lastName);
                  user.put("email", emailAddress);
                  user.put("phone", phoneNumber);
                  user.put("birthday", birthday);
                  user.put("gender", gender);
                  user.put("cloudprovider", cloudProvider);

               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }

            // encrypt fileContents
            fileContents = AsymmetricKeyManager.encrypt(user.toString());

            DeviceFileManager.writeStringToFile(fileContents, profileFilePath + "/personalInfo.txt");
         }


      public boolean loadPersonalInformation()
         {
            String fileContents, line;
            File file = new File(profileFilePath + "/personalInfo.txt");

            try
               {
                  BufferedReader br = new BufferedReader(new FileReader(file));

                  StringBuilder sb = new StringBuilder();
                  while ((line = br.readLine()) != null)
                     {
                        sb.append(line);
                     }

                  br.close();
                  fileContents = sb.toString();

                  // decrypt the file contents
                  fileContents = AsymmetricKeyManager.decrypt(fileContents);

                  JSONObject data = new JSONObject(fileContents);

                  // get the first name
                  firstName = data.getString("firstname");

                  // get the last name
                  lastName = data.getString("lastname");

                  // get the email address
                  emailAddress = data.getString("email");

                  // get the phone number
                  phoneNumber = data.getString("phone");

                  // get the birthday
                  birthday = data.getString("birthday");

                  // get the gender
                  gender = data.getString("gender");

                  // get the cloud provider
                  cloudProvider = data.getString("cloudprovider");

                  final HashCode hashCode = Hashing.sha1().hashString(emailAddress, Charset.defaultCharset());
                  id = hashCode.toString();

                  return true;
               }
            catch (IOException | JSONException e)
               {
                  e.printStackTrace();
               }


            return false;
         }


      public String getCloudProvider()
         {
            return cloudProvider;
         }


      public void setCloudProvider(String cloudProvider)
         {
            this.cloudProvider = cloudProvider;
         }

   }
