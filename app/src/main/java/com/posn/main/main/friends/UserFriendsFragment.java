package com.posn.main.main.friends;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.posn.R;
import com.posn.constants.Constants;
import com.posn.datatypes.Friend;
import com.posn.datatypes.RequestedFriend;
import com.posn.exceptions.POSNCryptoException;
import com.posn.main.main.MainActivity;
import com.posn.main.main.friends.asynctasks.NewFriendFinalAsyncTask;
import com.posn.main.main.friends.asynctasks.NewFriendInitialAsyncTask;
import com.posn.main.main.friends.asynctasks.NewFriendIntermediateAsyncTask;
import com.posn.main.main.friends.asynctasks.RemoveFriendAsyncTask;
import com.posn.main.main.friends.listview_items.AcceptedFriendItem;
import com.posn.main.main.friends.listview_items.HeaderItem;
import com.posn.main.main.friends.listview_items.ListViewFriendItem;
import com.posn.main.main.friends.listview_items.NoFriendItem;
import com.posn.main.main.friends.listview_items.PendingFriendItem;
import com.posn.main.main.friends.listview_items.RequestFriendItem;
import com.posn.managers.AppDataManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * This fragment class implements the functionality for the friend fragment:
 * <ul><li>Populates the list view using the data stored in the current friends and friend request hashmaps located in the data manager class in the main activity
 * <li>Allows for friends to be added or removed
 * <li>Allows the user to accept or decline friend requests</ul>
 * <p/>
 * Functionality should be added to view a friend's profile
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
       * Sets up the list view adapter to display the different friend types (Friend request, pending friend, accepted friend)
       **/
      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
         {
            System.out.println("FRIENDS ON CREATE!!!!!!!!!!!!!!");

            super.onCreate(savedInstanceState);

            // load the friend tab layout
            View view = inflater.inflate(R.layout.fragment_user_friends, container, false);

            // get the bottom bar buttons from the layout and set listeners
            RelativeLayout addFriendButton = (RelativeLayout) view.findViewById(R.id.add_friend_button);
            addFriendButton.setOnClickListener(this);

            // get the listview from the layout
            ListView lv = (ListView) view.findViewById(R.id.listView1);

            // get the main activity to access data
            activity = (MainActivity) getActivity();
            dataManager = activity.dataManager;

            // create a new list view adapter and set it to the list view
            adapter = new FriendsArrayAdapter(getActivity(), listViewItems);
            lv.setAdapter(adapter);

            // get the friends and friend request list from the main activity
            currentFriendsList = dataManager.friendManager.currentFriends;
            friendRequestsList = dataManager.friendManager.friendRequests;

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
       * This method is called after a new friend request has been made or a friend request has been accepted and the user selected the friend groups
       * The requested friend object is returned and the appropriate async task is launched
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

                     // create a new activity intent to add a friend
                     Intent intent = new Intent(getActivity(), AddFriendsActivity.class);

                     intent.putExtra("type", Constants.TYPE_FRIEND_REQUEST_NEW);

                     // pass the list of groups to the activity
                     intent.putParcelableArrayListExtra("groups", dataManager.userGroupManager.getUserGroupsArrayList());

                     // start the activity and get the result from it
                     startActivityForResult(intent, Constants.RESULT_ADD_FRIEND);
                     break;


                  case R.id.confirm_button:

                     // get the accepted friend
                     RequestedFriend friend = (RequestedFriend) v.getTag();

                     // remove them from the friend request list
                     newFriendNum--;
                     activity.updateTabNotificationNum(fragNum, newFriendNum);

                     // create a new activity intent to add friend groups
                     intent = new Intent(getActivity(), AddFriendsActivity.class);
                     intent.putExtra("type", Constants.TYPE_FRIEND_REQUEST_ACCEPT);
                     intent.putParcelableArrayListExtra("groups", dataManager.userGroupManager.getUserGroupsArrayList());
                     intent.putExtra("requestedFriend", friend);

                     startActivityForResult(intent, Constants.RESULT_ADD_FRIEND_GROUPS);
                     break;


                  case R.id.decline_button:

                     // get the declined friend
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

                     // get the friend object of the deleted friend
                     final Friend deleteFriend = (Friend) v.getTag();

                     // create a new confirmation dialog to ask if the user really wants to remove that friend
                     new AlertDialog.Builder(activity)
                         .setTitle("Friend Removal Confirmation")
                         .setMessage(Html.fromHtml("Are you sure you to remove: <b>" + deleteFriend.name + "</b> from your friends list?"))
                         .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                               @Override
                               public void onClick(DialogInterface dialog, int which)
                                  {
                                     // start an async task to remove the friend
                                     new RemoveFriendAsyncTask(UserFriendsFragment.this, deleteFriend).execute();
                                  }

                            })
                         .setNegativeButton("No", null)
                         .show();

                     break;
               }
         }

      /**
       * This method clears and repopulates the listview using the wall post data and processes any new friend requests
       **/
      public void updateFriendList()
         {
            // check if a new friend needs to be added from URI
            try
               {
                  RequestedFriend requestedFriend = dataManager.processFriendRequest();

                  // check if a requested friend was returned (null indicates no new request)
                  if (requestedFriend != null)
                     {
                        dataManager.saveFriendListAppFile(true);

                        // start final phase
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

            // create the friends list
            createFriendsList();

            // notify the adapter about the data change
            adapter.notifyDataSetChanged();
         }


      /**
       * This method sorts the friends list in the listview: friend requests go first then the accepted/pending friends. Each group is sorted alphabetically
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
       * This method creates all the list view items and headers based on the current friends and friend request hashmaps
       **/
      private void createFriendsList()
         {
            System.out.println("CREATING FRIENDS!!");
            newFriendNum = 0;

            // add data to list view
            listViewItems.clear();

            // add the new requests section header
            listViewItems.add(0, new HeaderItem("Friend Requests"));

            // add any friend requests to the top of the list view
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
