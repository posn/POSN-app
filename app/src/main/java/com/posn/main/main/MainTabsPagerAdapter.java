package com.posn.main.main;

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
import com.posn.main.main.friends.UserFriendsFragment;
import com.posn.main.main.messages.UserConversationFragment;
import com.posn.main.main.notifications.UserNotificationsFragment;
import com.posn.main.main.settings.UserSettingsFragment;
import com.posn.main.main.wall.UserWallFragment;

import java.util.ArrayList;


public class MainTabsPagerAdapter extends FragmentPagerAdapter
   {

      // declare variables
      Context context;
      ArrayList<View> views = new ArrayList<>();
      SparseArray<Fragment> registeredFragments = new SparseArray<>();


      public MainTabsPagerAdapter(FragmentManager fm, Context context)
         {
            super(fm);
            this.context = context;
         }


      @Override
      public Fragment getItem(int index)
         {
            // check if the fragment needs to be created
            if (registeredFragments.get(index) == null)
               {
                  switch (index)
                     {
                        // create a wall tab fragment
                        case 0:
                           UserWallFragment wallFrag = new UserWallFragment();
                           wallFrag.setFragNum(0);
                           registeredFragments.put(0, wallFrag);
                           break;

                        // create a notifications tab fragment
                        case 1:
                           UserNotificationsFragment notificationsFrag = new UserNotificationsFragment();
                           notificationsFrag.setFragNum(1);
                           registeredFragments.put(1, notificationsFrag);
                           break;

                        // create a conversations tab fragment
                        case 2:
                           UserConversationFragment conversationFrag = new UserConversationFragment();
                           conversationFrag.setFragNum(2);
                           registeredFragments.put(2, conversationFrag);
                           break;

                        // create a friends tab fragment
                        case 3:
                           UserFriendsFragment friendsFrag = new UserFriendsFragment();
                           friendsFrag.setFragNum(3);
                           registeredFragments.put(3, friendsFrag);
                           break;

                        // create a settings tab fragment
                        case 4:
                           UserSettingsFragment settingsFrag = new UserSettingsFragment();
                           settingsFrag.setFragNum(4);
                           registeredFragments.put(4, settingsFrag);
                           break;
                     }
               }

            // return the fragment (returns the existing fragment or the newly created one)
            return registeredFragments.get(index);
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
                  String text = Integer.toString(numNotifications);
                  tv.setText(text);
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
