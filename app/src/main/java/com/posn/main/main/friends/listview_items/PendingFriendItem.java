package com.posn.main.main.friends.listview_items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.RequestedFriend;
import com.posn.main.main.friends.FriendsArrayAdapter.RowType;


/**
 * This class creates a pending friend request listview item for the Friends list listview
 * Implements the functions defined in the ListViewFriendItem interface
 * Uses a viewholder pattern: https://developer.android.com/training/improving-layouts/smooth-scrolling.html
 **/
public class PendingFriendItem implements ListViewFriendItem
   {
      // view holder class
      static class PendingFriendViewHolder
         {
            TextView friendNameText;
         }

      // listview item data variables
      private final RequestedFriend friend;


      public PendingFriendItem(RequestedFriend friend)
         {
            this.friend = friend;
         }


      @Override
      public int getViewType()
         {
            return RowType.PENDING_FRIEND_ITEM.ordinal();
         }


      @Override
      public View getView(LayoutInflater inflater, View convertView, ViewGroup parent)
         {
            PendingFriendViewHolder viewHolder;

            if (convertView != null && !(convertView.getTag() instanceof PendingFriendViewHolder))
               {
                  convertView = null;
               }

            // check if the view was already created
            if (convertView == null)
               {
                  // create a new view by inflating the layout
                  convertView = inflater.inflate(R.layout.listview_friend_pending_item, parent, false);

                  // well set up the ViewHolder
                  viewHolder = new PendingFriendViewHolder();
                  viewHolder.friendNameText = (TextView) convertView.findViewById(R.id.name);

                  // store the holder with the view.
                  convertView.setTag(viewHolder);
               }
            else
               {
                  viewHolder = (PendingFriendViewHolder) convertView.getTag();
               }

            // set the data into the views
            viewHolder.friendNameText.setText(friend.name);

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
            return friend.name;
         }

      @Override
      public boolean equals(Object o)
         {
            if (!(o instanceof PendingFriendItem)) return false;
            PendingFriendItem other = (PendingFriendItem) o;
            return (this.friend.name == other.friend.name);
         }

   }
