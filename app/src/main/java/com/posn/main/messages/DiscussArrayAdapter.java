package com.posn.main.messages;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.ConversationMessage;

import java.util.ArrayList;


public class DiscussArrayAdapter extends ArrayAdapter<ConversationMessage>
   {
      private final int FRIEND_MESSAGE = 0;
      private final int USER_MESSAGAGE = 1;

      private ArrayList<ConversationMessage> messageList;

      public DiscussArrayAdapter(Context context, ArrayList<ConversationMessage> messageList, int textViewResourceId)
         {
            super(context, textViewResourceId);
            this.messageList = messageList;
         }

      /*
      @Override
      public void add(ConversationMessage object)
         {
            messageList.add(object);
            super.add(object);
         }

      @Override
      public void insert(ConversationMessage object, int position)
         {
            messageList.add(position, object);
            super.add(object);
         }
         */

      public int getCount()
         {
            return this.messageList.size();
         }


      public ConversationMessage getItem(int index)
         {
            return this.messageList.get(index);
         }


      public View getView(int position, View convertView, ViewGroup parent)
         {
            View row = convertView;
            if (row == null)
               {
                  LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                  row = inflater.inflate(R.layout.listview_message_converstation_item, parent, false);
               }

            LinearLayout wrapper = (LinearLayout) row.findViewById(R.id.wrapper);
            LinearLayout wrapper2 = (LinearLayout) row.findViewById(R.id.wrapper2);
            LinearLayout wrapper3 = (LinearLayout) row.findViewById(R.id.wrapper3);

            ConversationMessage comment = getItem(position);

            TextView messageTextView = (TextView) row.findViewById(R.id.comment);

            messageTextView.setText(comment.message);

            wrapper2.setBackgroundResource(comment.type == FRIEND_MESSAGE ? R.drawable.out_message_bg : R.drawable.in_message_bg);

            wrapper.setGravity(comment.type == FRIEND_MESSAGE ? Gravity.START : Gravity.END);
            wrapper3.setGravity(comment.type == FRIEND_MESSAGE ? Gravity.START : Gravity.END);

            return row;
         }


      public Bitmap decodeToBitmap(byte[] decodedByte)
         {
            return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
         }

   }
