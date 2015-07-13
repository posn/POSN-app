package com.posn.main.friends;

import android.view.LayoutInflater;
import android.view.View;


public interface ListViewFriendItem
   {

      public int getViewType();


      public View getView(LayoutInflater inflater, View convertView);


      public boolean isClickable();

      public String getName();

      public boolean equals(Object o);
   }
