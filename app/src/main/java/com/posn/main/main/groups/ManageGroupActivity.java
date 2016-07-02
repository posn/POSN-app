package com.posn.main.main.groups;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.posn.R;
import com.posn.datatypes.Friend;
import com.posn.datatypes.UserGroup;
import com.posn.main.main.groups.listview_items.GroupFriendItem;
import com.posn.main.main.groups.listview_items.GroupHeaderItem;
import com.posn.main.main.groups.listview_items.GroupNoFriendItem;
import com.posn.main.main.groups.listview_items.ListViewManageGroupItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * This activity class implements the functionality to manage a single user group. Can add or remove friends from the group
 **/
public class ManageGroupActivity extends FragmentActivity implements OnClickListener
   {
      // user interface variables
      private ManageGroupArrayAdapter adapter;
      public ArrayList<ListViewManageGroupItem> listViewItems = new ArrayList<>();

      private UserGroup group;
      private HashMap<String, Friend> friendsList;

      private boolean hasChanged = false;

      /**
       * This method is called when the activity needs to be created and sets up the user interface.
       **/
      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_manage_groups);

            // get the data passed in from the user groups fragment
            group = getIntent().getParcelableExtra("group");
            friendsList = (HashMap<String, Friend>) getIntent().getSerializableExtra("friendsList");

            // get the listview from the layout
            ListView lv = (ListView) findViewById(R.id.listView1);

            // get the buttons from the layout
            Button updateGroup = (Button) findViewById(R.id.update_group_button);

            // set onclick listener for each button
            updateGroup.setOnClickListener(this);


            // create a custom adapter for each contact item in the listview
            adapter = new ManageGroupArrayAdapter(this, listViewItems);

            // set up the listview
            lv.setAdapter(adapter);

            // get the action bar and set the title
            ActionBar actionBar = getActionBar();
            if (actionBar != null)
               {
                  actionBar.setTitle("Manage the " + group.name + " Group");
               }

            updateGroupFriendList();
         }


      /**
       * This method is called when the user clicks the different user interface elements and implements each element's functionality
       **/
      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.update_group_button:

                     // check if the group membership has changed
                     if(hasChanged)
                        {
                           // return to the user groups fragment and pass the modified group back
                           Intent resultIntent = new Intent();
                           setResult(Activity.RESULT_OK, resultIntent);
                           resultIntent.putExtra("group", group);
                        }

                     // end the activity
                     finish();
                     break;


                  case R.id.delete_button:

                     // get the friend object from the view (tag was set in the GroupFriendItem class)
                     Friend friend = (Friend)v.getTag();

                     // determine if friend was added or removed
                     if(friend.selected)
                        {
                           // remove friend from group
                           group.friendsList.remove(friend.ID);
                        }
                     else
                        {
                           // add friend to group
                           group.friendsList.add(friend.ID);
                        }

                     // set flag that the membership has been modified
                     hasChanged = true;

                     // update the listview
                     updateGroupFriendList();
                     break;
               }
         }


      /**
       * This method clears and repopulates the listview using the wall post data and processes any new friend requests
       **/
      public void updateGroupFriendList()
         {
            // create the friends list
            createGroupFriendsList();

            // notify the adapter about the data change
            adapter.notifyDataSetChanged();
         }


      /**
       * This method sorts the friends list in the listview: friend requests go first then the accepted/pending friends. Each group is sorted alphabetically
       **/
      private void sortGroupFriendsList()
         {
            int firstHeader, secondHeader;

            firstHeader = listViewItems.indexOf(new GroupHeaderItem("Current Group Members"));
            secondHeader = listViewItems.indexOf(new GroupHeaderItem("Suggested Group Members"));

            if (firstHeader + 1 == secondHeader)
               {
                  listViewItems.add(firstHeader + 1, new GroupNoFriendItem("No current members"));
                  secondHeader++;
               }

            if (secondHeader + 1 == listViewItems.size())
               {
                  listViewItems.add(secondHeader + 1, new GroupNoFriendItem("No suggested members"));
               }

            Comparator<ListViewManageGroupItem> friendNameComparator = new Comparator<ListViewManageGroupItem>()
               {
                  public int compare(ListViewManageGroupItem emp1, ListViewManageGroupItem emp2)
                     {
                        return emp1.getName().compareToIgnoreCase(emp2.getName());
                     }
               };

            Collections.sort(listViewItems.subList(firstHeader + 1, secondHeader), friendNameComparator);
            Collections.sort(listViewItems.subList(secondHeader + 1, listViewItems.size()), friendNameComparator);
         }


      /**
       * This method creates all the list view items and headers based on the current friends and friend request hashmaps
       **/
      private void createGroupFriendsList()
         {
            // add data to list view
            listViewItems.clear();

            // add the new requests section header
            listViewItems.add(0, new GroupHeaderItem("Current Group Members"));

            // add any friend requests to the top of the list view
            for (int i = 0; i < group.friendsList.size(); i++)
               {
                  String friendID = group.friendsList.get(i);
                  Friend friend = friendsList.get(friendID);

                  listViewItems.add(new GroupFriendItem(this, friend, true));
               }

            // add the current and pending friends section header
            listViewItems.add(new GroupHeaderItem("Suggested Group Members"));

            // loop through friends list and add all current friends
            for (Map.Entry<String, Friend> entry : friendsList.entrySet())
               {
                  Friend friend = entry.getValue();

                  if(!group.friendsList.contains(friend.ID))
                     {
                        listViewItems.add(new GroupFriendItem(this, friend, false));
                     }
               }


            // sort the friends list alphabetically
            sortGroupFriendsList();
         }
   }
