package com.posn.main.notifications;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Notification;
import com.posn.main.MainActivity;

import java.util.ArrayList;


/**
 * This fragment class implements the functionality for the notification fragment:
 * The detailed functionality is not implemented, this acts a skeleton to add the functionality
 * Currently implement: Listview that holds notification items, custom adapter
 **/
public class UserNotificationsFragment extends Fragment implements OnClickListener
   {
      int TYPE_COMMENT = 0;
      int TYPE_FRIEND_REQUEST = 1;
      int TYPE_FRIEND_ACCEPTED = 2;

      // declare variables
      ArrayList<Notification> notificationsList;
      ListView lv;
      NotificationArrayAdapter adapter;
      MainActivity main;
      TextView noNotificationsText;

      private int fragNum;
      private int newNotificationNum = 0;


      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            System.out.println("NOTIFICATIONS ON CREATE!!!!!!!!!!!!!!");

            // load the system tab layout
            View view = inflater.inflate(R.layout.fragment_user_notifications, container, false);

            // get the application
            main = (MainActivity) getActivity();

            // get the listview from the layout
            lv = (ListView) view.findViewById(R.id.listView1);

            noNotificationsText = (TextView) view.findViewById(R.id.notification_text);

            // get the notification list from the main activity
            notificationsList = main.dataManager.notificationList.notifications;

            // check if there are any notifications, if so then update listview
            if (notificationsList.size() > 0)
               {
                  updateNotifications();
               }

            adapter = new NotificationArrayAdapter(getActivity(), notificationsList);
            lv.setAdapter(adapter);

            return view;
         }


      @Override
      public void onResume()
         {
            super.onResume();

            if (!notificationsList.isEmpty())
               {
                  updateNotifications();
               }
         }

      public void setFragNum(int position)
         {
            fragNum = position;
         }


      @Override
      public void onClick(View arg0)
         {
         }




      public void createNotificationsList()
         {
         }

      public void updateNotifications()
         {
            System.out.println("CREATING NOTIFICATIONS!!! | " + notificationsList.size());

            createNotificationsList();

            if (notificationsList.size() > 0)
               {
                  noNotificationsText.setVisibility(View.GONE);
               }
            else
               {
                  noNotificationsText.setVisibility(View.VISIBLE);
               }


            // notify the adapter about the data change
            adapter.notifyDataSetChanged();
         }
   }