package com.posn.main.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Friend;
import com.posn.main.friends.FriendsArrayAdapter.RowType;


public class AcceptedFriendItem implements ListViewFriendItem
   {

      private final Friend friend;
      private boolean isClickable = true;
      private View.OnClickListener deleteListener;
      private Button deleteButton;

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
      public View getView(LayoutInflater inflater, View convertView)
         {
            View view = convertView;

            view = (View) inflater.inflate(R.layout.listview_friend_accepted_item, null);

            TextView text1 = (TextView) view.findViewById(R.id.name);
            text1.setText(friend.name);

            deleteButton = (Button) view.findViewById(R.id.delete_button);
            deleteButton.setTag(friend);
            deleteButton.setOnClickListener(deleteListener);

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

      public boolean equals(Object o)
         {
            if (!(o instanceof AcceptedFriendItem)) return false;
            AcceptedFriendItem other = (AcceptedFriendItem) o;
            return (this.friend.name == other.friend.name);
         }

   }
