package com.posn.main.main;

import android.app.ActionBar;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.posn.R;
import com.posn.application.POSNApplication;
import com.posn.clouds.DropboxProvider;
import com.posn.clouds.GoogleDriveProvider;
import com.posn.clouds.OnConnectedCloudListener;
import com.posn.clouds.OneDriveProvider;
import com.posn.constants.Constants;
import com.posn.exceptions.POSNCryptoException;
import com.posn.main.BaseActivity;
import com.posn.managers.AppDataManager;

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
      public MainTabsPagerAdapter tabsAdapter;

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
       * <ul><li>Sets up the tab pager adapter for all the fragments
       * <li>Connects the app to the cloud provider</ul>
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
            tabLayout.getTabAt(4).setCustomView(tabsAdapter.createTabView(R.drawable.ic_groups_gray, false));

            // get the action bar to set the title
            actionBar = getActionBar();
            actionBar.setTitle("Wall");

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
               {
                  @Override
                  public void onTabSelected(TabLayout.Tab tab)
                     {
                        String tabNames[] = {"Wall", "Notifications", "Messages", "Friends", "Groups"};
                        int selectedIcons[] = {R.drawable.ic_wall_blue, R.drawable.ic_notification_blue, R.drawable.ic_message_blue, R.drawable.ic_friends_blue, R.drawable.ic_groups_blue};

                        int index = tab.getPosition();
                        viewPager.setCurrentItem(index, true);
                        tabsAdapter.updateTabIcon(index, selectedIcons[index]);
                        actionBar.setTitle(tabNames[index]);
                     }

                  @Override
                  public void onTabUnselected(TabLayout.Tab tab)
                     {
                        int index = tab.getPosition();
                        int unselectedIcons[] = {R.drawable.ic_wall_gray, R.drawable.ic_notification_gray, R.drawable.ic_message_gray, R.drawable.ic_friends_gray, R.drawable.ic_groups_gray};

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
                  System.out.println("INTIALIZE CLOUD!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                  initializeCloudProvider();
               }
            else
               {
                  // check if the data has been initialized
                  if (!isInitialized)
                     {
                        System.out.println("CLOUD - LOAD!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                        new InitializeApplicationDataAsyncTask(this).execute();
                     }
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

      /**
       * This method is called when the cloud provider has been connected with the application
       **/
      @Override
      public void OnConnected()
         {
            if (!isInitialized)
               {
                  System.out.println("INTIALIZE - ON CONNECT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                  new InitializeApplicationDataAsyncTask(this).execute();
               }
         }

      /**
       * This method connects to the cloud provider and initializes it
       **/
      public void initializeCloudProvider()
         {
            if (dataManager.userManager.cloudProvider == Constants.PROVIDER_DROPBOX)
               {
                  app.cloud = new DropboxProvider(this, this);
               }
            else if (dataManager.userManager.cloudProvider == Constants.PROVIDER_GOOGLEDRIVE)
               {
                  app.cloud = new GoogleDriveProvider(this, this);
               }
            else
               {
                  app.cloud = new OneDriveProvider(this, this);
               }

            app.cloud.initializeCloud();
         }


      @Override
      public boolean onCreateOptionsMenu(Menu menu)
         {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main_activity_actions, menu);
            return true;


         }

      /**
       * This method is called when new data has been fetched and updates all the fragments with the new data
       **/
      public void notifyFragmentsOnNewDataChange()
         {
            // update the friend list with app data
            tabsAdapter.notifyFriendsFragmentOnNewDataChange();

            // update the wall post list with app data
            tabsAdapter.notifyWallFragmentOnNewDataChange();

            // update the conversation list with app data
            tabsAdapter.notifyConversationsFragmentOnNewDataChange();

            // update the notification list with app data
            tabsAdapter.notifyNofiticationsFragmentOnNewDataChange();

            // update the group list with the app data
            tabsAdapter.notifyUserGroupFragmentOnNewDataChange();

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

