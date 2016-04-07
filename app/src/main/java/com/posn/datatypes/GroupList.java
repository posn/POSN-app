package com.posn.datatypes;

import android.os.AsyncTask;

import com.posn.utility.FileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class GroupList
   {
      public HashMap<String, Group> groups;

      public GroupList()
         {
            groups = new HashMap<>();
         }

      public void loadGroupsFromFile(String fileName)
         {
            try
               {
                  // load friends list into JSON object
                  JSONObject data = FileManager.loadJSONObjectFromFile(fileName);

                  // get array of friends
                  JSONArray groupList = data.getJSONArray("groups");

                  // loop through array and parse individual friends
                  for (int n = 0; n < groupList.length(); n++)
                     {
                        // parse the friend
                        Group group = new Group();
                        group.parseJSONObject(groupList.getJSONObject(n));

                        // put into request or current friend list based on status
                        groups.put(group.ID, group);
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
            Group group;

            try
               {
                  // add all of the friends in the current friends list into the JSON array
                  for (Map.Entry<String, Group> entry : groups.entrySet())
                     {
                        group = entry.getValue();
                        groupList.put(group.createJSONObject());
                     }

                  // create new JSON object and put the JSON array into it
                  JSONObject object = new JSONObject();
                  object.put("groups", groupList);

                  // write the JSON object to a file
                  FileManager.writeJSONToFile(object, devicePath);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }
   }
