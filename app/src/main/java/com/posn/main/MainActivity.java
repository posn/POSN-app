package com.posn.main;

import android.app.ActionBar;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.posn.R;
import com.posn.application.POSNApplication;
import com.posn.asynctasks.AsyncResponseIntialize;
import com.posn.asynctasks.GetFriendContentAsyncTask;
import com.posn.asynctasks.InitializeAsyncTask;
import com.posn.clouds.Dropbox.DropboxClientUsage;
import com.posn.exceptions.POSNCryptoException;
import com.posn.main.friends.UserFriendsFragment;
import com.posn.main.messages.UserConversationFragment;
import com.posn.main.notifications.UserNotificationsFragment;
import com.posn.main.wall.UserWallFragment;
import com.posn.utility.POSNDataManager;

import java.io.UnsupportedEncodingException;


public class MainActivity extends BaseActivity implements AsyncResponseIntialize
   {
      // user interface variables
      private ViewPager viewPager;
      private ActionBar actionBar;
      private MainTabsPagerAdapter tabsAdapter;

      // count variables used to show the number of new posts/notifications/messages/friend requests
      public int newWallPostsNum = 0;
      public int newNotificationNum = 0;
      public int newMessagesNum = 0;
      public int newFriendNum = 0;

      // data manager object that holds all the application data and methods to create different files
      public POSNDataManager dataManager;

      boolean initialize = true;
      public boolean newFriendRequest = false;

      @Override
      public void onSaveInstanceState(Bundle savedInstanceState)
         {
            // save the current notification numbers
            savedInstanceState.putInt("newWallPostsNum", newWallPostsNum);
            savedInstanceState.putInt("newNotificationNum", newNotificationNum);
            savedInstanceState.putInt("newMessagesNum", newMessagesNum);
            savedInstanceState.putInt("newFriendNum", newFriendNum);

            savedInstanceState.putParcelable("dataManager", dataManager);

            // Always call the superclass so it can save the view hierarchy state
            super.onSaveInstanceState(savedInstanceState);
         }


      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            // load the xml file for the logs
            setContentView(R.layout.activity_main);

            // create a new data manager object
            dataManager = (POSNDataManager) getIntent().getExtras().get("dataManager");

            // attempt to get any new friend requests
            if (getIntent().hasExtra("uri"))
               {
                  try
                     {
                        newFriendRequest = dataManager.parseFriendRequestURI(Uri.parse(getIntent().getExtras().getString("uri")));
                     }
                  catch (UnsupportedEncodingException | POSNCryptoException e)
                     {
                        e.printStackTrace();
                     }
               }

            // find the viewpager in the xml file
            viewPager = (ViewPager) findViewById(R.id.system_viewpager);
            viewPager.setOffscreenPageLimit(4);

            // create a new pager adapter and assign it to the view pager
            tabsAdapter = new MainTabsPagerAdapter(this.getSupportFragmentManager(), this);
            viewPager.setAdapter(tabsAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);

            // set the tabs with the correct icon and number of notifications bubble
            tabLayout.getTabAt(0).setCustomView(tabsAdapter.getTabView(R.drawable.ic_wall_blue, newWallPostsNum, false));
            tabLayout.getTabAt(1).setCustomView(tabsAdapter.getTabView(R.drawable.ic_notification_gray, newNotificationNum, true));
            tabLayout.getTabAt(2).setCustomView(tabsAdapter.getTabView(R.drawable.ic_message_gray, newMessagesNum, true));
            tabLayout.getTabAt(3).setCustomView(tabsAdapter.getTabView(R.drawable.ic_friends_gray, newFriendNum, true));
            tabLayout.getTabAt(4).setCustomView(tabsAdapter.getTabView(R.drawable.ic_settings_gray, 0, false));

            // get the action bar to set the title
            actionBar = getActionBar();
            actionBar.setTitle("Wall");

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
               {
                  @Override
                  public void onTabSelected(TabLayout.Tab tab)
                     {
                        viewPager.setCurrentItem(tab.getPosition(), true);
                        updateTab(tab.getPosition(), true);
                     }

                  @Override
                  public void onTabUnselected(TabLayout.Tab tab)
                     {
                        updateTab(tab.getPosition(), false);
                     }

                  @Override
                  public void onTabReselected(TabLayout.Tab tab)
                     {
                     }
               });

            // get the application
            app = (POSNApplication) getApplication();

            // initialize the cloud provider
            if (app.cloud == null)
               {
                  app.cloud = new DropboxClientUsage(this);
                  // cloud = new GoogleDriveClientUsage(this);
                  //cloud = new OneDriveClientUsage(this);
                  app.cloud.initializeCloud();
               }

            // check if the activity was saved previous and fetch the previous data
            if (savedInstanceState != null)
               {
                  newWallPostsNum = savedInstanceState.getInt("newWallPostsNum");
                  newNotificationNum = savedInstanceState.getInt("newNotificationNum");
                  newMessagesNum = savedInstanceState.getInt("newMessagesNum");
                  newFriendNum = savedInstanceState.getInt("newFriendNum");

                  dataManager = savedInstanceState.getParcelable("dataManager");
               }
            // otherwise fetch the data from the application files
            else
               {
                  InitializeAsyncTask asyncTaskInitialize = new InitializeAsyncTask(this);
                  asyncTaskInitialize.delegate = this;
                  asyncTaskInitialize.execute();
               }
         }


      @Override
      public void onResume()
         {
            super.onResume();

            // sign into the cloud
            if (app.cloud == null)
               {
                  app.cloud = new DropboxClientUsage(this);
                  // cloud = new GoogleDriveClientUsage(this);
                  //cloud = new OneDriveClientUsage(this);
                  app.cloud.initializeCloud();
               }

         }

      @Override
      public boolean onCreateOptionsMenu(Menu menu)
         {
            return true;
         }


      public void finishedInitializingApplicationData()
         {
            // get the friend fragment and update the friend list with app data
            UserFriendsFragment friendFrag = (UserFriendsFragment) tabsAdapter.getRegisteredFragment(3);
            if (friendFrag != null)
               {
                  // process the new friend request
                  friendFrag.updateFriendList();
                  tabsAdapter.updateTab(3, R.drawable.ic_friends_gray, newFriendNum, true);
               }

            // get the wall fragment and update the wall post list with app data
            UserWallFragment wallFrag = (UserWallFragment) tabsAdapter.getRegisteredFragment(0);
            if (wallFrag != null)
               {
                  wallFrag.updateWallPosts();
               }

            // get the message fragment and update the conversation list with app data
            UserConversationFragment messagesFrag = (UserConversationFragment) tabsAdapter.getRegisteredFragment(2);
            if (messagesFrag != null)
               {
                  messagesFrag.updateConversations();
               }

            // get the notification fragment and update the notification list with app data
            UserNotificationsFragment notificationFrag = (UserNotificationsFragment) tabsAdapter.getRegisteredFragment(1);
            if (notificationFrag != null)
               {
                  notificationFrag.updateNotifications();
               }

            if (initialize)
               {
                  new GetFriendContentAsyncTask(this).execute();
                  initialize = false;
               }
         }


      public void updateTab(int position, boolean selected)
         {
            if (position == 0)
               {
                  actionBar.setTitle("Wall");
                  tabsAdapter.updateTab(position, (selected ? R.drawable.ic_wall_blue : R.drawable.ic_wall_gray), newWallPostsNum, true);
               }
            else if (position == 1)
               {
                  actionBar.setTitle("Notifications");
                  tabsAdapter.updateTab(position, (selected ? R.drawable.ic_notification_blue : R.drawable.ic_notification_gray), newNotificationNum, true);
               }
            else if (position == 2)
               {
                  actionBar.setTitle("Messages");
                  tabsAdapter.updateTab(position, (selected ? R.drawable.ic_message_blue : R.drawable.ic_message_gray), newMessagesNum, true);
               }
            else if (position == 3)
               {
                  actionBar.setTitle("Friends");
                  tabsAdapter.updateTab(position, (selected ? R.drawable.ic_friends_blue : R.drawable.ic_friends_gray), newFriendNum, true);
               }
            else
               {
                  actionBar.setTitle("Settings");
                  tabsAdapter.updateTab(position, (selected ? R.drawable.ic_settings_blue : R.drawable.ic_settings_gray), 0, false);
               }
         }


   }

