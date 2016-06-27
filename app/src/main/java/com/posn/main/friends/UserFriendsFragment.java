package com.posn.main.friends;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.posn.Constants;
import com.posn.R;
import com.posn.asynctasks.friends.NewFriendFinalAsyncTask;
import com.posn.asynctasks.friends.NewFriendInitialAsyncTask;
import com.posn.asynctasks.friends.NewFriendIntermediateAsyncTask;
import com.posn.datatypes.Friend;
import com.posn.datatypes.RequestedFriend;
import com.posn.exceptions.POSNCryptoException;
import com.posn.main.AppDataManager;
import com.posn.main.MainActivity;
import com.posn.main.groups.CreateGroupDialogFragment;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * This fragment class implements the functionality for the friendID fragment:
 * <ul><li>Populates the list view using the data stored in the current friends and friendID request hashmaps located in the data manager class in the main activity
 * <li>Allows for friends to be added or removed
 * <li>Allows the user to accept or decline friendID requests</ul>
 *
 * Functionality should be added to view a friendID's profile
 **/
public class UserFriendsFragment extends Fragment implements OnClickListener
   {
      // user interface variables
      private HashMap<String, Friend> currentFriendsList;
      private ArrayList<RequestedFriend> friendRequestsList;
      public ArrayList<ListViewFriendItem> listViewItems = new ArrayList<>();
      public FriendsArrayAdapter adapter;
      public int newFriendNum = 0;


      public MainActivity activity;
      public AppDataManager dataManager;

      private int fragNum;


      /**
       * This method is called when the activity needs to be created and fetches and implements the user interface elements
       * Sets up the list view adapter to display the different friendID types (Friend request, pending friendID, accepted friendID)
       **/
      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
         {
            System.out.println("FRIENDS ON CREATE!!!!!!!!!!!!!!");

            super.onCreate(savedInstanceState);

            // load the friendID tab layout
            View view = inflater.inflate(R.layout.fragment_user_friends, container, false);

            RelativeLayout addFriendButton = (RelativeLayout) view.findViewById(R.id.add_friend_button);
            addFriendButton.setOnClickListener(this);

            RelativeLayout addGroupButton = (RelativeLayout) view.findViewById(R.id.add_group_button);
            addGroupButton.setOnClickListener(this);

            ListView lv = (ListView) view.findViewById(R.id.listView1);

            // get the main activity to access data
            activity = (MainActivity) getActivity();
            dataManager = activity.dataManager;

            // create a new list view adapter and set it to the list view
            adapter = new FriendsArrayAdapter(getActivity(), listViewItems);
            lv.setAdapter(adapter);

            // get the friends and friendID request list from the main activity
            currentFriendsList = dataManager.masterFriendList.currentFriends;
            friendRequestsList = dataManager.masterFriendList.friendRequests;

            // check if there are any friends, if so then update listview
            if (activity.isInitialized)
               {
                  updateFriendList();
               }

            return view;
         }


      /**
       * This method is called to set the fragment number so it can be used to update the number of new wall posts
       **/
      public void setFragNum(int position)
         {
            fragNum = position;
         }


      /**
       * This method is called after a new friendID request has been made or a friendID request has been accepted and the user selected the friendID groups
       * The requested friendID object is returned and the appropriate async task is launched
       **/
      @Override
      public void onActivityResult(int requestCode, int resultCode, Intent data)
         {
            if (requestCode == Constants.RESULT_ADD_FRIEND && resultCode == Activity.RESULT_OK)
               {
                  RequestedFriend friend = data.getParcelableExtra("requestedFriend");
                  new NewFriendInitialAsyncTask(this, friend).execute();
               }
            else if (requestCode == Constants.RESULT_ADD_FRIEND_GROUPS && resultCode == Activity.RESULT_OK)
               {
                  RequestedFriend friend = data.getParcelableExtra("requestedFriend");
                  new NewFriendIntermediateAsyncTask(this, friend).execute();
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
                  case R.id.add_friend_button:

                     // create a new activity intent to add a friendID
                     Intent intent = new Intent(getActivity(), AddFriendsActivity.class);

                     intent.putExtra("type", Constants.TYPE_FRIEND_REQUEST_NEW);

                     // pass the list of groups to the activity
                     intent.putParcelableArrayListExtra("groups", dataManager.userGroupList.getUserGroupsArrayList());

                     // start the activity and get the result from it
                     startActivityForResult(intent, Constants.RESULT_ADD_FRIEND);
                     break;


                  case R.id.add_group_button:

                     // create new dialog fragment to get the new group name
                     CreateGroupDialogFragment groupFrag = new CreateGroupDialogFragment();

                     // show the dialog
                     groupFrag.show(getActivity().getFragmentManager(), "group");
                     break;


                  case R.id.confirm_button:
                     // get the accepted friendID
                     RequestedFriend friend = (RequestedFriend) v.getTag();

                     // remove them from the friendID request list
                     newFriendNum--;
                     activity.updateTabNotificationNum(fragNum, newFriendNum);

                     // remove them from the list view
                     //listViewItems.remove(new RequestFriendItem(this, this, friend));


                     // create a new activity intent to add friendID groups
                     intent = new Intent(getActivity(), AddFriendsActivity.class);
                     intent.putExtra("type", Constants.TYPE_FRIEND_REQUEST_ACCEPT);
                     intent.putParcelableArrayListExtra("groups", dataManager.userGroupList.getUserGroupsArrayList());
                     intent.putExtra("requestedFriend", friend);

                     startActivityForResult(intent, Constants.RESULT_ADD_FRIEND_GROUPS);


                     break;


                  case R.id.decline_button:
                     // get the declined friendID
                     RequestedFriend requestedFriend = (RequestedFriend) v.getTag();

                     // remove them from the request list
                     friendRequestsList.remove(requestedFriend);

                     // remove them from the list view
                     listViewItems.remove(new RequestFriendItem(this, this, requestedFriend));

                     // sort the friends and update the list view
                     sortFriendsList();
                     adapter.notifyDataSetChanged();
                     newFriendNum--;
                     activity.updateTabNotificationNum(fragNum, newFriendNum);

                     // save the updated friends list to file
                     try
                        {
                           dataManager.saveFriendListAppFile(true);
                        }
                     catch (IOException | JSONException | POSNCryptoException e)
                        {
                           e.printStackTrace();
                        }

                     break;


                  case R.id.delete_button:
                     Friend position = (Friend) v.getTag();

                     currentFriendsList.remove(position.ID);

                     listViewItems.remove(new AcceptedFriendItem(this, position));

                     sortFriendsList();
                     adapter.notifyDataSetChanged();

                     try
                        {
                           dataManager.saveFriendListAppFile(true);
                        }
                     catch (IOException | JSONException | POSNCryptoException e)
                        {
                           e.printStackTrace();
                        }
                     break;
               }
         }

      /**
       * This method clears and repopulates the listview using the wall post data and processes any new friendID requests
       **/
      public void updateFriendList()
         {
            // check if a new friendID needs to be added from URI
            try
               {
                  RequestedFriend requestedFriend = dataManager.processFriendRequest();

                  // check if a requested friendID was returned (null indicates no new request)
                  if (requestedFriend != null)
                     {
                        dataManager.saveFriendListAppFile(true);

                        // start phase FRAG_NUM
                        if (requestedFriend.status == Constants.STATUS_ACCEPTED)
                           {
                              new NewFriendFinalAsyncTask(this, requestedFriend).execute();
                           }
                     }
               }
            catch (IOException | JSONException | POSNCryptoException e)
               {
                  e.printStackTrace();
               }

            createFriendsList();

            // notify the adapter about the data change
            adapter.notifyDataSetChanged();
         }


      /**
       * This method sorts the friends list in the listview: friendID requests go first then the accepted/pending friends. Each group is sorted alphabetically
       **/
      private void sortFriendsList()
         {
            int firstHeader, secondHeader;

            firstHeader = listViewItems.indexOf(new HeaderItem("Friend Requests"));
            secondHeader = listViewItems.indexOf(new HeaderItem("Accepted and Pending Friends"));

            if (firstHeader + 1 == secondHeader)
               {
                  listViewItems.add(firstHeader + 1, new NoFriendItem("No new requests"));
                  secondHeader++;
               }

            if (secondHeader + 1 == listViewItems.size())
               {
                  listViewItems.add(secondHeader + 1, new NoFriendItem("No current friends"));
               }

            Comparator<ListViewFriendItem> friendNameComparator = new Comparator<ListViewFriendItem>()
               {
                  public int compare(ListViewFriendItem emp1, ListViewFriendItem emp2)
                     {
                        return emp1.getName().compareToIgnoreCase(emp2.getName());
                     }
               };

            Collections.sort(listViewItems.subList(firstHeader + 1, secondHeader), friendNameComparator);
            Collections.sort(listViewItems.subList(secondHeader + 1, listViewItems.size()), friendNameComparator);
         }


      /**
       * This method creates all the list view items and headers based on the current friends and friendID request hashmaps
       **/
      private void createFriendsList()
         {
            System.out.println("CREATING FRIENDS!!");
            newFriendNum = 0;

            // add data to list view
            listViewItems.clear();

            // add the new requests section header
            listViewItems.add(0, new HeaderItem("Friend Requests"));

            // add any friendID requests to the top of the list view
            for (int i = 0; i < friendRequestsList.size(); i++)
               {
                  RequestedFriend friend = friendRequestsList.get(i);
                  if (friend.status == Constants.STATUS_REQUEST)
                     {
                        listViewItems.add(new RequestFriendItem(this, this, friend));
                        newFriendNum++;
                     }
               }

            // add the current and pending friends section header
            listViewItems.add(new HeaderItem("Accepted and Pending Friends"));

            // loop through friends list and add all current friends
            for (Map.Entry<String, Friend> entry : currentFriendsList.entrySet())
               {
                  Friend friend = entry.getValue();
                  listViewItems.add(new AcceptedFriendItem(this, friend));
               }

            // loop through and add all pending friends
            for (int i = 0; i < friendRequestsList.size(); i++)
               {
                  RequestedFriend friend = friendRequestsList.get(i);
                  if (friend.status == Constants.STATUS_PENDING)
                     {
                        listViewItems.add(new PendingFriendItem(friend));
                     }
               }

            // sort the friends list alphabetically
            sortFriendsList();

            // update tab number
            activity.updateTabNotificationNum(fragNum, newFriendNum);
         }

   }
