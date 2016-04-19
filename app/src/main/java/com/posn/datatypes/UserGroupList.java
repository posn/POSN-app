package com.posn.datatypes;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import com.posn.utility.DeviceFileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class UserGroupList implements Parcelable
   {
      public HashMap<String, UserGroup> groups;

      public UserGroupList()
         {
            groups = new HashMap<>();
         }

      public void loadGroupsFromFile(String fileName)
         {
            try
               {
                  // load friends list into JSON object
                  JSONObject data = DeviceFileManager.loadJSONObjectFromFile(fileName);

                  // get array of friends
                  JSONArray groupList = data.getJSONArray("groups");

                  // loop through array and parse individual friends
                  for (int n = 0; n < groupList.length(); n++)
                     {
                        // parse the friend
                        UserGroup userGroup = new UserGroup();
                        userGroup.parseJSONObject(groupList.getJSONObject(n));

                        // put into request or current friend list based on status
                        groups.put(userGroup.ID, userGroup);
                     }
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      public void saveGroupsToFileAsyncTask(final String devicePath)
         {
            // create new AsyncTask to execute function off main UI thread
            new AsyncTask<Void, Void, Void>()
            {
               protected Void doInBackground(Void... params)
                  {
                     saveGroupsToFile(devicePath);
                     return null;
                  }
            }.execute();
         }

      public void saveGroupsToFile(String devicePath)
         {
            JSONArray groupList = new JSONArray();
            UserGroup userGroup;

            try
               {
                  // add all of the friends in the current friends list into the JSON array
                  for (Map.Entry<String, UserGroup> entry : groups.entrySet())
                     {
                        userGroup = entry.getValue();
                        groupList.put(userGroup.createJSONObject());
                     }

                  // create new JSON object and put the JSON array into it
                  JSONObject object = new JSONObject();
                  object.put("groups", groupList);

                  // write the JSON object to a file
                  DeviceFileManager.writeJSONToFile(object, devicePath);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      public ArrayList<UserGroup> getList()
         {
            ArrayList<UserGroup> list = new ArrayList<>();

            for (Map.Entry<String, UserGroup> entry : groups.entrySet())
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
      public UserGroupList(Parcel in)
         {
            //initialize your map before
            int size = in.readInt();
            for(int i = 0; i < size; i++){
               String key = in.readString();
               UserGroup value = in.readParcelable(UserGroup.class.getClassLoader());
               groups.put(key,value);
            }
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeInt(groups.size());
            for(Map.Entry<String,UserGroup> entry : groups.entrySet()){
               dest.writeString(entry.getKey());
               dest.writeParcelable(entry.getValue(),flags);
            }
         }

      public static final Parcelable.Creator<UserGroupList> CREATOR = new Parcelable.Creator<UserGroupList>()
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
