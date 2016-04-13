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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.posn.Constants;
import com.posn.R;
import com.posn.application.POSNApplication;
import com.posn.asynctasks.friends.NewFriendFinalAsyncTask;
import com.posn.asynctasks.friends.NewFriendInitialAsyncTask;
import com.posn.asynctasks.friends.NewFriendIntermediateAsyncTask;
import com.posn.datatypes.Friend;
import com.posn.datatypes.RequestedFriend;
import com.posn.main.MainActivity;
import com.posn.main.groups.CreateGroupDialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class UserFriendsFragment extends Fragment implements OnClickListener
   {


      // declare variables
      public MainActivity activity;
      Context context;
      ListView lv;
      TableRow statusBar;
      RelativeLayout addFriendButton;
      RelativeLayout addGroupButton;

      HashMap<String, Friend> friendList;
      ArrayList<RequestedFriend> friendRequestsList;
      public ArrayList<ListViewFriendItem> listViewItems = new ArrayList<>();
      POSNApplication app;
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

            // get the status bar
            statusBar = (TableRow) view.findViewById(R.id.status_bar);

            lv.setOnScrollListener(new OnScrollListener()
               {
                  private int mLastFirstVisibleItem;

                  @Override
                  public void onScrollStateChanged(AbsListView view, int scrollState)
                     {
                     }

                  @Override
                  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
                     {
                        if (mLastFirstVisibleItem < firstVisibleItem)
                           {
                              statusBar.setVisibility(View.GONE);
                           }
                        if (mLastFirstVisibleItem > firstVisibleItem)
                           {
                              statusBar.setVisibility(View.VISIBLE);
                           }
                        mLastFirstVisibleItem = firstVisibleItem;
                     }
               });

            activity = (MainActivity) getActivity();
            app = activity.app;

            friendList = activity.masterFriendList.currentFriends;
            friendRequestsList = activity.masterFriendList.friendRequests;

            adapter = new FriendsArrayAdapter(getActivity(), listViewItems);
            lv.setAdapter(adapter);

            return view;
         }


      @Override
      public void onActivityCreated(Bundle savedInstanceState)
         {
            super.onActivityCreated(savedInstanceState);
            onResume();
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
                  System.out.println(friend.name + " | " + friend.email);

                  for (int i = 0; i < friend.groups.size(); i++)
                     {
                        System.out.println(activity.groupList.groups.get(friend.groups.get(i)).name);
                     }


                  // create a new current friend for the accepted user and add them to the current friends list
                  friendList.put(friend.ID, new Friend(friend));

                  // add them to the list view as an accepted friend
                  listViewItems.add(new AcceptedFriendItem(this, new Friend(friend)));

                  // sort the friends and update the list view
                  sortFriendsList();
                  adapter.notifyDataSetChanged();

                  Toast.makeText(activity, "CONFIRMED!", Toast.LENGTH_SHORT).show();

                  // create AsyncTask to handle the intermediate phase
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
                     intent.putParcelableArrayListExtra("groups", activity.groupList.getList());

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
                     activity.newFriendNum--;
                     activity.updateTab(3, true);

                     // remove them from the list view
                     listViewItems.remove(new RequestFriendItem(this, this, friend));


                     // create a new activity intent to add friend groups
                     intent = new Intent(getActivity(), AddFriendsActivity.class);
                     intent.putExtra("type", Constants.TYPE_FRIEND_GROUPS);
                     intent.putParcelableArrayListExtra("groups", activity.groupList.getList());
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
                     activity.masterFriendList.saveFriendsListToFileAsyncTask(Constants.applicationDataFilePath + "/user_friends.txt");

                     // SEND NOTIFICATION TO FRIEND ABOUT DECLINE
                     break;


                  case R.id.delete_button:
                     Friend position = (Friend) v.getTag();

                     friendList.remove(position.id);

                     listViewItems.remove(new AcceptedFriendItem(this, position));

                     sortFriendsList();
                     adapter.notifyDataSetChanged();

                     activity.masterFriendList.saveFriendsListToFileAsyncTask(Constants.applicationDataFilePath + "/user_friends.txt");
                     break;
               }
         }

      public void createFriendsList()
         {
            System.out.println("CREATING FRIENDS!!");

            // check if a new friend needs to be added from URI
            if (activity.requestedFriend != null)
               {
                  // check if the friend is a new incoming friend request
                  if (activity.requestedFriend.status == Constants.STATUS_REQUEST)
                     {
                        System.out.println("ADDED");
                        friendRequestsList.add(activity.requestedFriend);
                        activity.requestedFriend = null;
                     }
                  // check if the friend accepted the sent request
                  else if (activity.requestedFriend.status == Constants.STATUS_ACCEPTED)
                     {
                        System.out.println("ACCEPT!!!: " + activity.requestedFriend.name + " | " + activity.masterFriendList.friendRequests.contains(activity.requestedFriend));
                        RequestedFriend pendingFriend = activity.masterFriendList.friendRequests.get(activity.masterFriendList.friendRequests.indexOf(activity.requestedFriend));

                        // merge data
                        RequestedFriend friend = new RequestedFriend();

                        friend.name = pendingFriend.name;
                        friend.email = pendingFriend.email;
                        friend.groups = pendingFriend.groups;
                        friend.status = Constants.STATUS_ACCEPTED;
                        friend.fileLink = activity.requestedFriend.fileLink;
                        friend.publicKey = activity.requestedFriend.publicKey;
                        friend.ID = activity.requestedFriend.ID;
                        friend.nonce = activity.requestedFriend.nonce;
                        friend.nonce2 = activity.requestedFriend.nonce2;

                        activity.masterFriendList.friendRequests.remove(activity.requestedFriend);

                        activity.masterFriendList.currentFriends.put(activity.requestedFriend.ID, new Friend(friend));

                        // start phase 3
                        new NewFriendFinalAsyncTask(this, friend).execute();

                     }

                  activity.masterFriendList.saveFriendsListToFileAsyncTask(Constants.applicationDataFilePath + "/user_friends.txt");
               }

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
         }


      public void updateFriendList()
         {
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
                  listViewItems.add(1, new NoRequestFriendItem());
                  secondHeader++;
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
