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
import com.posn.asynctasks.AsyncResponseIntialize;
import com.posn.asynctasks.InitializeAsyncTask;
import com.posn.clouds.Dropbox.DropboxClientUsage;
import com.posn.datatypes.ConversationList;
import com.posn.datatypes.FriendList;
import com.posn.datatypes.UserGroupList;
import com.posn.datatypes.NotificationList;
import com.posn.datatypes.RequestedFriend;
import com.posn.datatypes.User;
import com.posn.datatypes.WallPostList;
import com.posn.encryption.AsymmetricKeyManager;
import com.posn.encryption.SymmetricKeyManager;
import com.posn.main.friends.UserFriendsFragment;
import com.posn.main.messages.UserMessagesFragment;
import com.posn.main.notifications.UserNotificationsFragment;
import com.posn.main.wall.UserWallFragment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;


public class MainActivity extends BaseActivity implements AsyncResponseIntialize
   {
      private ViewPager viewPager;
      private ActionBar actionBar;
      private MainTabsPagerAdapter tabsAdapter;

      public int newWallPostsNum = 0;
      public int newNotificationNum = 0;
      public int newMessagesNum = 0;
      public int newFriendNum = 0;

      InitializeAsyncTask asyncTaskInitialize;

      public User user = null;

      // data for wall fragment
      public WallPostList masterWallPostList = new WallPostList();

      // data for master friends list
      public FriendList masterFriendList = new FriendList();

      // data for notification fragment
      public NotificationList notificationList = new NotificationList();

      // data for message fragment
      public ConversationList conversationList = new ConversationList();

      // data for groups
      public UserGroupList userGroupList = new UserGroupList();

      public RequestedFriend requestedFriend = null;


      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            // load the xml file for the logs
            setContentView(R.layout.activity_main);

            // get the application
            app = (POSNApplication) this.getApplication();

            // attempt to get any new friend requests
            if (getIntent().hasExtra("uri"))
               {
                  processURI(Uri.parse(getIntent().getExtras().getString("uri")));

                  //requestedFriend = (RequestedFriend) getIntent().getExtras().get("newFriend");
               }

            // get the user from the login activity
            user = (User) getIntent().getExtras().get("user");

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


            // sign into the cloud
            app.cloud = new DropboxClientUsage(this);
            // cloud = new GoogleDriveClientUsage(this);
            //cloud = new OneDriveClientUsage(this);
            app.cloud.initializeCloud();

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
                  tabsAdapter.updateTab(3, R.drawable.ic_friends_gray, newFriendNum, true);
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

      private void processURI(Uri uriData)
         {
            if (uriData != null)
               {
                  requestedFriend = new RequestedFriend();

                  // get the path segments of the URI
                  List<String> params = uriData.getPathSegments();

                  // check the type of URI
                  String uriType = params.get(0);

                  System.out.println("TYPE: " + uriType);

                  if (uriType.equals("request"))
                     {
                        try
                           {
                              // set friend status
                              requestedFriend.status = Constants.STATUS_REQUEST;

                              // get ID
                              requestedFriend.ID = params.get(1);

                              // get email
                              requestedFriend.email = params.get(2);

                              // get first and last name
                              requestedFriend.name = params.get(3) + " " + params.get(4);

                              // get public key
                              requestedFriend.publicKey = URLDecoder.decode(params.get(5), "UTF-8");
                              requestedFriend.publicKey = requestedFriend.publicKey.replace("%2B", "+");

                              // get file link
                              requestedFriend.fileLink = URLDecoder.decode(params.get(6), "UTF-8");

                              // get nonce
                              requestedFriend.nonce = params.get(7);
                           }
                        catch (UnsupportedEncodingException e)
                           {
                              e.printStackTrace();
                           }
                     }
                  else if (uriType.equals("accept"))
                     {
                        // get encrypted key
                        String encryptedSymmetricKey = params.get(1);

                        // decrypt symmetric key
                        String key = AsymmetricKeyManager.decrypt(user.privateKey, encryptedSymmetricKey);

                        // decrypt URI data
                        String encryptedURI = params.get(2);

                        String URI = SymmetricKeyManager.decrypt(key, encryptedURI);
                        String[] paths = URI.split("/");

                        try
                           {
                              // set friend status
                              requestedFriend.status = Constants.STATUS_ACCEPTED;

                              // get ID
                              requestedFriend.ID = paths[0];

                              // get first and last name
                              requestedFriend.name = paths[1] + " " + paths[2];

                              // get public key
                              requestedFriend.publicKey = URLDecoder.decode(paths[3], "UTF-8");
                              requestedFriend.publicKey = requestedFriend.publicKey.replace("%2B", "+");

                              // get file link
                              requestedFriend.fileLink = URLDecoder.decode(paths[4], "UTF-8");

                              // get nonces
                              requestedFriend.nonce = paths[5];
                              requestedFriend.nonce2 = paths[6];
                           }
                        catch (UnsupportedEncodingException e)
                           {
                              e.printStackTrace();
                           }


                     }
               }
         }
   }

