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

import com.posn.R;
import com.posn.application.POSNApplication;
import com.posn.asynctasks.friends.LoadFriendsListAsyncTask;
import com.posn.datatypes.Friend;
import com.posn.email.EmailSender;
import com.posn.main.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class UserFriendsFragment extends Fragment implements OnClickListener
   {
      static final int ADD_FRIEND_RESULT = 1;

      static final int STATUS_ACCEPTED = 1;
      static final int STATUS_REQUEST = 2;
      static final int STATUS_PENDING = 3;


      // declare variables
      MainActivity activity;
      Context context;
      ListView lv;
      TableRow statusBar;
      RelativeLayout addFriendButton;
      HashMap<String, Friend> friendList;
      ArrayList<Friend> friendRequestsList;
      ArrayList<ListViewFriendItem> listViewItems = new ArrayList<>();
      POSNApplication app;
      FriendsArrayAdapter adapter;

      LoadFriendsListAsyncTask asyncTask;


      OnClickListener confirmListener = new OnClickListener()
      {
         @Override
         public void onClick(View v)
            {
               Friend friend = (Friend) v.getTag();

               friendRequestsList.remove(friend);

               listViewItems.remove(new RequestFriendItem(confirmListener, declineListener, friend));

               friend.status = STATUS_ACCEPTED;
               friendList.put(friend.id, friend);
               listViewItems.add(new AcceptedFriendItem(deleteListener, friend));

               sortFriendsList();
               adapter.notifyDataSetChanged();

               activity.saveFriendsList();

               EmailSender test = new EmailSender("projectcloudbook@gmail.com", "cnlpass!!");
               // test.sendMail("POSN TEST!", "SUCCESS!\n\nhttp://posn.com/data1/data2/data3_data4", "POSN", "eklukovich92@hotmail.com");
               test.sendMail("POSN - Confirmed Friend Request", "SUCCESS!\n\nhttp://posn.com/confirm/" + app.getFirstName() + "/" + app.getLastName() + "/" + app.getEmailAddress(), "POSN", friend.email);
            }
      };

      OnClickListener declineListener = new OnClickListener()
      {
         @Override
         public void onClick(View v)
            {
               Friend position = (Friend) v.getTag();

               friendRequestsList.remove(position);

               listViewItems.remove(new RequestFriendItem(confirmListener, declineListener, position));


               sortFriendsList();
               adapter.notifyDataSetChanged();

               activity.saveFriendsList();


               // SEND NOTIFICATION TO FRIEND ABOUT DECLINE
            }
      };

      OnClickListener deleteListener = new OnClickListener()
      {
         @Override
         public void onClick(View v)
            {
               Friend position = (Friend) v.getTag();

               friendList.remove(position.id);

               listViewItems.remove(new AcceptedFriendItem(deleteListener, position));

               sortFriendsList();
               adapter.notifyDataSetChanged();

               activity.saveFriendsList();
            }
      };

      @Override
      public void onResume()
         {
            super.onResume();
         }


      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            // load the friend tab layout
            View view = inflater.inflate(R.layout.fragment_user_friends, container, false);
            context = getActivity();

            addFriendButton = (RelativeLayout) view.findViewById(R.id.add_friend_button);
            addFriendButton.setOnClickListener(this);

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
            System.out.println("FRIEND ONCREATEVIEW1");

            friendList = activity.masterFriendList;
            friendRequestsList = activity.masterRequestsList;
System.out.println("FRIEND ONCREATEVIEW2");

              // createFriendsList();
           //  saveFriendsList();

/*
            if (friendList.isEmpty())
               {
                  loadFriendsList();
               }
            else
               {
                  createFriendsList();
               }
*/

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
            if (requestCode == ADD_FRIEND_RESULT && resultCode == Activity.RESULT_OK)
               {
                  Friend friend = data.getParcelableExtra("friend");
                  System.out.println(friend.name + " | " + friend.email);

                  friendList.put(friend.id, friend);
                  listViewItems.add(new PendingFriendItem(friend));
                  sortFriendsList();
                  adapter.notifyDataSetChanged();
                  activity.saveFriendsList();
               }
         }


      @Override
      public void onAttach(Activity activity)
         {
            super.onAttach(activity);
            context = getActivity();
         }


      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.add_friend_button:

                     Intent intent = new Intent(getActivity(), AddFriendsActivity.class);
                     startActivityForResult(intent, ADD_FRIEND_RESULT);
                     break;
               }
         }


      private void sortFriendsList()
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

      public void createFriendsList()
         {
            System.out.println("CREATING FRIENDS!!");

            boolean modified = false;

            if (friendList.isEmpty())
               {
                  System.out.println("CREATING asd!!");

                  Friend friend = new Friend("Daniel Chavez", "testuser1@gmail.com", STATUS_REQUEST);
                  friendRequestsList.add(friend);
                  friend = new Friend("Cam Rowe", "testuser2@gmail.com", STATUS_REQUEST);
                  friendRequestsList.add(friend);
                  friend = new Friend("Allen Rice", "testuser3@gmail.com", STATUS_ACCEPTED);
                  friendList.put(friend.id, friend);
                  friend = new Friend("Wes Hudson", "testuser4@gmail.com", STATUS_PENDING);
                  friendList.put(friend.id, friend);
                  friend = new Friend("Ryan Rice", "testuser5@gmail.com", STATUS_ACCEPTED);
                  friendList.put(friend.id, friend);
                  friend = new Friend("Tim Walker", "testuser6@gmail.com", STATUS_ACCEPTED);
                  friendList.put(friend.id, friend);
                  friend = new Friend("Jack Denzeler", "testuser7@gmail.com", STATUS_ACCEPTED);
                  friendList.put(friend.id, friend);
                  friend = new Friend("Bob Smith", "testuser8@gmail.com", STATUS_PENDING);
                  friendList.put(friend.id, friend);
                  friend = new Friend("John Hunter", "testuser9@gmail.com", STATUS_ACCEPTED);
                  friendList.put(friend.id, friend);
               }

            if (app.newAcceptedFriend != null)
               {
                  //int i = friendList.indexOf(app.newAcceptedFriend);
                  //System.out.println("INDEX: " + i);
                  app.newAcceptedFriend.status = STATUS_ACCEPTED;
                  friendList.put(app.newAcceptedFriend.id, app.newAcceptedFriend);
                  app.newAcceptedFriend = null;
                  modified = true;
               }

            if (app.newFriendRequest != null)
               {
                  friendRequestsList.add(app.newFriendRequest);
                  app.newFriendRequest = null;
                  modified = true;
               }

            if (modified)
               {
                  activity.saveFriendsList();
               }

            listViewItems.clear();
            listViewItems.add(0, new HeaderItem("Friend Requests"));

            for (int i = 0; i < friendRequestsList.size(); i++)
               {
                  Friend friend = friendRequestsList.get(i);
                  listViewItems.add(new RequestFriendItem(confirmListener, declineListener, friend));
               }

            listViewItems.add(new HeaderItem("Accepted and Pending Friends"));

            for (Map.Entry<String, Friend> entry : friendList.entrySet())
               {
                  Friend friend = entry.getValue();
                  int type = friend.status;

                  if (type == STATUS_ACCEPTED)
                     {
                        listViewItems.add(new AcceptedFriendItem(deleteListener, friend));
                     }
                  else
                     {
                        listViewItems.add(new PendingFriendItem(friend));
                     }

               }

            sortFriendsList();
         }


      public void updateFriendList()
         {
            createFriendsList();

            // notify the adapter about the data change
            adapter.notifyDataSetChanged();
         }
   }
