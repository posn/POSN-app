package com.posn.main.main.wall.listview_items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;


/**
 * This interface class defines functions that all listview items in the wall fragment should have
 **/
public interface ListViewPostItem
   {
      int getViewType();

      View getView(LayoutInflater inflater, View convertView, ViewGroup parent);

      Date getDate();
   }
