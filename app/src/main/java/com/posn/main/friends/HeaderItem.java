package com.posn.main.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.posn.R;
import com.posn.main.friends.FriendsArrayAdapter.RowType;


/**
 * This class creates a header listview item for the Friends list listview and implements the functions defined in the ListViewFriendItem interface
 * Uses a viewholder pattern: https://developer.android.com/training/improving-layouts/smooth-scrolling.html
 **/
public class HeaderItem implements ListViewFriendItem
   {
      // view holder class
      static class HeaderViewHolder
         {
            TextView headerText;
         }

      // listview item data variables
      private String title;


      public HeaderItem(String title)
         {
            this.title = title;
         }


      @Override
      public int getViewType()
         {
            return RowType.HEADER_ITEM.ordinal();
         }

      @Override
      public boolean isClickable()
         {
            return false;
         }

      @Override
      public View getView(LayoutInflater inflater, View convertView, ViewGroup parent)
         {
            HeaderViewHolder viewHolder;

            if (convertView != null && !(convertView.getTag() instanceof HeaderViewHolder))
               {
                  convertView = null;
               }

            // check if the view was already created
            if (convertView == null)
               {
                  // create a new view by inflating the layout
                  convertView = inflater.inflate(R.layout.listview_friend_header_item, parent, false);

                  // well set up the ViewHolder
                  viewHolder = new HeaderViewHolder();
                  viewHolder.headerText = (TextView) convertView.findViewById(R.id.header_title_text);

                  // store the holder with the view.
                  convertView.setTag(viewHolder);
               }
            else
               {
                  viewHolder = (HeaderViewHolder) convertView.getTag();
               }

            // set the data into the views
            viewHolder.headerText.setText(title);

            return convertView;
         }

      @Override
      public String getName()
         {
            return title;
         }

      @Override
      public boolean equals(Object o)
         {
            if (!(o instanceof HeaderItem)) return false;
            HeaderItem other = (HeaderItem) o;
            return (this.title.equals(other.title));
         }


   }
