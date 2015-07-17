package com.posn.asynctasks.friends;

import com.posn.datatypes.Friend;

import java.util.ArrayList;


public interface AsyncResponseFriends
   {
      void loadingFriendsFinished(ArrayList<Friend> friendList, ArrayList<Friend> friendRequestList);
   }