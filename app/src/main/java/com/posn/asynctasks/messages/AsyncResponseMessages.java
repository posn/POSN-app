package com.posn.asynctasks.messages;

import com.posn.datatypes.Friend;

import java.util.ArrayList;


public interface AsyncResponseMessages
   {
      void loadingFriendsFinished(ArrayList<Friend> friendList, ArrayList<Friend> friendRequestList);
   }