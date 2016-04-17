package com.posn.main.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.posn.R;
import com.posn.main.friends.FriendsArrayAdapter.RowType;


public class NoFriendItem implements ListViewFriendItem
   {

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
      public View getView(LayoutInflater inflater, View convertView)
         {
            View view;

            view = inflater.inflate(R.layout.listview_friend_none_item, null);
            TextView text = (TextView) view.findViewById(R.id.list_content2);
            text.setText(message);

            return view;
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

      @Override
      public boolean equals(Object o)
         {
            if (!(o instanceof NoFriendItem))
               return false;
            NoFriendItem other = (NoFriendItem) o;
            return true;
         }

   }
