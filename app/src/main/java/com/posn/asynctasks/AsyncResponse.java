package com.posn.asynctasks;

import com.posn.datatypes.Friend;

import java.util.ArrayList;


public interface AsyncResponse
   {
      void loadingFinished(ArrayList<Friend> friendList, ArrayList<Friend> friendRequestList);
   }