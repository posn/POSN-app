package com.posn.main.main.friends.listview_items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * This interface class defines functions that all listview items in the friends fragment should have
 **/
public interface ListViewFriendItem
   {
      int getViewType();

      View getView(LayoutInflater inflater, View convertView, ViewGroup parent);

      boolean isClickable();

      String getName();

      boolean equals(Object o);
   }
