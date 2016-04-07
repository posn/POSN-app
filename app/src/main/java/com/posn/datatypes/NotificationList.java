package com.posn.datatypes;

import com.posn.utility.FileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class NotificationList
   {
      public ArrayList<Notification> notifications;

      public NotificationList()
         {
            notifications = new ArrayList<>();
         }

      public void loadNotificationsFromFile(String fileName)
         {
            // open the file
            try
               {
                  JSONObject data = FileManager.loadJSONObjectFromFile(fileName);

                  JSONArray notificationsArray = data.getJSONArray("notifications");

                  for (int n = 0; n < notificationsArray.length(); n++)
                     {
                        Notification notification = new Notification();
                        notification.parseJSONObject(notificationsArray.getJSONObject(n));

                        notifications.add(notification);
                     }
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }
      public void saveNotificationsToFile(String devicePath)
         {
            JSONArray notificationList = new JSONArray();

            try
               {
                  for(int i = 0; i < notifications.size(); i++)
                     {
                        Notification notification = notifications.get(i);
                        notificationList.put(notification.createJSONObject());
                     }

                  JSONObject object = new JSONObject();
                  object.put("notifications", notificationList);


                  FileManager.writeJSONToFile(object, devicePath);

               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }
   }
