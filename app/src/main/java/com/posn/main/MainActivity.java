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
import com.posn.asynctasks.friends.AsyncResponseFriends;
import com.posn.asynctasks.friends.LoadFriendsListAsyncTask;
import com.posn.asynctasks.friends.SaveFriendsListAsyncTask;
import com.posn.asynctasks.notifications.AsyncResponseNotifications;
import com.posn.asynctasks.notifications.LoadNotificationsAsyncTask;
import com.posn.asynctasks.notifications.SaveNotificationsAsyncTask;
import com.posn.asynctasks.wall.AsyncResponseWall;
import com.posn.asynctasks.wall.LoadWallPostsAsyncTask;
import com.posn.asynctasks.wall.SaveWallPostsAsyncTask;
import com.posn.datatypes.Friend;
import com.posn.datatypes.Notification;
import com.posn.datatypes.Post;
import com.posn.main.friends.UserFriendsFragment;
import com.posn.main.notifications.UserNotificationsFragment;
import com.posn.main.wall.UserWallFragment;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends FragmentActivity implements AsyncResponseFriends, AsyncResponseWall, AsyncResponseNotifications
   {
      private ViewPager viewPager;
      private ActionBar actionBar;
      private MainTabsPagerAdapter tabsAdapter;
      public POSNApplication app;

      private boolean firstStart = true;

      public int newWallPostsNum = 0;
      public int newNotificationNum = 0;
      public int newMessagesNum = 0;
      public int newFriendNum = 0;

      // data for wall fragment
      public ArrayList<Post> wallPostData = new ArrayList<>();
      LoadWallPostsAsyncTask asyncTaskWall;

      // data for master friends list
      public HashMap<String, Friend> masterFriendList = new HashMap<>();
      public ArrayList<Friend> masterRequestsList = new ArrayList<>();
      LoadFriendsListAsyncTask asyncTaskFriend;

      // data for notification fragment
      public ArrayList<Notification> notificationData = new ArrayList<>();
      LoadNotificationsAsyncTask asyncTaskNotification;

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
            viewPager.setOffscreenPageLimit(4);

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
            //loadFriendsList();
         }


      @Override
      protected void onResume()
         {
            super.onResume();
            if (app.getDropbox() != null)
               {
                  app.getDropbox().authenticateDropboxLogin();
               }
            System.out.println("ON RESUME Friend: " + masterFriendList.size() + " Wall: " + wallPostData.size());

            if (masterFriendList.isEmpty())
               {
                  loadFriendsList();
                  firstStart = true;
               }


            // might need else to call fragment update functions

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

      public void saveFriendsList()
         {
            new SaveFriendsListAsyncTask(this, app.wallFilePath + "/user_friends.txt", masterFriendList, masterRequestsList).execute();
         }

      public void loadFriendsList()
         {
            asyncTaskFriend = new LoadFriendsListAsyncTask(this, app.wallFilePath + "/user_friends.txt");
            asyncTaskFriend.delegate = this;
            asyncTaskFriend.execute();
         }

      public void loadingFriendsFinished(HashMap<String, Friend> friendList, ArrayList<Friend> friendRequestList)
         {
            this.masterFriendList.putAll(friendList);
            this.masterRequestsList.addAll(friendRequestList);


            UserFriendsFragment fragment = (UserFriendsFragment) tabsAdapter.getRegisteredFragment(3);
            if (fragment != null)
               {
                  fragment.updateFriendList();
               }

            if (firstStart)
               {
                  loadWallPosts();
               }
         }


      public void saveWallPosts()
         {
            SaveWallPostsAsyncTask task = new SaveWallPostsAsyncTask(this, app.wallFilePath + "/user_wall.txt", wallPostData);
            task.execute();
         }

      public void loadWallPosts()
         {
            asyncTaskWall = new LoadWallPostsAsyncTask(this, app.wallFilePath + "/user_wall.txt");
            asyncTaskWall.delegate = this;
            asyncTaskWall.execute();
         }

      public void loadingWallFinished(ArrayList<Post> wallData)
         {
            // add the loaded data to the array list and hashmap
            this.wallPostData.addAll(wallData);

            UserWallFragment fragment = (UserWallFragment) tabsAdapter.getRegisteredFragment(0);
            if (fragment != null)
               {
                  fragment.updateWallPosts();
               }

            if (firstStart)
               {
                  loadNotifications();
               }
         }

      public void saveNotifications()
         {
            SaveNotificationsAsyncTask task = new SaveNotificationsAsyncTask(this, app.wallFilePath + "/user_notifications.txt", notificationData);
            task.execute();
         }

      public void loadNotifications()
         {
            asyncTaskNotification = new LoadNotificationsAsyncTask(this, app.wallFilePath + "/user_notifications.txt");
            asyncTaskNotification.delegate = this;
            asyncTaskNotification.execute();
         }


      public void loadingNotificationsFinished(ArrayList<Notification> notificationList)
         {
            // add the loaded data to the array list and hashmap
            this.notificationData.addAll(notificationList);

            UserNotificationsFragment fragment = (UserNotificationsFragment) tabsAdapter.getRegisteredFragment(1);
            if (fragment != null)
               {
                  fragment.updateNotifications();
               }

            firstStart = false;

         }

   }
