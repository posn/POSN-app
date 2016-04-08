package com.posn.main;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.posn.R;
import com.posn.application.POSNApplication;
import com.posn.asynctasks.AsyncResponseIntialize;
import com.posn.asynctasks.InitializeAsyncTask;
import com.posn.clouds.Dropbox.DropboxClientUsage;
import com.posn.datatypes.ConversationList;
import com.posn.datatypes.FriendList;
import com.posn.datatypes.GroupList;
import com.posn.datatypes.NotificationList;
import com.posn.datatypes.WallPostList;
import com.posn.main.friends.UserFriendsFragment;
import com.posn.main.messages.UserMessagesFragment;
import com.posn.main.notifications.UserNotificationsFragment;
import com.posn.main.wall.UserWallFragment;
import com.posn.utility.IDGenerator;


public class MainActivity extends BaseActivity implements AsyncResponseIntialize
   {
      private ViewPager viewPager;
      private ActionBar actionBar;
      private MainTabsPagerAdapter tabsAdapter;
      public POSNApplication app;

      public int newWallPostsNum = 0;
      public int newNotificationNum = 0;
      public int newMessagesNum = 0;
      public int newFriendNum = 0;

      InitializeAsyncTask asyncTaskInitialize;

      // data for wall fragment
      public WallPostList wallPostList = new WallPostList();

      // data for master friends list
      public FriendList masterFriendList = new FriendList();

      // data for notification fragment
      public NotificationList notificationList = new NotificationList();

      // data for message fragment
      public ConversationList conversationList = new ConversationList();

      // data for groups
      public GroupList groupList = new GroupList();


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

            // create a new pager adapter and assign it to the view pager
            tabsAdapter = new MainTabsPagerAdapter(this.getSupportFragmentManager(), this);
            viewPager.setAdapter(tabsAdapter);

            final TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);

            System.out.println("MAIN OnCreate!!!");

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

            // sign into the cloud
            cloud = new DropboxClientUsage(this);
           // cloud = new GoogleDriveClientUsage(this);
            //cloud = new OneDriveClientUsage(this);
            cloud.initializeCloud();

            asyncTaskInitialize = new InitializeAsyncTask(this);
            asyncTaskInitialize.delegate = this;
            asyncTaskInitialize.execute();
         }



      @Override
      public boolean onCreateOptionsMenu(Menu menu)
         {
            return true;
         }


      public void initializingFileDataFinished()
         {
            UserFriendsFragment friendFrag = (UserFriendsFragment) tabsAdapter.getRegisteredFragment(3);
            if (friendFrag != null)
               {
                  friendFrag.updateFriendList();
               }

            UserWallFragment wallFrag = (UserWallFragment) tabsAdapter.getRegisteredFragment(0);
            if (wallFrag != null)
               {
                  wallFrag.updateWallPosts();
               }

            UserMessagesFragment messagesFrag = (UserMessagesFragment) tabsAdapter.getRegisteredFragment(2);
            if (messagesFrag != null)
               {
                  messagesFrag.updateMessages();
               }

            UserNotificationsFragment notificationFrag = (UserNotificationsFragment) tabsAdapter.getRegisteredFragment(1);
            if (notificationFrag != null)
               {
                  notificationFrag.updateNotifications();
               }
         }

      public void createNewGroup(String groupName)
         {
            // generate group ID based on the group name



         }
   }

