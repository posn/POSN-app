package com.posn.main.wall.posts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;


public interface ListViewPostItem
   {
      int getViewType();

      View getView(LayoutInflater inflater, View convertView, ViewGroup parent);

      Date getDate();
   }
