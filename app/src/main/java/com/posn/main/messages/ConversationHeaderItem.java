package com.posn.main.messages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.posn.R;
import com.posn.main.messages.DiscussArrayAdapter.RowType;

public class ConversationHeaderItem implements ListViewConversationItem
   {

      private final String name;
      private boolean isClickable = false;

      public ConversationHeaderItem(String name)
         {
            this.name = name;
         }


      @Override
      public int getViewType()
         {
            return RowType.CONVERSATION_HEADER_ITEM.ordinal();
         }

      @Override
      public View getView(LayoutInflater inflater, View convertView, ViewGroup parent)
         {
            View view;
            if (convertView == null)
               {
                  view = inflater.inflate(R.layout.listview_message_conversation_header, parent, false);
               }
            else
               {
                  view = convertView;
               }

            TextView text = (TextView) view.findViewById(R.id.separator);
            text.setText(name);

            return view;
         }

      @Override
      public boolean isClickable()
         {
            return isClickable;
         }
   }
