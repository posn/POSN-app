package com.posn.managers;


import android.os.Parcel;
import android.os.Parcelable;

import com.posn.constants.Constants;
import com.posn.datatypes.ApplicationFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>This class represents a data owner user and his or her data. Methods are included to read and write the data to and from a file.</p>
 * <p>Implements the methods defined by the ApplicationFile interface</p>
 * <p>Implements parcelable to easily pass this class between activities</p>
 **/
public class UserManager implements Parcelable, ApplicationFile
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


      /**
       * Constructor to create new User object
       **/
      public UserManager()
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
         }

      // Parcelling part
      public UserManager(Parcel in)
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
         }

      public static final Parcelable.Creator<UserManager> CREATOR = new Parcelable.Creator<UserManager>()
         {
            public UserManager createFromParcel(Parcel in)
               {
                  return new UserManager(in);
               }

            public UserManager[] newArray(int size)
               {
                  return new UserManager[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }
