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


public class MainActivity extends FragmentActivity
   {
      private ViewPager viewPager;
      private ActionBar actionBar;
      public POSNApplication app;

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


            viewPager.setAdapter(new MainTabsPagerAdapter(this.getSupportFragmentManager(), this));

            TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);

            // set initial values
            tabLayout.getTabAt(0).setIcon(R.drawable.ic_wall_blue);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_notification_gray);
            tabLayout.getTabAt(2).setIcon(R.drawable.ic_message_gray);
            tabLayout.getTabAt(3).setIcon(R.drawable.ic_friends_gray);
            tabLayout.getTabAt(4).setIcon(R.drawable.ic_settings_gray);
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
                           tab.setIcon(R.drawable.ic_wall_blue);

                        }
                     else
                        if (tab.getPosition() == 1)
                           {
                              actionBar.setTitle("Notifications");
                              tab.setIcon(R.drawable.ic_notification_blue);

                           }
                        else
                           if (tab.getPosition() == 2)
                              {
                                 actionBar.setTitle("Messages");
                                 tab.setIcon(R.drawable.ic_message_blue);

                              }
                           else
                              if (tab.getPosition() == 3)
                                 {
                                    actionBar.setTitle("Friends");
                                    tab.setIcon(R.drawable.ic_friends_blue);

                                 }
                              else
                                 {
                                    actionBar.setTitle("Settings");
                                    tab.setIcon(R.drawable.ic_settings_blue);

                                 }
                  }

               @Override
               public void onTabUnselected(TabLayout.Tab tab)
                  {

                     if (tab.getPosition() == 0)
                        {
                           actionBar.setTitle("Wall");
                           tab.setIcon(R.drawable.ic_wall_gray);

                        }
                     else
                        if (tab.getPosition() == 1)
                           {
                              actionBar.setTitle("Notifications");
                              tab.setIcon(R.drawable.ic_notification_gray);

                           }
                        else
                           if (tab.getPosition() == 2)
                              {
                                 actionBar.setTitle("Messages");
                                 tab.setIcon(R.drawable.ic_message_gray);

                              }
                           else
                              if (tab.getPosition() == 3)
                                 {
                                    actionBar.setTitle("Friends");
                                    tab.setIcon(R.drawable.ic_friends_gray);

                                 }
                              else
                                 {
                                    actionBar.setTitle("Settings");
                                    tab.setIcon(R.drawable.ic_settings_gray);

                                 }
                  }

               @Override
               public void onTabReselected(TabLayout.Tab tab)
                  {
                  }
            });

            // get the application
            app = (POSNApplication) this.getApplication();

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
            // Inflate the menu; this adds items to the action bar if it is present.
            // getMenuInflater().inflate(R.menu.display_system, menu);
            return true;
         }

   }
