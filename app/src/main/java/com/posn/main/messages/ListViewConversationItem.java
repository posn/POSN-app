package com.posn.main.messages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public interface ListViewConversationItem
   {
      public int getViewType();
      public View getView(LayoutInflater inflater, View convertView, ViewGroup parent);
      public boolean isClickable();
   }
