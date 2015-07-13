package com.posn.main.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Friend;
import com.posn.main.friends.FriendsArrayAdapter.RowType;


public class PendingFriendItem implements ListViewFriendItem
   {

      private final Friend friend;
      private boolean isClickable = false;


      public PendingFriendItem(Friend friend)
         {
            this.friend = friend;
         }


      @Override
      public int getViewType()
         {
            return RowType.PENDING_FRIEND_ITEM.ordinal();
         }


      @Override
      public View getView(LayoutInflater inflater, View convertView)
         {
            View view;

            view = (View) inflater.inflate(R.layout.listview_friend_pending_item, null);

            TextView text1 = (TextView) view.findViewById(R.id.name);
            text1.setText(friend.name);

            return view;
         }


      @Override
      public boolean isClickable()
         {
            return isClickable;
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
