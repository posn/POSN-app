package com.posn.datatypes;

import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.utility.DeviceFileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FriendList
   {
      public HashMap<String, Friend> currentFriends;
      public ArrayList<Friend> friendRequests;

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

                        // put into request or current friend list based on status
                        if (friend.status == Constants.STATUS_ACCEPTED || friend.status == Constants.STATUS_PENDING)
                           {
                              currentFriends.put(friend.id, friend);
                           }
                        else
                           {
                              friendRequests.add(friend);
                           }
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
            JSONArray friendsList = new JSONArray();
            Friend friend;

            try
               {
                  // add all of the friends in the current friends list into the JSON array
                  for (Map.Entry<String, Friend> entry : currentFriends.entrySet())
                     {
                        friend = entry.getValue();
                        friendsList.put(friend.createJSONObject());
                     }

                  // add all of the friends in the friend request list into the JSON array
                  for (int i = 0; i < friendRequests.size(); i++)
                     {
                        friend = friendRequests.get(i);
                        friendsList.put(friend.createJSONObject());
                     }

                  // create new JSON object and put the JSON array into it
                  JSONObject object = new JSONObject();
                  object.put("friends", friendsList);

                  // write the JSON object to a file
                  DeviceFileManager.writeJSONToFile(object, devicePath);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }
   }
