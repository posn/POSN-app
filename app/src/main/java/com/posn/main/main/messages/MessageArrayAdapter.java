package com.posn.main.main.messages;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Conversation;
import com.posn.main.main.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


class MessageViewHolder
   {
      TextView friendName;
      TextView lastMessage;
      TextView time;
      ImageView friendImage;
   }


public class MessageArrayAdapter extends ArrayAdapter<Conversation>
   {

      private final Context context;
      private ArrayList<Conversation> values;
      ArrayList<Conversation> selectedContacts = new ArrayList<Conversation>();
      MessageViewHolder mViewHolder = null;


      public MessageArrayAdapter(Context context, ArrayList<Conversation> values)
         {
            super(context, R.layout.listview_message_item, values);
            this.context = context;
            this.values = values;
         }


      @Override
      public View getView(final int position, View convertView, ViewGroup parent)
         {

            if (convertView == null)
               {
                  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                  convertView = inflater.inflate(R.layout.listview_message_item, parent, false);

                  mViewHolder = new MessageViewHolder();

                  mViewHolder.friendName = (TextView) convertView.findViewById(R.id.name);
                  mViewHolder.time = (TextView) convertView.findViewById(R.id.time);
                  mViewHolder.lastMessage = (TextView) convertView.findViewById(R.id.last_message);
                  mViewHolder.friendImage = (ImageView) convertView.findViewById(R.id.image);

                  convertView.setTag(mViewHolder);

               }

            else
               {

                  mViewHolder = (MessageViewHolder) convertView.getTag();

               }
            Conversation conversation = values.get(position);
            String friendName = ((MainActivity)context).dataManager.friendManager.currentFriends.get(conversation.friend).name;


            friendName = "<b>" + friendName + "</b>";

            mViewHolder.friendName.setText(Html.fromHtml(friendName));

            SimpleDateFormat ft = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

           // mViewHolder.time.setText(ft.format(message.date));
            mViewHolder.time.setText(conversation.date);
            mViewHolder.lastMessage.setText(conversation.lastMessage);

            return convertView;
         }


      @Override
      public int getCount()
         {
            return values.size();
         }


      @Override
      public boolean isEnabled(int position)
         {
            return true;
         }

   }
