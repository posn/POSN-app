package com.posn.main.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Friend;
import com.posn.main.friends.FriendsArrayAdapter.RowType;


public class RequestFriendItem implements ListViewFriendItem
   {

      private final Friend friend;
      private boolean isClickable = false;
      Button acceptButton, declineButton;
      View.OnClickListener confirm;
      View.OnClickListener decline;


      public RequestFriendItem(View.OnClickListener confirm, View.OnClickListener decline, Friend friend)
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
      public View getView(LayoutInflater inflater, View convertView)
         {
            View view;

            view = (View) inflater.inflate(R.layout.listview_friend_request_item, null);

            TextView text1 = (TextView) view.findViewById(R.id.name);
            text1.setText(friend.name);

            acceptButton = (Button) view.findViewById(R.id.confirm_button);
            declineButton = (Button) view.findViewById(R.id.decline_button);
            acceptButton.setTag(friend);
            declineButton.setTag(friend);

            acceptButton.setOnClickListener(confirm);
            declineButton.setOnClickListener(decline);
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
            if (!(o instanceof RequestFriendItem)) return false;
            RequestFriendItem other = (RequestFriendItem) o;
            return (this.friend.name == other.friend.name);
         }
   }


