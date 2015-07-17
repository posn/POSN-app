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
import com.posn.asynctasks.friends.AsyncResponseFriends;
import com.posn.asynctasks.friends.LoadFriendsListAsyncTask;
import com.posn.asynctasks.friends.SaveFriendsListAsyncTask;
import com.posn.datatypes.Friend;
import com.posn.email.EmailSender;
import com.posn.main.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class UserFriendsFragment extends Fragment implements OnClickListener, AsyncResponseFriends
   {
      static final int ADD_FRIEND_RESULT = 1;

      static final int STATUS_ACCEPTED = 1;
      static final int STATUS_REQUEST = 2;
      static final int STATUS_PENDING = 3;


      // declare variables
      Context context;
      ListView lv;
      TableRow statusBar;
      RelativeLayout addFriendButton;
      ArrayList<Friend> friendList;
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
               Friend position = (Friend) v.getTag();

               friendRequestsList.remove(position);

               listViewItems.remove(new RequestFriendItem(confirmListener, declineListener, position));

               position.status = STATUS_ACCEPTED;
               friendList.add(position);
               listViewItems.add(new AcceptedFriendItem(deleteListener, position));

               sortFriendsList();
               adapter.notifyDataSetChanged();

               saveFriendsList();

               EmailSender test = new EmailSender("projectcloudbook@gmail.com", "cnlpass!!");
               // test.sendMail("POSN TEST!", "SUCCESS!\n\nhttp://posn.com/data1/data2/data3_data4", "POSN", "eklukovich92@hotmail.com");
               test.sendMail("POSN - Confirmed Friend Request", "SUCCESS!\n\nhttp://posn.com/confirm/" + app.getFirstName() + "/" + app.getLastName() + "/" + app.getEmailAddress(), "POSN", position.email);
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

               saveFriendsList();


               // SEND NOTIFICATION TO FRIEND ABOUT DECLINE
            }
      };

      OnClickListener deleteListener = new OnClickListener()
      {
         @Override
         public void onClick(View v)
            {
               Friend position = (Friend) v.getTag();

               friendList.remove(position);

               listViewItems.remove(new AcceptedFriendItem(deleteListener, position));

               sortFriendsList();
               adapter.notifyDataSetChanged();

               saveFriendsList();
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


            app = ((MainActivity) getActivity()).app;
            friendList = app.friendList;
            friendRequestsList = app.friendRequestsList;
            //friendList.clear();

            //createFriendsList();
            // saveFriendsList();

            if (friendList.isEmpty())
               {
                  loadFriendsList();
               }
            else
               {
                  createFriendsList();
               }

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
                  listViewItems.add(new PendingFriendItem(friendList.get(friendList.size() - 1)));
                  sortFriendsList();
                  adapter.notifyDataSetChanged();
                  saveFriendsList();
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
            boolean modified = false;

            if (friendList.size() == 0)
               {
                  friendRequestsList.add(new Friend("Daniel Chavez", STATUS_REQUEST));
                  friendRequestsList.add(new Friend("Cam Rowe", STATUS_REQUEST));
                  friendList.add(new Friend("Allen Rice", STATUS_ACCEPTED));
                  friendList.add(new Friend("Wes Hudson", STATUS_PENDING));
                  friendList.add(new Friend("Ryan Rice", STATUS_ACCEPTED));
                  friendList.add(new Friend("Tim Walker", STATUS_ACCEPTED));
                  friendList.add(new Friend("Jack Denzeler", STATUS_ACCEPTED));
                  friendList.add(new Friend("Bob Smith", STATUS_PENDING));
                  friendList.add(new Friend("John Hunter", STATUS_ACCEPTED));
               }

            if (app.newAcceptedFriend != null)
               {
                  int i = friendList.indexOf(app.newAcceptedFriend);
                  System.out.println("INDEX: " + i);
                  app.newAcceptedFriend.status = STATUS_ACCEPTED;
                  friendList.set(i, app.newAcceptedFriend);
                  app.newAcceptedFriend = null;
                  modified = true;
               }

            if(app.newFriendRequest != null)
               {
                  friendRequestsList.add(app.newFriendRequest);
                  app.newFriendRequest = null;
                  modified = true;
               }

            if(modified)
               {
                  saveFriendsList();
               }


            listViewItems.add(0, new HeaderItem("Friend Requests"));

            for (int i = 0; i < friendRequestsList.size(); i++)
               {
                  Friend friend = friendRequestsList.get(i);
                  listViewItems.add(new RequestFriendItem(confirmListener, declineListener, friend));
               }

           // HashMap<Integer, Friend> map = new HashMap<>();
            //map.entrySet()
           // for (Map.Entry<Integer, Friend> entry : map.entrySet())
            //   {
            //      System.out.println(entry.getKey() + "/" + entry.getValue());
            //   }


            listViewItems.add(new HeaderItem("Accepted and Pending Friends"));

            for (int i = 0; i < friendList.size(); i++)
               {
                  Friend friend = friendList.get(i);
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

      public void saveFriendsList()
         {
            new SaveFriendsListAsyncTask(getActivity(), app.wallFilePath + "/user_friends.txt", friendList, friendRequestsList).execute();
         }

      public void loadFriendsList()
         {
            asyncTask = new LoadFriendsListAsyncTask(getActivity(), app.wallFilePath + "/user_friends.txt");
            asyncTask.delegate = this;
            asyncTask.execute();
         }

      public void loadingFriendsFinished(ArrayList<Friend> friendList, ArrayList<Friend> friendRequestsList)
         {
            // add the loaded data to the array list and hashmap
            this.friendList.addAll(friendList);
            this.friendRequestsList.addAll(friendRequestsList);

            createFriendsList();

            // notify the adapter about the data change
            adapter.notifyDataSetChanged();
         }
   }
