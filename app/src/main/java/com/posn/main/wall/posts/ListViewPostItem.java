package com.posn.main.wall.posts;

import android.view.LayoutInflater;
import android.view.View;


public interface ListViewPostItem
   {
      int getViewType();
      View getView(LayoutInflater inflater, View convertView);
   }
