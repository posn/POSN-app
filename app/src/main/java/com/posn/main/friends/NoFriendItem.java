package com.posn.main.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.posn.R;
import com.posn.main.friends.FriendsArrayAdapter.RowType;


/**
 * This class creates a listview item that contains a message (ex. No New Friend Requests) for the Friends list listview
 * Implements the functions defined in the ListViewFriendItem interface
 * Uses a viewholder pattern: https://developer.android.com/training/improving-layouts/smooth-scrolling.html
 **/
public class NoFriendItem implements ListViewFriendItem
   {
      // view holder class
      static class NoFriendViewHolder
         {
            TextView messageText;
         }

      // listview item data variables
      String message;


      public NoFriendItem(String message)
         {
            this.message = message;
         }


      @Override
      public int getViewType()
         {
            return RowType.NO_REQUEST_FRIEND_ITEM.ordinal();
         }


      @Override
      public View getView(LayoutInflater inflater, View convertView, ViewGroup parent)
         {
            NoFriendViewHolder viewHolder;

            if (convertView != null && !(convertView.getTag() instanceof NoFriendViewHolder))
               {
                  convertView = null;
               }

            // check if the view was already created
            if (convertView == null)
               {
                  // create a new view by inflating the layout
                  convertView = inflater.inflate(R.layout.listview_friend_none_item, parent, false);

                  // well set up the ViewHolder
                  viewHolder = new NoFriendViewHolder();
                  viewHolder.messageText = (TextView) convertView.findViewById(R.id.list_content2);

                  // store the holder with the view.
                  convertView.setTag(viewHolder);
               }
            else
               {
                  viewHolder = (NoFriendViewHolder) convertView.getTag();
               }

            // set the data into the views
            viewHolder.messageText.setText(message);

            return convertView;
         }

      @Override
      public boolean isClickable()
         {
            return false;
         }

      @Override
      public String getName()
         {
            return "None";
         }


   }
