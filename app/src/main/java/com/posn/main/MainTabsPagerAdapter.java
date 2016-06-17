package com.posn.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.posn.R;
import com.posn.main.friends.UserFriendsFragment;
import com.posn.main.messages.UserConversationFragment;
import com.posn.main.notifications.UserNotificationsFragment;
import com.posn.main.settings.UserSettingsFragment;
import com.posn.main.wall.UserWallFragment;

import java.util.ArrayList;


public class MainTabsPagerAdapter extends FragmentPagerAdapter
   {
      SparseArray<Fragment> registeredFragments = new SparseArray<>();

      // declare variables
      Fragment wallTab, notificationTab, messagesTab, friendsTabs, settingsTab;
      Context context;

      ArrayList<View> views = new ArrayList<>();


      public MainTabsPagerAdapter(FragmentManager fm, Context context)
         {
            super(fm);
            this.context = context;
         }


      @Override
      public Fragment getItem(int index)
         {
            // load the tab that was selected.
            switch (index)
               {
                  // wall tab fragment
                  case 0:
                     if (wallTab == null)
                        {
                           wallTab = new UserWallFragment(0);
                        }
                     //registeredFragments.put(index, wallTab);

                     return wallTab;

                  // notification tab fragment
                  case 1:
                     if (notificationTab == null)
                        {
                           notificationTab = new UserNotificationsFragment();
                        }
                     return notificationTab;

                  // Messages tab fragment
                  case 2:
                     if (messagesTab == null)
                        {
                           messagesTab = new UserConversationFragment();
                        }
                     return messagesTab;

                  // Friends tab fragment
                  case 3:
                     if (friendsTabs == null)
                        {
                           friendsTabs = new UserFriendsFragment();
                        }
                     return friendsTabs;

                  // Settings tab fragment
                  case 4:
                     if (settingsTab == null)
                        {
                           settingsTab = new UserSettingsFragment();
                        }
                     return settingsTab;
               }

            return null;
         }

      @Override
      public Object instantiateItem(ViewGroup container, int position)
         {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
         }

      @Override
      public void destroyItem(ViewGroup container, int position, Object object)
         {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
         }

      @Override
      public int getCount()
         {
            // get item count - equal to number of tabs
            return 5;
         }

      public View createTabView(int resource, boolean showNotifications)
         {
            // inflate the custom icon view
            View v = LayoutInflater.from(context).inflate(R.layout.icon_test, null);

            // get the notification number textview and set it to gone
            TextView tv = (TextView) v.findViewById(R.id.notification_textview);
            tv.setTag(showNotifications);
            tv.setVisibility(View.GONE);

            // set the tab icon resource to the image view
            ImageView img = (ImageView) v.findViewById(R.id.icon_imageview);
            img.setImageResource(resource);

            // add the view to the arraylist
            views.add(v);

            return v;
         }

      public void updateTabIcon(int position, int resource)
         {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View v = views.get(position);

            ImageView img = (ImageView) v.findViewById(R.id.icon_imageview);
            img.setImageResource(resource);
         }

      public void updateNotificationNum(int position, int numNotifications)
         {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View v = views.get(position);

            TextView tv = (TextView) v.findViewById(R.id.notification_textview);
            boolean showNotifications = (boolean) tv.getTag();

            if (showNotifications && numNotifications > 0)
               {
                  tv.setVisibility(View.VISIBLE);
                  tv.setText(Integer.toString(numNotifications));
               }
            else
               {
                  tv.setVisibility(View.GONE);
               }
         }

      public Fragment getRegisteredFragment(int position)
         {
            return registeredFragments.get(position);
         }

   }
