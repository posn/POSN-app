package com.posn.main.wall;

import android.app.Activity;
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
import com.posn.asynctasks.wall.NewWallPostAsyncTask;
import com.posn.datatypes.Friend;
import com.posn.datatypes.WallPost;
import com.posn.main.AppDataManager;
import com.posn.main.MainActivity;
import com.posn.main.wall.comments.CommentActivity;
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

/**
 * This fragment class implements the functionality for the user wall fragment:
 * <ul><li>Populates the list view using the data stored in the wall post hashmap located in the data manager class in the main activity
 * <li>Allows the comments for each post to be view (launches a new activity for comments)
 * <li>Allows the user to create new status or photo wall posts</ul>
 **/

public class UserWallFragment extends Fragment implements OnClickListener, OnRefreshListener
   {

      // user interface variables
      RelativeLayout statusButton, photoButton;
      public TextView noWallPostsText;
      ListView lv;
      TableRow statusBar;
      SwipeRefreshLayout swipeLayout;

      public ArrayList<ListViewPostItem> listViewItems = new ArrayList<>();
      HashMap<String, WallPost> wallPostData;
      public WallArrayAdapter adapter;

      public MainActivity main;
      public AppDataManager dataManager;

      public int newWallPostsNum = 0;
      private int fragNum;

      /**
       * This method is called when the activity needs to be created and fetches and implements the user interface elements
       * Sets up the list view adapter to display the different wall posts (each wall post is a derived class based on the type)
       **/
      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            // load the wall fragment layout
            View view = inflater.inflate(R.layout.fragment_user_wall, container, false);

            // get the main activity
            main = (MainActivity) getActivity();
            dataManager = main.dataManager;

            // get the listview from the layout
            lv = (ListView) view.findViewById(R.id.listView1);
            noWallPostsText = (TextView) view.findViewById(R.id.notification_text);

            // get the buttons from the layout
            statusButton = (RelativeLayout) view.findViewById(R.id.status_button);
            photoButton = (RelativeLayout) view.findViewById(R.id.photo_button);

            // set an onclick listener for each button
            statusButton.setOnClickListener(this);
            photoButton.setOnClickListener(this);

            // get the bottom status bar  from the layout
            statusBar = (TableRow) view.findViewById(R.id.status_bar);

            swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
            swipeLayout.setOnRefreshListener(this);


            // create an on scroll listener to refresh/fetch new data went the listview is pulled down
            lv.setOnScrollListener(new OnScrollListener()
               {
                  @Override
                  public void onScrollStateChanged(AbsListView view, int scrollState)
                     {
                     }

                  @Override
                  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
                     {
                        int topRowVerticalPosition = (lv == null || lv.getChildCount() == 0) ? 0 : lv.getChildAt(0).getTop();
                        swipeLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
                     }
               });

            // create a new adapter to handle the list view items
            adapter = new WallArrayAdapter(getActivity(), listViewItems);
            lv.setAdapter(adapter);

            // get the wall post data from activity
            wallPostData = dataManager.masterWallPostList.wallPosts;
            if (wallPostData.size() != 0)
               {
                  updateWallPosts();
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
       * This method is called when the user clicks the different user interface elements and implements each element's functionality
       **/
      @Override
      public void onClick(View v)
         {
            WallPost wallPost;
            Intent intent;

            switch (v.getId())
               {
                  // check if the user selected the create status post button
                  case R.id.status_button:

                     // create new activity to handle the new status post
                     intent = new Intent(main, CreateNewStatusPostActivity.class);

                     // pass the user defined groups to the activity
                     intent.putExtra("groups", dataManager.userGroupList.getUserGroupsArrayList());

                     // start the activity with a result to get the newly created post
                     startActivityForResult(intent, Constants.RESULT_CREATE_STATUS_POST);
                     break;


                  // check if the user selected the create photo post button
                  case R.id.photo_button:

                     // create new activity to handle the new photo post
                     intent = new Intent(main, CreateNewPhotoPostActivity.class);

                     // pass the user defined groups to the activity
                     intent.putExtra("groups", dataManager.userGroupList.getUserGroupsArrayList());

                     // start the activity with a result to get the newly created post
                     startActivityForResult(intent, Constants.RESULT_CREATE_PHOTO_POST);
                     break;


                  // check if the comment button for a specific post was touched
                  case R.id.comment_button:

                     // get the wall post data from the view
                     wallPost = (WallPost) v.getTag();

                     // create a new activity to handle comments
                     intent = new Intent(main, CommentActivity.class);

                     // pass the wall post, the friends list, and the user data to the activity
                     intent.putExtra("post", wallPost);
                     intent.putExtra("friends", dataManager.masterFriendList.currentFriends);
                     intent.putExtra("user", dataManager.user);

                     // start the activity
                     main.startActivity(intent);
                     break;


                  // check if the image for a specific post was touched
                  case R.id.photo:

                     // get the wall post data from the view
                     wallPost = (WallPost) v.getTag();

                     // create a new activity to view the photo
                     intent = new Intent(main, PhotoViewerActivity.class);

                     // pass the wall post to the activity
                     intent.putExtra("post", wallPost);

                     // start the activity
                     main.startActivity(intent);
                     break;


                  // check if the video for a specific post was touched
                  case R.id.video:

                     // get the wall post data from the view
                     wallPost = (WallPost) v.getTag();

                     // create a new activity to view the video
                     intent = new Intent(main, VideoPlayerActivity.class);

                     // pass the wall post to the activity
                     intent.putExtra("post", wallPost);

                     // start the activity
                     main.startActivity(intent);
                     break;
               }
         }

      /**
       * This method is called when the create status or photo post activities are finished
       * The newly created post is returned from the activity and the appropriate async task is launched
       **/
      @Override
      public void onActivityResult(int requestCode, int resultCode, Intent data)
         {
            // check if the created post was a status text
            if (requestCode == Constants.RESULT_CREATE_STATUS_POST && resultCode == Activity.RESULT_OK)
               {
                  String status = data.getStringExtra("status");
                  ArrayList<String> selectGroups = data.getStringArrayListExtra("groups");
                  new NewWallPostAsyncTask(this, selectGroups, Constants.POST_TYPE_STATUS, status).execute();
               }

            // check if the created post was a photo
            else if (requestCode == Constants.RESULT_CREATE_PHOTO_POST && resultCode == Activity.RESULT_OK)
               {
                  String photoPath = data.getStringExtra("photopath");
                  ArrayList<String> selectGroups = data.getStringArrayListExtra("groups");
                  new NewWallPostAsyncTask(this, selectGroups, Constants.POST_TYPE_PHOTO, photoPath).execute();
               }
         }



      /**
       * This method clears and repopulates the listview using the wall post data
       **/
      public void updateWallPosts()
         {
            // clear the list view
            listViewItems.clear();

            System.out.println("CREATING WALL POSTS!!!");

            // loop through all the wall posts and add them to the listview
            WallPost wallPost;

            for (Map.Entry<String, WallPost> entry : wallPostData.entrySet())
               {
                  // get the post from the hash map
                  wallPost = entry.getValue();

                  // create the list view item
                  createWallPostListViewItem(wallPost);
               }

            // sort the listview items
            sortWallPostList();

            // show or hide the "No Wall Posts" message based on the number of data items
            noWallPostsText.setVisibility(wallPostData.size() > 0 ? View.GONE : View.VISIBLE);

            // notify the adapter about the data change
            adapter.notifyDataSetChanged();
         }

      /**
       * This method adds a new wall post to the list view
       **/
      public void addNewWallPost(WallPost post)
         {
            // create the list view item
            createWallPostListViewItem(post);

            // hide the No Wall Posts" message
            noWallPostsText.setVisibility(View.GONE);

            // sort the listview items
            sortWallPostList();

            // notify the adapter that the data changed
            adapter.notifyDataSetChanged();
         }

      /**
       * This method creates a new listview item depending on the post type and adds it to the list view
       **/
      private void createWallPostListViewItem(WallPost post)
         {
            String name;

            // get the name of the person who created the post
            if (post.friendID.equals(dataManager.user.ID))
               {
                  name = dataManager.user.firstName + " " + dataManager.user.lastName;
               }
            else
               {
                  Friend friend = dataManager.masterFriendList.currentFriends.get(post.friendID);
                  name = friend.name;
               }

            // check if the post is an image
            if (post.type == Constants.POST_TYPE_PHOTO)
               {
                  String photoPath = Constants.multimediaFilePath + "/" + post.postID + ".jpg";
                  File imgFile = new File(photoPath);
                  if (imgFile.exists())
                     {
                        listViewItems.add(new PhotoPostItem(this, name, post));
                     }
               }
            // check if the post is a link or status
            else if (post.type == Constants.POST_TYPE_STATUS || post.type == Constants.POST_TYPE_LINK)
               {
                  listViewItems.add(new StatusPostItem(this, name, post));
               }
            // check if the post is a video
            else if (post.type == Constants.POST_TYPE_VIDEO)
               {
                  listViewItems.add(new VideoPostItem(this, name, post));
               }
         }

      /**
       * This method is called when the user pulls down on the listview and the listview is at the first item
       * An async task is launched to fetch any new wall posts from his/her friends
       **/
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
                        new GetFriendContentAsyncTask(main).execute();
                     }
               });
         }

      /**
       * This method sorts the wall posts in the listview based on the date (sorts from most recent to oldest)
       **/
      private void sortWallPostList()
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
   }
