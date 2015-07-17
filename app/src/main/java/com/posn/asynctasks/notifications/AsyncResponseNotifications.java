package com.posn.asynctasks.notifications;

import com.posn.datatypes.Friend;

import java.util.ArrayList;


public interface AsyncResponseNotifications
   {
      void loadingFriendsFinished(ArrayList<Friend> friendList, ArrayList<Friend> friendRequestList);
   }