package com.posn.main;

import android.app.ActionBar;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.posn.Constants;
import com.posn.R;
import com.posn.application.POSNApplication;
import com.posn.asynctasks.InitializeApplicationDataAsyncTask;
import com.posn.clouds.DropboxClientUsage;
import com.posn.clouds.GoogleDriveClientUsage;
import com.posn.clouds.OnConnectedCloudListener;
import com.posn.clouds.OneDriveClientUsage;
import com.posn.exceptions.POSNCryptoException;
import com.posn.main.friends.UserFriendsFragment;
import com.posn.main.messages.UserConversationFragment;
import com.posn.main.notifications.UserNotificationsFragment;
import com.posn.main.wall.UserWallFragment;

import java.io.UnsupportedEncodingException;

/**
 * This activity class implements the main social network functionality after the user has been authenticated:
 * <ul><li>Creates and maintains the wall, notification, message, and friend fragments through the MainTabsPagerAdapter
 * <li>Manages all the application data for the fragments through a AppDataManager object
 * <li>Connects to the user's chosen cloud provider</ul>
 **/
public class MainActivity extends BaseActivity implements OnConnectedCloudListener
   {
      // user interface variables
      private ViewPager viewPager;
      private ActionBar actionBar;
      private MainTabsPagerAdapter tabsAdapter;

      // data manager object that holds all the app data and methods to create different files
      public AppDataManager dataManager;

      // bool value used to determine if the async task finished loading the data from the app files
      public boolean isInitialized = false;

      /**
       * This method is called when the activity is stopped and saves the current data values
       **/
      @Override
      public void onSaveInstanceState(Bundle savedInstanceState)
         {
            savedInstanceState.putParcelable("dataManager", dataManager);
            savedInstanceState.putBoolean("isInitialized", isInitialized);

            // Always call the superclass so it can save the view hierarchy state
            super.onSaveInstanceState(savedInstanceState);
         }


      /**
       * This method is called when the activity needs to be created and handles data being passed in from the login activity (uri, dataManager).
       * Sets up the tab pager adapter for all the fragments
       * Connects the app to the cloud provider
       **/
      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            // load the xml file for the logs
            setContentView(R.layout.activity_main);

            // create a new data manager object
            dataManager = (AppDataManager) getIntent().getExtras().get("dataManager");

            // attempt to get any new friend requests
            if (getIntent().hasExtra("uri"))
               {
                  try
                     {
                        dataManager.parseFriendRequestURI(Uri.parse(getIntent().getExtras().getString("uri")));
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
            tabLayout.getTabAt(0).setCustomView(tabsAdapter.createTabView(R.drawable.ic_wall_blue, true));
            tabLayout.getTabAt(1).setCustomView(tabsAdapter.createTabView(R.drawable.ic_notification_gray, true));
            tabLayout.getTabAt(2).setCustomView(tabsAdapter.createTabView(R.drawable.ic_message_gray, true));
            tabLayout.getTabAt(3).setCustomView(tabsAdapter.createTabView(R.drawable.ic_friends_gray, true));
            tabLayout.getTabAt(4).setCustomView(tabsAdapter.createTabView(R.drawable.ic_settings_gray, false));

            // get the action bar to set the title
            actionBar = getActionBar();
            actionBar.setTitle("Wall");

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
               {
                  @Override
                  public void onTabSelected(TabLayout.Tab tab)
                     {
                        String tabNames[] = {"Wall", "Notifications", "Messages", "Friends", "Settings"};
                        int selectedIcons[] = {R.drawable.ic_wall_blue, R.drawable.ic_notification_blue, R.drawable.ic_message_blue, R.drawable.ic_friends_blue, R.drawable.ic_settings_blue};

                        int index = tab.getPosition();
                        viewPager.setCurrentItem(index, true);
                        tabsAdapter.updateTabIcon(index, selectedIcons[index]);
                        actionBar.setTitle(tabNames[index]);
                     }

                  @Override
                  public void onTabUnselected(TabLayout.Tab tab)
                     {
                        int index = tab.getPosition();
                        int unselectedIcons[] = {R.drawable.ic_wall_gray, R.drawable.ic_notification_gray, R.drawable.ic_message_gray, R.drawable.ic_friends_gray, R.drawable.ic_settings_gray};

                        tabsAdapter.updateTabIcon(index, unselectedIcons[index]);
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
                  initializeCloudProvider();
               }

            // check if the activity was saved previously and fetch the previous data
            if (savedInstanceState != null)
               {
                  dataManager = savedInstanceState.getParcelable("dataManager");
                  isInitialized = savedInstanceState.getBoolean("isInitialized");
               }

         }

      /**
       * This method is called when the activity is reopened and reconnects to the cloud provider if needed
       **/
      @Override
      public void onResume()
         {
            super.onResume();

            // sign into the cloud if the cloud provider is null
            // required to be here to reconnect to the cloud if the application is destroyed by the Android OS
            if (app.cloud == null)
               {
                  initializeCloudProvider();
               }
         }

      @Override
      public void OnConnected()
         {
            if (!isInitialized)
               {
                  new InitializeApplicationDataAsyncTask(this).execute();
               }
         }

      /**
       * This method connects to the cloud provider and initializes it
       **/
      public void initializeCloudProvider()
         {
            if (dataManager.user.cloudProvider == Constants.PROVIDER_DROPBOX)
               {
                  app.cloud = new DropboxClientUsage(this, this);
               }
            else if (dataManager.user.cloudProvider == Constants.PROVIDER_GOOGLEDRIVE)
               {
                  app.cloud = new GoogleDriveClientUsage(this, this);
               }
            else
               {
                  app.cloud = new OneDriveClientUsage(this, this);
               }

            app.cloud.initializeCloud();
         }

      @Override
      public boolean onCreateOptionsMenu(Menu menu)
         {
            return true;
         }

      /**
       * This method is called when new data has been fetched and updates all the fragments with the new data
       **/
      public void notifyFragmentsOnNewDataChange()
         {
            // get the friend fragment and update the friend list with app data
            UserFriendsFragment friendFrag = (UserFriendsFragment) tabsAdapter.getRegisteredFragment(3);
            if (friendFrag != null)
               {
                  friendFrag.updateFriendList();
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

            isInitialized = true;
         }

      /**
       * This method changes the action bar title to the current tab name and changes the icon to be selected or not. Also updates the number of new items for that tab.
       **/
      public void updateTabNotificationNum(int position, int numNotifications)
         {
            tabsAdapter.updateNotificationNum(position, numNotifications);
         }
   }

