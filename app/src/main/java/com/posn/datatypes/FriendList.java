package com.posn.datatypes;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import com.posn.utility.DeviceFileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FriendList implements Parcelable
   {
      public HashMap<String, Friend> currentFriends;
      public ArrayList<RequestedFriend> friendRequests;

      public FriendList()
         {
            currentFriends = new HashMap<>();
            friendRequests = new ArrayList<>();
         }

      public void loadFriendsListFromFile(String fileName)
         {
            currentFriends.clear();
            friendRequests.clear();
            try
               {
                  // load friends list into JSON object
                  JSONObject data = DeviceFileManager.loadJSONObjectFromFile(fileName);

                  // get array of friends
                  JSONArray friendsList = data.getJSONArray("friends");

                  // loop through array and parse individual friends
                  for (int n = 0; n < friendsList.length(); n++)
                     {
                        // parse the friend
                        Friend friend = new Friend();
                        friend.parseJSONObject(friendsList.getJSONObject(n));
                        currentFriends.put(friend.ID, friend);
                     }

                  JSONArray requestedFriendsList = data.getJSONArray("requests");
                  for (int n = 0; n < requestedFriendsList.length(); n++)
                     {
                        RequestedFriend friend = new RequestedFriend();
                        friend.parseJSONObject(requestedFriendsList.getJSONObject(n));
                        friendRequests.add(friend);
                     }

               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      public void saveFriendsListToFileAsyncTask(final String devicePath)
         {
            // create new AsyncTask to execute function off main UI thread
            new AsyncTask<Void, Void, Void>()
               {
                  protected Void doInBackground(Void... params)
                     {
                        saveFriendsListToFile(devicePath);
                        return null;
                     }
               }.execute();
         }

      public void saveFriendsListToFile(String devicePath)
         {
            Friend friend;
            RequestedFriend requestedFriend;
            try
               {
                  JSONObject object = new JSONObject();
                  JSONArray friendsList = new JSONArray();


                  // add all of the friends in the current friends list into the JSON array
                  for (Map.Entry<String, Friend> entry : currentFriends.entrySet())
                     {
                        friend = entry.getValue();
                        friendsList.put(friend.createJSONObject());
                     }
                  object.put("friends", friendsList);


                  // add all of the friends in the friend request list into the JSON array
                  JSONArray requestedFriendsList = new JSONArray();

                  System.out.println("SIZE: " + friendRequests.size());
                  for (int i = 0; i < friendRequests.size(); i++)
                     {
                        requestedFriend = friendRequests.get(i);
                        requestedFriendsList.put(requestedFriend.createJSONObject());
                     }
                  object.put("requests", requestedFriendsList);

                  // create new JSON object and put the JSON array into it

                  // write the JSON object to a file
                  DeviceFileManager.writeJSONToFile(object, devicePath);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      // Parcelling part
      public FriendList(Parcel in)
         {
            // get requested friends
            friendRequests = in.readArrayList(RequestedFriend.class.getClassLoader());

            //initialize your map before
            int size = in.readInt();
            for(int i = 0; i < size; i++){
               String key = in.readString();
               Friend value = in.readParcelable(Friend.class.getClassLoader());
               currentFriends.put(key,value);
            }
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeList(friendRequests);

            dest.writeInt(currentFriends.size());
            for(Map.Entry<String,Friend> entry : currentFriends.entrySet()){
               dest.writeString(entry.getKey());
               dest.writeParcelable(entry.getValue(),flags);
            }
         }

      public static final Parcelable.Creator<FriendList> CREATOR = new Parcelable.Creator<FriendList>()
         {
            public FriendList createFromParcel(Parcel in)
               {
                  return new FriendList(in);
               }

            public FriendList[] newArray(int size)
               {
                  return new FriendList[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }
