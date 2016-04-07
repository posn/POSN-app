package com.posn.main.messages;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Message;
import com.posn.main.messages.DiscussArrayAdapter.RowType;


public class ConversationMessageItem implements ListViewConversationItem
   {
      private final int FRIEND_MESSAGE = 0;
      private final int USER_MESSAGAGE = 1;

      private Message comment;
      private boolean isClickable = true;
      private View.OnClickListener deleteListener;
      private Button deleteButton;

      public ConversationMessageItem(Message comment)
         {
            this.comment = comment;
         }


      @Override
      public int getViewType()
         {
            return RowType.CONVERSATION_MESSAGE_ITEM.ordinal();
         }


      @Override
      public View getView(LayoutInflater inflater, View convertView, ViewGroup parent)
         {
            View row = convertView;
            if (row == null)
               {
                  row = inflater.inflate(R.layout.listview_message_converstation_item, parent, false);
               }

            LinearLayout wrapper = (LinearLayout) row.findViewById(R.id.wrapper);
            LinearLayout wrapper2 = (LinearLayout) row.findViewById(R.id.wrapper2);
            LinearLayout wrapper3 = (LinearLayout) row.findViewById(R.id.wrapper3);

            TextView messageTextView = (TextView) row.findViewById(R.id.comment);
            TextView timeTextView = (TextView) row.findViewById(R.id.time);

            messageTextView.setText(comment.message);

            wrapper2.setBackgroundResource(comment.type == FRIEND_MESSAGE ? R.drawable.out_message_bg : R.drawable.in_message_bg);

            wrapper.setGravity(comment.type == FRIEND_MESSAGE ? Gravity.START : Gravity.END);
            wrapper3.setGravity(comment.type == FRIEND_MESSAGE ? Gravity.START : Gravity.END);


            timeTextView.setText(comment.getTimeString());

            return row;
         }

      @Override
      public boolean isClickable()
         {
            return isClickable;
         }
   }
