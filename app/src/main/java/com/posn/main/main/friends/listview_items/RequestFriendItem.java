package com.posn.main.main.friends.listview_items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.RequestedFriend;
import com.posn.main.main.friends.FriendsArrayAdapter.RowType;

/**
 * This class creates a new friendID request listview item for the Friends list listview.
 * Implements the functions defined in the ListViewFriendItem interface.
 * Uses a viewholder pattern: https://developer.android.com/training/improving-layouts/smooth-scrolling.html
 **/
public class RequestFriendItem implements ListViewFriendItem
   {
      // view holder class
      static class RequestFriendViewHolder
         {
            Button acceptButton;
            Button declineButton;
            TextView friendNameText;
         }

      // listview item data variables
      private final RequestedFriend friend;
      View.OnClickListener confirm;
      View.OnClickListener decline;


      public RequestFriendItem(View.OnClickListener confirm, View.OnClickListener decline, RequestedFriend friend)
         {
            this.friend = friend;
            this.confirm = confirm;
            this.decline = decline;
         }


      @Override
      public int getViewType()
         {
            return RowType.ACCEPTED_FRIEND_ITEM.ordinal();
         }


      @Override
      public View getView(LayoutInflater inflater, View convertView, ViewGroup parent)
         {
            RequestFriendViewHolder viewHolder;

            if (convertView != null && !(convertView.getTag() instanceof RequestFriendViewHolder))
               {
                  convertView = null;
               }

            // check if the view was already created
            if (convertView == null)
               {
                  // create a new view by inflating the layout
                  convertView = inflater.inflate(R.layout.listview_friend_request_item, parent, false);

                  // well set up the ViewHolder
                  viewHolder = new RequestFriendViewHolder();
                  viewHolder.friendNameText = (TextView) convertView.findViewById(R.id.name);
                  viewHolder.acceptButton = (Button) convertView.findViewById(R.id.confirm_button);
                  viewHolder.declineButton = (Button) convertView.findViewById(R.id.decline_button);

                  // store the holder with the view.
                  convertView.setTag(viewHolder);
               }
            else
               {
                  viewHolder = (RequestFriendViewHolder) convertView.getTag();
               }

            // set the data into the views
            viewHolder.friendNameText.setText(friend.name);

            viewHolder.acceptButton.setTag(friend);
            viewHolder.declineButton.setTag(friend);
            viewHolder.acceptButton.setOnClickListener(confirm);
            viewHolder.declineButton.setOnClickListener(decline);

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
            if (!(o instanceof RequestFriendItem)) return false;
            RequestFriendItem other = (RequestFriendItem) o;
            return (this.friend.name.equals(other.friend.name));
         }
   }


