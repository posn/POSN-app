package com.posn.main.notifications;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Friend;
import com.posn.datatypes.Notification;
import com.posn.main.MainActivity;

import java.util.ArrayList;


public class NotificationArrayAdapter extends ArrayAdapter<Notification>
   {
      static class NotificationViewHolder
         {
            TextView notification;
            TextView time;
            ImageView friendImage;
         }

      int TYPE_COMMENT = 0;
      int TYPE_FRIEND_REQUEST = 1;
      int TYPE_FRIEND_ACCEPTED = 2;

      private final Context context;
      private ArrayList<Notification> values;


      public NotificationArrayAdapter(Context context, ArrayList<Notification> values)
         {
            super(context, R.layout.listview_notification_item, values);
            this.context = context;
            this.values = values;
         }


      @Override
      public View getView(final int position, View convertView, ViewGroup parent)
         {
            NotificationViewHolder viewHolder;

            if (convertView == null)
               {
                  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                  convertView = inflater.inflate(R.layout.listview_notification_item, parent, false);

                  viewHolder = new NotificationViewHolder();

                  viewHolder.notification = (TextView) convertView.findViewById(R.id.notification_text);
                  viewHolder.time = (TextView) convertView.findViewById(R.id.time_text);
                  viewHolder.friendImage = (ImageView) convertView.findViewById(R.id.image);

                  convertView.setTag(viewHolder);
               }
            else
               {
                  viewHolder = (NotificationViewHolder) convertView.getTag();
               }

            // get the notification from the listview
            Notification notification = values.get(position);
            String notificationMessage, friendName;

            // get the Main Activity to look up which friend
            MainActivity main = (MainActivity) context;
            Friend friend = main.dataManager.masterFriendList.currentFriends.get(notification.friend);

            // add bold HTML tag to make the friend name bold
            friendName = "<b>" + friend.name + "</b>";

            // set the notification time
            viewHolder.time.setText(notification.date);

            // set the notification message
            if (notification.type == TYPE_FRIEND_REQUEST)
               {
                  notificationMessage = " wants to be your friend.";
               }
            else if (notification.type == TYPE_FRIEND_ACCEPTED)
               {
                  notificationMessage = " accepted your friend request.";
               }
            else if (notification.type == TYPE_COMMENT)
               {
                  notificationMessage = " commented on your post.";
               }
            else
               {
                  notificationMessage = null;
               }
            viewHolder.notification.setText(Html.fromHtml(friendName + notificationMessage));


            return convertView;
         }


      @Override
      public int getCount()
         {
            return values.size();
         }

   }
