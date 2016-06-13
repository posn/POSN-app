package com.posn.main.friends;

import android.app.Activity;
import android.content.Context;
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
import com.posn.main.MainActivity;
import com.posn.main.AppDataManager;
import com.posn.main.groups.CreateGroupDialogFragment;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class UserFriendsFragment extends Fragment implements OnClickListener
   {
      // declare variables
      public MainActivity activity;
      AppDataManager dataManager;
      Context context;
      ListView lv;
      RelativeLayout addFriendButton;
      RelativeLayout addGroupButton;

      HashMap<String, Friend> friendList;
      ArrayList<RequestedFriend> friendRequestsList;
      public ArrayList<ListViewFriendItem> listViewItems = new ArrayList<>();
      public FriendsArrayAdapter adapter;


      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
         {
            System.out.println("FRIENDS ON CREATE!!!!!!!!!!!!!!");

            super.onCreate(savedInstanceState);

            // load the friend tab layout
            View view = inflater.inflate(R.layout.fragment_user_friends, container, false);
            context = getActivity();

            addFriendButton = (RelativeLayout) view.findViewById(R.id.add_friend_button);
            addFriendButton.setOnClickListener(this);

            addGroupButton = (RelativeLayout) view.findViewById(R.id.add_group_button);
            addGroupButton.setOnClickListener(this);

            lv = (ListView) view.findViewById(R.id.listView1);

            // get the main activity to access data
            activity = (MainActivity) getActivity();
            dataManager = activity.dataManager;

            // create a new list view adapter and set it to the list view
            adapter = new FriendsArrayAdapter(getActivity(), listViewItems);
            lv.setAdapter(adapter);

            // get the friends and friend request list from the main activity
            friendList = dataManager.masterFriendList.currentFriends;
            friendRequestsList = dataManager.masterFriendList.friendRequests;

            // check if there are any friends, if so then update listview
            if (activity.isInitialized)
               {
                  updateFriendList();
               }

            return view;
         }

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

      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.add_friend_button:

                     // create a new activity intent to add a friend
                     Intent intent = new Intent(getActivity(), AddFriendsActivity.class);

                     intent.putExtra("type", Constants.TYPE_FRIEND_INFO);

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
                     // get the accepted friend
                     RequestedFriend friend = (RequestedFriend) v.getTag();

                     // remove them from the friend request list
                     friendRequestsList.remove(friend);
                     activity.updateTab(3, true);

                     // remove them from the list view
                     listViewItems.remove(new RequestFriendItem(this, this, friend));


                     // create a new activity intent to add friend groups
                     intent = new Intent(getActivity(), AddFriendsActivity.class);
                     intent.putExtra("type", Constants.TYPE_FRIEND_GROUPS);
                     intent.putParcelableArrayListExtra("groups", dataManager.userGroupList.getUserGroupsArrayList());
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
                     activity.newFriendNum--;
                     activity.updateTab(3, true);

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

                     friendList.remove(position.ID);

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

      public void createFriendsList()
         {
            System.out.println("CREATING FRIENDS!!");
            activity.newFriendNum = 0;

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
                        activity.newFriendNum++;
                     }
               }

            // add the current and pending friends section header
            listViewItems.add(new HeaderItem("Accepted and Pending Friends"));

            // loop through friends list and add all current friends
            for (Map.Entry<String, Friend> entry : friendList.entrySet())
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
            activity.updateTab(3, true);
         }


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

                        // start phase 3
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


      public void sortFriendsList()
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
   }
