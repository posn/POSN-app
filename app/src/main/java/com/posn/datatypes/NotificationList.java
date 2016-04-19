package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import com.posn.utility.DeviceFileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class NotificationList implements Parcelable
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
                  JSONObject data = DeviceFileManager.loadJSONObjectFromFile(fileName);

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


                  DeviceFileManager.writeJSONToFile(object, devicePath);

               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      // Parcelling part
      public NotificationList(Parcel in)
         {
            this.notifications = in.readArrayList(Notification.class.getClassLoader());

         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeList(this.notifications);

         }

      public static final Parcelable.Creator<NotificationList> CREATOR = new Parcelable.Creator<NotificationList>()
         {
            public NotificationList createFromParcel(Parcel in)
               {
                  return new NotificationList(in);
               }

            public NotificationList[] newArray(int size)
               {
                  return new NotificationList[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }
