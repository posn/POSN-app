package com.posn.asynctasks.friends;

import com.posn.datatypes.Friend;

import java.util.ArrayList;
import java.util.HashMap;


public interface AsyncResponseFriends
   {
      void loadingFriendsFinished(HashMap<String, Friend> friendList, ArrayList<Friend> friendRequestList);
   }