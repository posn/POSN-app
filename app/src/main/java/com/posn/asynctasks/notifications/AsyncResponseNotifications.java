package com.posn.asynctasks.notifications;

import com.posn.datatypes.Notification;

import java.util.ArrayList;


public interface AsyncResponseNotifications
   {
      void loadingNotificationsFinished(ArrayList<Notification> notificationList);
   }