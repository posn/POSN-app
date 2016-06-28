package com.posn.main.main.messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;


public class DiscussArrayAdapter extends ArrayAdapter<ListViewConversationItem>
   {
      private LayoutInflater mInflater;

      public enum RowType
         {
            CONVERSATION_MESSAGE_ITEM, CONVERSATION_HEADER_ITEM
         }

      public DiscussArrayAdapter(Context context, ArrayList<ListViewConversationItem> messageList)
         {
            super(context, 0, messageList);
            mInflater = LayoutInflater.from(context);
         }

      @Override
      public int getViewTypeCount()
         {
            return RowType.values().length;

         }

      @Override
      public int getItemViewType(int position)
         {
            return getItem(position).getViewType();
         }


      @Override
      public View getView(int position, View convertView, ViewGroup parent)
         {
            return getItem(position).getView(mInflater, convertView, parent);
         }

      @Override
      public boolean isEnabled(int position)
         {
            return (!getItem(position).isClickable());
         }
   }
