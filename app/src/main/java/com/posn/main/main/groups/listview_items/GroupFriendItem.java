package com.posn.main.main.groups.listview_items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Friend;
import com.posn.main.main.groups.ManageGroupArrayAdapter;


/**
 * This class creates an accepted friend listview item for the Friends list listview.
 * Implements the functions defined in the ListViewFriendItem interface.
 * Uses a viewholder pattern: https://developer.android.com/training/improving-layouts/smooth-scrolling.html
 **/
public class GroupFriendItem implements ListViewManageGroupItem
   {
      // view holder class
      static class GroupFriendViewHolder
         {
            Button addRemoveButton;
            TextView friendNameText;
         }

      // listview item data variables
      private final Friend friend;
      private boolean inGroup;
      private View.OnClickListener addRemoveListener;


      public GroupFriendItem(View.OnClickListener addRemoveListener, Friend friend, boolean inGroup)
         {
            this.friend = friend;
            this.friend.selected = inGroup;
            this.inGroup = inGroup;
            this.addRemoveListener = addRemoveListener;
         }


      @Override
      public int getViewType()
         {
            return ManageGroupArrayAdapter.ManageGroupRowType.FRIEND_ITEM.ordinal();
         }


      @Override
      public View getView(LayoutInflater inflater, View convertView, ViewGroup parent)
         {
            GroupFriendViewHolder viewHolder;

            if (convertView != null && !(convertView.getTag() instanceof GroupFriendViewHolder))
               {
                  convertView = null;
               }

            // check if the view was already created
            if (convertView == null)
               {
                  // create a new view by inflating the layout
                  convertView = inflater.inflate(R.layout.listview_friend_accepted_item, parent, false);

                  // well set up the ViewHolder
                  viewHolder = new GroupFriendViewHolder();
                  viewHolder.addRemoveButton = (Button) convertView.findViewById(R.id.delete_button);
                  viewHolder.friendNameText = (TextView) convertView.findViewById(R.id.name);

                  // store the holder with the view.
                  convertView.setTag(viewHolder);
               }
            else
               {
                  viewHolder = (GroupFriendViewHolder) convertView.getTag();
               }

            // set the data into the views
            viewHolder.friendNameText.setText(friend.name);

            viewHolder.addRemoveButton.setTag(friend);
            viewHolder.addRemoveButton.setOnClickListener(addRemoveListener);

            if (friend.selected)
               {
                  viewHolder.addRemoveButton.setBackgroundResource(R.drawable.button_friend_delete_background);
                  viewHolder.addRemoveButton.setText("Remove");
               }
            else
               {
                  viewHolder.addRemoveButton.setBackgroundResource(R.drawable.button_friend_background);
                  viewHolder.addRemoveButton.setText("Add");
               }


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

      public boolean equals(Object o)
         {
            if (!(o instanceof GroupFriendItem)) return false;
            GroupFriendItem other = (GroupFriendItem) o;
            return (this.friend.name == other.friend.name);
         }

   }
