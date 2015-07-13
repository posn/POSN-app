package com.posn.main.friends;

import android.view.LayoutInflater;
import android.view.View;

import com.posn.R;
import com.posn.main.friends.FriendsArrayAdapter.RowType;


public class NoRequestFriendItem implements ListViewFriendItem
   {

      private boolean isClickable = false;


      public NoRequestFriendItem()
         {
         }


      @Override
      public int getViewType()
         {
            return RowType.NO_REQUEST_FRIEND_ITEM.ordinal();
         }


      @Override
      public View getView(LayoutInflater inflater, View convertView)
         {
            View view;

            view = (View) inflater.inflate(R.layout.listview_friend_none_item, null);

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
            return "None";
         }

      @Override
      public boolean equals(Object o)
         {
            if (!(o instanceof NoRequestFriendItem))
               return false;
            NoRequestFriendItem other = (NoRequestFriendItem) o;
            return true;
         }

   }
