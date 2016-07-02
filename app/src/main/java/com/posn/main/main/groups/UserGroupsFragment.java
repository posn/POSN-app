package com.posn.main.main.groups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.posn.R;
import com.posn.constants.Constants;
import com.posn.datatypes.UserGroup;
import com.posn.main.main.MainActivity;
import com.posn.main.main.groups.asynctasks.ManageGroupAsyncTask;
import com.posn.main.main.groups.asynctasks.NewGroupAsyncTask;
import com.posn.managers.AppDataManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


/**
 * This fragment class implements the functionality for the groups fragment:
 * <ul><li>Populates the list view using the data stored in the user group manager that is located in the data manager class in the main activity
 * <li>Allows for new groups to be added
 * <li>Allows the user to change which friends are part of which group
 * <li>Implementation to remove groups needs to be implemented</ul><p/>
 * Functionality should be added to view a friend's profile
 **/
public class UserGroupsFragment extends Fragment implements OnClickListener
   {
      // user interface variables
      private HashMap<String, UserGroup> userGroupList;

      public ArrayList<UserGroup> listViewItems = new ArrayList<>();
      public UserGroupArrayAdapter adapter;


      public MainActivity activity;
      public AppDataManager dataManager;

      /**
       * This method is called when the activity needs to be created and fetches and implements the user interface elements
       * Sets up the list view adapter to display the different user defined groups
       **/
      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
         {
            System.out.println("GROUPS ON CREATE!!!!!!!!!!!!!!");

            super.onCreate(savedInstanceState);

            // load the user group fragment layout
            View view = inflater.inflate(R.layout.fragment_user_groups, container, false);

            // get the bottom bar buttons from the layout and set listeners
            RelativeLayout addGroupButton = (RelativeLayout) view.findViewById(R.id.add_group_button);
            addGroupButton.setOnClickListener(this);

            // get the listview from the layout
            ListView lv = (ListView) view.findViewById(R.id.listView1);

            // get the main activity to access data
            activity = (MainActivity) getActivity();
            dataManager = activity.dataManager;

            // create a new list view adapter and set it to the list view
            adapter = new UserGroupArrayAdapter(getActivity(), listViewItems);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
               {
                  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                     {
                        // creates a new intent and launches an activity to view the friends who are part of the selected group
                        Intent intent = new Intent(getActivity(), ManageGroupActivity.class);
                        intent.putExtra("group", listViewItems.get(position));
                        intent.putExtra("friendsList", dataManager.friendManager.currentFriends);
                        startActivityForResult(intent, Constants.RESULT_MANAGE_GROUP);
                     }
               });

            // get the user groups list from the data manager
            userGroupList = dataManager.userGroupManager.userGroups;

            // check if there are any groups, if so then update listview
            if (activity.isInitialized)
               {
                  updateUserGroupListView();
               }

            return view;
         }


      /**
       * This method is called after a new group has been created or when the user group's members have changed.
       **/
      @Override
      public void onActivityResult(int requestCode, int resultCode, Intent data)
         {
            if (requestCode == Constants.RESULT_CREATE_GROUP && resultCode == Activity.RESULT_OK)
               {
                  // get the name of the new group
                  String newGroupName = data.getStringExtra("newGroupName");

                  // launch the asynctask to handle creating a new group
                  new NewGroupAsyncTask(this, newGroupName).execute();
               }
            else if (requestCode == Constants.RESULT_MANAGE_GROUP && resultCode == Activity.RESULT_OK)
               {
                  UserGroup modifiedGroup = data.getParcelableExtra("group");
                  new ManageGroupAsyncTask(this, modifiedGroup).execute();
               }
         }


      /**
       * This method is called when the user clicks the different user interface elements and implements each element's functionality
       **/
      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.add_group_button:

                     // create new dialog fragment to get the new group name
                     CreateGroupDialogFragment createGroupFrag = new CreateGroupDialogFragment();

                     // set the fragment that is created the dialog
                     createGroupFrag.setTargetFragment(this, Constants.RESULT_CREATE_GROUP);

                     // show the dialog
                     createGroupFrag.show(getFragmentManager().beginTransaction(), "group");
                     break;
               }
         }

      /**
       * This method clears and repopulates the listview with the group names
       **/
      public void updateUserGroupListView()
         {
            // add data to list view
            listViewItems.clear();

            // add all the groups to the listview
            listViewItems.addAll(userGroupList.values());

            // sort the friends list alphabetically
            sortUserGroupList();

            // notify the adapter about the data change
            adapter.notifyDataSetChanged();
         }


      /**
       * This method sorts the group list in the listview: group names are sorted alphabetically
       **/
      private void sortUserGroupList()
         {
            Comparator<UserGroup> groupNameComparator = new Comparator<UserGroup>()
               {
                  public int compare(UserGroup emp1, UserGroup emp2)
                     {
                        return emp1.name.compareToIgnoreCase(emp2.name);
                     }
               };

            Collections.sort(listViewItems, groupNameComparator);
         }
   }
