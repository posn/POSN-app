package com.posn.main.main.friends.listview_items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Friend;
import com.posn.main.main.friends.FriendsArrayAdapter.RowType;


/**
 * This class creates an accepted friendID listview item for the Friends list listview.
 * Implements the functions defined in the ListViewFriendItem interface.
 * Uses a viewholder pattern: https://developer.android.com/training/improving-layouts/smooth-scrolling.html
 **/
public class AcceptedFriendItem implements ListViewFriendItem
   {
      // view holder class
      static class AcceptedFriendViewHolder
         {
            Button deleteButton;
            TextView friendNameText;
         }

      // listview item data variables
      private final Friend friend;
      private View.OnClickListener deleteListener;


      public AcceptedFriendItem(View.OnClickListener deleteListener, Friend friend)
         {
            this.friend = friend;
            this.deleteListener = deleteListener;
         }


      @Override
      public int getViewType()
         {
            return RowType.ACCEPTED_FRIEND_ITEM.ordinal();
         }


      @Override
      public View getView(LayoutInflater inflater, View convertView, ViewGroup parent)
         {
            AcceptedFriendViewHolder viewHolder;

            if (convertView != null && !(convertView.getTag() instanceof AcceptedFriendViewHolder))
               {
                  convertView = null;
               }

            // check if the view was already created
            if (convertView == null)
               {
                  // create a new view by inflating the layout
                  convertView = inflater.inflate(R.layout.listview_friend_accepted_item, parent, false);

                  // well set up the ViewHolder
                  viewHolder = new AcceptedFriendViewHolder();
                  viewHolder.deleteButton = (Button) convertView.findViewById(R.id.delete_button);
                  viewHolder.friendNameText = (TextView) convertView.findViewById(R.id.name);

                  // store the holder with the view.
                  convertView.setTag(viewHolder);
               }
            else
               {
                  viewHolder = (AcceptedFriendViewHolder) convertView.getTag();
               }

            // set the data into the views
            viewHolder.friendNameText.setText(friend.name);

            viewHolder.deleteButton.setTag(friend);
            viewHolder.deleteButton.setTag(friend);
            viewHolder.deleteButton.setOnClickListener(deleteListener);

            return convertView;
         }


      @Override
      public boolean isClickable()
         {
            return true;
         }

      @Override
      public String getName()
         {
            return friend.name;
         }

      public boolean equals(Object o)
         {
            if (!(o instanceof AcceptedFriendItem)) return false;
            AcceptedFriendItem other = (AcceptedFriendItem) o;
            return (this.friend.name == other.friend.name);
         }

   }
