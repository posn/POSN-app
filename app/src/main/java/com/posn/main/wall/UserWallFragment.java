package com.posn.main.wall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.posn.Constants;
import com.posn.R;
import com.posn.asynctasks.GetFriendContentAsyncTask;
import com.posn.asynctasks.wall.NewWallPhotoPostAsyncTask;
import com.posn.asynctasks.wall.NewWallStatusPostAsyncTask;
import com.posn.datatypes.Friend;
import com.posn.datatypes.Post;
import com.posn.main.MainActivity;
import com.posn.main.wall.posts.ListViewPostItem;
import com.posn.main.wall.posts.PhotoPostItem;
import com.posn.main.wall.posts.StatusPostItem;
import com.posn.main.wall.posts.VideoPostItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class UserWallFragment extends Fragment implements OnClickListener, OnRefreshListener
   {
      // declare variables
      Context context;
      RelativeLayout statusButton, photoButton, checkInButton;
      public TextView noWallPostsText;
      ListView lv;
      TableRow statusBar;

      public ArrayList<ListViewPostItem> listViewItems = new ArrayList<>();
      HashMap<String, Post> wallPostData;
      SwipeRefreshLayout swipeLayout;
      public WallArrayAdapter adapter;

      public MainActivity activity;


      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            // load the system tab layout
            View view = inflater.inflate(R.layout.fragment_user_wall, container, false);
            context = getActivity();

            // get the application

            // get the main activity
            activity = (MainActivity) getActivity();

            // get the listview from the layout
            lv = (ListView) view.findViewById(R.id.listView1);
            noWallPostsText = (TextView) view.findViewById(R.id.notification_text);

            // get the buttons from the layout
            statusButton = (RelativeLayout) view.findViewById(R.id.status_button);
            photoButton = (RelativeLayout) view.findViewById(R.id.photo_button);
            checkInButton = (RelativeLayout) view.findViewById(R.id.checkin_button);

            // set an onclick listener for each button
            statusButton.setOnClickListener(this);
            photoButton.setOnClickListener(this);
            checkInButton.setOnClickListener(this);

            // get the status bar
            statusBar = (TableRow) view.findViewById(R.id.status_bar);

            swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
            swipeLayout.setOnRefreshListener(this);


            //
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
                        int topRowVerticalPosition = (lv == null || lv.getChildCount() == 0) ? 0 : lv.getChildAt(0).getTop();
                        swipeLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);

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


            adapter = new WallArrayAdapter(getActivity(), listViewItems);
            lv.setAdapter(adapter);

            // get the wall post data from activity
            wallPostData = activity.masterWallPostList.wallPosts;
            if (wallPostData.size() != 0)
               {
                  updateWallPosts();
               }

            return view;
         }

      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {

                  case R.id.status_button:
                     Intent intent = new Intent(context, CreateNewStatusPostActivity.class);
                     intent.putExtra("groups", activity.user.getUserGroupsArrayList());
                     startActivityForResult(intent, Constants.RESULT_CREATE_STATUS_POST);

                     break;

                  case R.id.photo_button:
                     intent = new Intent(context, CreateNewPhotoPostActivity.class);
                     intent.putExtra("groups", activity.user.getUserGroupsArrayList());
                     startActivityForResult(intent, Constants.RESULT_CREATE_PHOTO_POST);
                     break;

                  case R.id.checkin_button:

                     break;

               }
         }


      @Override
      public void onActivityResult(int requestCode, int resultCode, Intent data)
         {
            if (requestCode == Constants.RESULT_CREATE_STATUS_POST && resultCode == Activity.RESULT_OK)
               {
                  String status = data.getStringExtra("status");
                  ArrayList<String> selectGroups = data.getStringArrayListExtra("groups");
                  new NewWallStatusPostAsyncTask(this, selectGroups, status).execute();
               }
            else if (requestCode == Constants.RESULT_CREATE_PHOTO_POST && resultCode == Activity.RESULT_OK)
               {
                  String photopath = data.getStringExtra("photopath");
                  ArrayList<String> selectGroups = data.getStringArrayListExtra("groups");
                  new NewWallPhotoPostAsyncTask(this, selectGroups, photopath).execute();
               }
         }

      public void createWallPostsList()
         {
            listViewItems.clear();
            System.out.println("CREATING WALL POSTS!!!");
            String name;

            // loop through all the wall posts and add them to the listview
            for (Map.Entry<String, Post> entry : wallPostData.entrySet())
               {
                  // get the post
                  Post post = entry.getValue();

                  // get the name of the person who created the post
                  if (post.friendID.equals(activity.user.ID))
                     {
                        name = activity.user.firstName + " " + activity.user.lastName;
                     }
                  else
                     {
                        Friend friend = activity.masterFriendList.currentFriends.get(post.friendID);
                        name = friend.name;
                     }

                  // check if the post is an image
                  if (post.type == Constants.POST_TYPE_PHOTO)
                     {
                        String photoPath = Constants.multimediaFilePath + "/" + post.postID + ".jpg";
                        File imgFile = new File(photoPath);
                        if (imgFile.exists())
                           {
                              listViewItems.add(new PhotoPostItem(getActivity(), name, post, photoPath));
                           }
                     }
                  // check if the post is a link or status
                  else if (post.type == Constants.POST_TYPE_STATUS || post.type == Constants.POST_TYPE_LINK)
                     {
                        listViewItems.add(new StatusPostItem(getActivity(), name, post));
                     }
                  // check if the post is a video
                  else if (post.type == Constants.POST_TYPE_VIDEO)
                     {
                        listViewItems.add(new VideoPostItem(getActivity(), name, post, Constants.multimediaFilePath));
                     }
               }
            sortWallPostList();
         }


      @Override
      public void onRefresh()
         {
            new Handler().post(new Runnable()
               {
                  @Override
                  public void run()
                     {
                        swipeLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "Refreshing...", Toast.LENGTH_SHORT).show();
                        new GetFriendContentAsyncTask(activity).execute();

                     }
               });
         }

      public void sortWallPostList()
         {
            Comparator<ListViewPostItem> postDateComparator = new Comparator<ListViewPostItem>()
               {
                  public int compare(ListViewPostItem emp1, ListViewPostItem emp2)
                     {
                        return (emp1.getDate().compareTo(emp2.getDate()) * -1);
                     }
               };

            Collections.sort(listViewItems, postDateComparator);
         }

      public void updateWallPosts()
         {
            createWallPostsList();

            if (wallPostData.size() > 0)
               {
                  noWallPostsText.setVisibility(View.GONE);
               }
            else
               {
                  noWallPostsText.setVisibility(View.VISIBLE);
               }

            // notify the adapter about the data change
            adapter.notifyDataSetChanged();
         }


   }
