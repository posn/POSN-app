package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import com.posn.Constants;
import com.posn.exceptions.POSNCryptoException;
import com.posn.utility.IDGenerator;
import com.posn.utility.SymmetricKeyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>This class creates a list of groups the user has defined for his/her friends and the groups are stored in a hashmap using the wall post ID as a key.
 *        Methods are included to read and write the data to and from a file.</p>
 * <p>Implements the methods defined by the ApplicationFile interface</p>
 * <p>Implements parcelable to easily pass this class between activities</p>
 **/
public class UserGroupList implements Parcelable, ApplicationFile
   {
      private HashMap<String, UserGroup> userGroups = new HashMap<>();

      public UserGroupList()
         {

         }

      public UserGroup createNewUserGroup(String groupName) throws POSNCryptoException
         {
            UserGroup group = new UserGroup();
            group.name = groupName;

            // generate group ID
            group.ID = IDGenerator.generate(group.name);

            // generate group wall and archive key
            group.groupFileKey = SymmetricKeyManager.createRandomKey();
            group.groupFileLink = null;

            userGroups.put(group.ID, group);

            return group;
         }

      public void updateUserGroup(UserGroup group)
         {
            userGroups.put(group.ID, group);
         }

      public UserGroup getUserGroup(String groupID)
         {
            return userGroups.get(groupID);
         }


      @Override
      public String createApplicationFileContents() throws JSONException
         {
            JSONArray userGroupList = new JSONArray();

            for (Map.Entry<String, UserGroup> entry : userGroups.entrySet())
               {
                  UserGroup group = entry.getValue();
                  userGroupList.put(group.createJSONObject());
               }

            JSONObject object = new JSONObject();
            object.put("groups", userGroupList);

            return object.toString();
         }

      @Override
      public void parseApplicationFileContents(String fileContents) throws JSONException
         {
            userGroups.clear();

            JSONObject data = new JSONObject(fileContents);

            JSONArray userGroupArray = data.getJSONArray("groups");

            for (int n = 0; n < userGroupArray.length(); n++)
               {
                  UserGroup group = new UserGroup();
                  group.parseJSONObject(userGroupArray.getJSONObject(n));

                  userGroups.put(group.ID, group);
               }
         }

      @Override
      public String getDirectoryPath()
         {
            return Constants.applicationDataFilePath;
         }

      @Override
      public String getFileName()
         {
            return Constants.userGroupListFile;
         }

      /**
       * Creates a arraylist of user defined groups from the hashmap and sorts them alphabetically
       *
       * @return Arraylist of groups
       **/
      public ArrayList<UserGroup> getUserGroupsArrayList()
         {
            // create a new arraylist
            ArrayList<UserGroup> list = new ArrayList<>();

            // loop through the hashmap and get all the groups
            for (Map.Entry<String, UserGroup> entry : userGroups.entrySet())
               {
                  // add the group to arraylist
                  list.add(entry.getValue());
               }

            // sort the groups by name (alphabetical order)
            Collections.sort(list, new Comparator<UserGroup>()
               {
                  @Override
                  public int compare(UserGroup lhs, UserGroup rhs)
                     {
                        return lhs.name.compareTo(rhs.name);
                     }
               });

            return list;
         }


      // Parcelling part
      public UserGroupList(Parcel in)
         {
            //initialize your map before
            int size = in.readInt();
            for (int i = 0; i < size; i++)
               {
                  String key = in.readString();
                  UserGroup value = in.readParcelable(UserGroup.class.getClassLoader());
                  userGroups.put(key, value);
               }
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeInt(userGroups.size());
            for (Map.Entry<String, UserGroup> entry : userGroups.entrySet())
               {
                  dest.writeString(entry.getKey());
                  dest.writeParcelable(entry.getValue(), flags);
               }
         }

      public static final Creator<UserGroupList> CREATOR = new Creator<UserGroupList>()
         {
            public UserGroupList createFromParcel(Parcel in)
               {
                  return new UserGroupList(in);
               }

            public UserGroupList[] newArray(int size)
               {
                  return new UserGroupList[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }
