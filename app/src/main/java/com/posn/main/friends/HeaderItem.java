package com.posn.main.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.posn.R;
import com.posn.main.friends.FriendsArrayAdapter.RowType;


public class HeaderItem implements ListViewFriendItem
   {

      private final String name;
      private boolean isClickable = false;

      public HeaderItem(String name)
         {
            this.name = name;
         }


      @Override
      public int getViewType()
         {
            return RowType.HEADER_ITEM.ordinal();
         }

      @Override
      public boolean isClickable()
         {
            return isClickable;
         }

      @Override
      public View getView(LayoutInflater inflater, View convertView)
         {
            View view = inflater.inflate(R.layout.listview_friend_header_item, null);

            TextView text = (TextView) view.findViewById(R.id.separator);
            text.setText(name);

            return view;
         }

      @Override
      public String getName()
         {
            return name;
         }

      @Override
      public boolean equals(Object o)
         {
            if (!(o instanceof HeaderItem)) return false;
            HeaderItem other = (HeaderItem) o;
            return (this.name == other.name);
         }


   }
