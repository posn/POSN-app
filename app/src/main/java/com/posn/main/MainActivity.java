package com.posn.main;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.posn.R;
import com.posn.application.POSNApplication;
import com.posn.datatypes.Post;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity
   {
      private ViewPager viewPager;
      private ActionBar actionBar;
      private MainTabsPagerAdapter tabsAdapter;
      public POSNApplication app;

      public int newWallPostsNum = 0;
      public int newNotificationNum = 0;
      public int newMessagesNum = 0;
      public int newFriendNum = 0;

      // data for wall fragment
      public ArrayList<Post> wallPostData = new ArrayList<>();

      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            // load the xml file for the logs
            setContentView(R.layout.activity_main);

            // get the action bar to set the title
            actionBar = getActionBar();

            // find the viewpager in the xml file
            viewPager = (ViewPager) findViewById(R.id.system_viewpager);

            tabsAdapter = new MainTabsPagerAdapter(this.getSupportFragmentManager(), this);
            viewPager.setAdapter(tabsAdapter);

            final TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);

            // set initial values
            tabLayout.getTabAt(0).setCustomView(tabsAdapter.getTabView(R.drawable.ic_wall_blue, newWallPostsNum, false));
            tabLayout.getTabAt(1).setCustomView(tabsAdapter.getTabView(R.drawable.ic_notification_gray, newNotificationNum, true));
            tabLayout.getTabAt(2).setCustomView(tabsAdapter.getTabView(R.drawable.ic_message_gray, newMessagesNum, true)); //setIcon(R.drawable.ic_message_gray);
            tabLayout.getTabAt(3).setCustomView(tabsAdapter.getTabView(R.drawable.ic_friends_gray, newFriendNum, true));
            tabLayout.getTabAt(4).setCustomView(tabsAdapter.getTabView(R.drawable.ic_settings_gray, 0, false));

            actionBar.setTitle("Wall");

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
            {
               @Override
               public void onTabSelected(TabLayout.Tab tab)
                  {
                     viewPager.setCurrentItem(tab.getPosition(), true);

                     System.out.println(tab.getPosition());
                     if (tab.getPosition() == 0)
                        {
                           actionBar.setTitle("Wall");
                           tabsAdapter.updateTab(tab.getPosition(), R.drawable.ic_wall_blue, newWallPostsNum, false);
                        }
                     else if (tab.getPosition() == 1)
                        {
                           actionBar.setTitle("Notifications");
                           tabsAdapter.updateTab(tab.getPosition(), R.drawable.ic_notification_blue, newNotificationNum, false);
                        }
                     else if (tab.getPosition() == 2)
                        {
                           actionBar.setTitle("Messages");
                           tabsAdapter.updateTab(tab.getPosition(), R.drawable.ic_message_blue, newMessagesNum, false);
                        }
                     else if (tab.getPosition() == 3)
                        {
                           actionBar.setTitle("Friends");
                           tabsAdapter.updateTab(tab.getPosition(), R.drawable.ic_friends_blue, newFriendNum, false);
                        }
                     else
                        {
                           actionBar.setTitle("Settings");
                           tabsAdapter.updateTab(tab.getPosition(), R.drawable.ic_settings_blue, 0, false);
                        }
                  }

               @Override
               public void onTabUnselected(TabLayout.Tab tab)
                  {

                     if (tab.getPosition() == 0)
                        {
                           actionBar.setTitle("Wall");
                           tabsAdapter.updateTab(tab.getPosition(), R.drawable.ic_wall_gray, newWallPostsNum, true);
                        }
                     else if (tab.getPosition() == 1)
                        {
                           actionBar.setTitle("Notifications");
                           tabsAdapter.updateTab(tab.getPosition(), R.drawable.ic_notification_gray, newNotificationNum, true);
                        }
                     else if (tab.getPosition() == 2)
                        {
                           actionBar.setTitle("Messages");
                           tabsAdapter.updateTab(tab.getPosition(), R.drawable.ic_message_gray, newMessagesNum, true);
                        }
                     else if (tab.getPosition() == 3)
                        {
                           actionBar.setTitle("Friends");
                           tabsAdapter.updateTab(tab.getPosition(), R.drawable.ic_friends_gray, newFriendNum, true);
                        }
                     else
                        {
                           actionBar.setTitle("Settings");
                           tabsAdapter.updateTab(tab.getPosition(), R.drawable.ic_settings_gray, 0, false);
                        }
                  }

               @Override
               public void onTabReselected(TabLayout.Tab tab)
                  {
                  }
            });

            // get the application
            app = (POSNApplication) this.getApplication();

            // clear friends list when activity starts
            app.friendList.clear();
            app.friendRequestsList.clear();
         }


      @Override
      protected void onResume()
         {
            super.onResume();
            if (app.getDropbox() != null)
               app.getDropbox().authenticateDropboxLogin();

         }

      public void onActivityResult(int requestCode, int resultCode, Intent data)
         {
            super.onActivityResult(requestCode, resultCode, data);
         }


      @Override
      public boolean onCreateOptionsMenu(Menu menu)
         {
            return true;
         }

   }
