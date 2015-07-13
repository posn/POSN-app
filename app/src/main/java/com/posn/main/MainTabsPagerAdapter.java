package com.posn.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.posn.R;
import com.posn.main.friends.UserFriendsFragment;
import com.posn.main.messages.UserMessagesFragment;
import com.posn.main.notifications.UserNotificationsFragment;
import com.posn.main.settings.UserSettingsFragment;
import com.posn.main.wall.UserWallFragment;


public class MainTabsPagerAdapter extends FragmentPagerAdapter
	{

		// declare variables
		Fragment wallTab, notificationTab, messagesTab, friendsTabs, settingsTab;
        Context context;
		private String tabTitles[] = new String[] { "Tab1", "Tab2", "Tab3", "Tab3", "Tab3" };

        private int[] imageResId = {
                R.drawable.selector_wall_icon,
                R.drawable.selector_notification_icon,
                R.drawable.selector_messages_icon,
                R.drawable.selector_friends_icon,
                R.drawable.selector_settings_icon
        };


		public MainTabsPagerAdapter(FragmentManager fm, Context context)
			{
				super(fm);
                this.context = context;
			}


		@Override
		public Fragment getItem(int index)
			{

				// load the tab that was selected.
				switch(index)
				{
					// wall tab fragment
					case 0:
						if (wallTab == null)
							{
								wallTab = new UserWallFragment();
							}
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
							messagesTab = new UserMessagesFragment();
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
		public int getCount()
			{
				// get item count - equal to number of tabs
				return 5;
			}



	}
