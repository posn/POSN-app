package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import com.posn.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * <p>This class creates a list of notifcations are currently storedin an arraylist (Note: this is not fully implemented and may need to use a hashmap).
 *        Methods are included to read and write the data to and from a file.</p>
 * <p>Implements the methods defined by the ApplicationFile interface</p>
 * <p>Implements parcelable to easily pass this class between activities</p>
 **/
public class NotificationList implements Parcelable, ApplicationFile
   {
      public ArrayList<Notification> notifications = new ArrayList<>();;

      public NotificationList()
         {
         }


      @Override
      public void parseApplicationFileContents(String fileContents) throws JSONException
         {
            JSONObject data = new JSONObject(fileContents);

            JSONArray notificationsArray = data.getJSONArray("notifications");

            for (int n = 0; n < notificationsArray.length(); n++)
               {
                  Notification notification = new Notification();
                  notification.parseJSONObject(notificationsArray.getJSONObject(n));

                  notifications.add(notification);
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
            return Constants.notificationListFile;
         }

      @Override
      public String createApplicationFileContents() throws JSONException
         {
            JSONArray notificationList = new JSONArray();

            for (int i = 0; i < notifications.size(); i++)
               {
                  Notification notification = notifications.get(i);
                  notificationList.put(notification.createJSONObject());
               }

            JSONObject object = new JSONObject();
            object.put("notifications", notificationList);
            
            return object.toString();
         }

      // Parcelling part
      public NotificationList(Parcel in)
         {
            in.readList(notifications, Notification.class.getClassLoader());
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
