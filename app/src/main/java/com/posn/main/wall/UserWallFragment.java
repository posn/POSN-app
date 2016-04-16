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
import android.widget.Toast;

import com.posn.Constants;
import com.posn.R;
import com.posn.asynctasks.wall.NewWallStatusPostAsyncTask;
import com.posn.datatypes.Friend;
import com.posn.datatypes.Post;
import com.posn.main.MainActivity;
import com.posn.main.wall.posts.LinkPostItem;
import com.posn.main.wall.posts.ListViewPostItem;
import com.posn.main.wall.posts.PhotoPostItem;
import com.posn.main.wall.posts.StatusPostItem;
import com.posn.main.wall.posts.VideoPostItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UserWallFragment extends Fragment implements OnClickListener, OnRefreshListener
   {
      // declare variables
      Context context;
      RelativeLayout statusButton, photoButton, checkInButton;
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

            // get the wall post data from activity
            wallPostData = activity.masterWallPostList.wallPosts;

            // get the listview from the layout
            lv = (ListView) view.findViewById(R.id.listView1);

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

            return view;
         }

      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {

                  case R.id.status_button:
                     Intent intent = new Intent(context, CreateNewStatusPostActivity.class);
                     intent.putExtra("groups", activity.userGroupList.getList());
                     startActivityForResult(intent, Constants.RESULT_CREATE_STATUS_POST);

                     break;

                  case R.id.photo_button:

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
         }

      public void createWallPostsList()
         {
            listViewItems.clear();
            System.out.println("CREATING WALL POSTS!!!");
            String name;

            for (Map.Entry<String, Post> entry : wallPostData.entrySet())
               {
                  Post post = entry.getValue();

                  if (post.friendID.equals(activity.user.ID))
                     {
                        name = activity.user.firstName + " " + activity.user.lastName;
                     }
                  else
                     {
                        Friend friend = activity.masterFriendList.currentFriends.get(post.friendID);
                        name = friend.name;
                     }

                  if (post.type == Constants.POST_TYPE_PHOTO)
                     {
                        String photoPath = Constants.multimediaFilePath + "/" + post.postID;
                        File imgFile = new File(photoPath);
                        if (imgFile.exists())
                           {
                              listViewItems.add(new PhotoPostItem(getActivity(), name, post, Constants.multimediaFilePath));
                           }
                     }
                  else if (post.type == Constants.POST_TYPE_STATUS)
                     {
                        listViewItems.add(new StatusPostItem(getActivity(), name, post));
                     }
                  else if (post.type == Constants.POST_TYPE_LINK)
                     {
                        listViewItems.add(new LinkPostItem(getActivity(), name, post));
                     }
                  else if (post.type == Constants.POST_TYPE_VIDEO)
                     {
                        listViewItems.add(new VideoPostItem(getActivity(), name, post, Constants.multimediaFilePath));
                     }
               }

         }


      @Override
      public void onRefresh()
         {
            new Handler().postDelayed(new Runnable()
               {
                  @Override
                  public void run()
                     {
                        swipeLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "Refreshing...", Toast.LENGTH_SHORT).show();
                     }
               }, 2000);
         }


      public void createWallPosts()
         {
            /*
            JSONArray wallPosts = new JSONArray();

            try
               {

                  Post post = new Post(Constants.POST_TYPE_STATUS, "ec3591b0907170cc48c6759c013333f712141eb8", "Jan 19, 2015 at 1:45 pm", "This is a test post from a file.");
                  wallPosts.put(post.createJSONObject());

                  post = new Post(TYPE_PHOTO, "726e60c84e88dd01b49ecf6f0de42843383bffad", "Jan 19, 2015 at 1:45 pm", "test.jpg");
                  wallPosts.put(post.createJSONObject());

                  post = new Post(TYPE_STATUS, "eac054c17d7b49456f224788a12adf4eba4c0f9d", "Jan 19, 2015 at 1:45 pm", "Happy Birthday");
                  wallPosts.put(post.createJSONObject());

                  post = new Post(TYPE_VIDEO, "dc66ae1b5fa5c84cf12b82e2ec07f6b91233e8d4", "Jan 19, 2015 at 1:45 pm", "test.mp4");
                  wallPosts.put(post.createJSONObject());

                  post = new Post(TYPE_STATUS, "413e990ba1e5984d8fd41f1a1acaf3d154b21cab", "Jan 19, 2015 at 1:45 pm", "Test! TeSt!! TEST!!!");
                  wallPosts.put(post.createJSONObject());

                  post = new Post(TYPE_PHOTO, "f9febf09f9d7632a7611598bc03baed8d5c7357d", "Jan 19, 2015 at 1:45 pm", "test.jpg");
                  wallPosts.put(post.createJSONObject());

                  post = new Post(TYPE_STATUS, "eac054c17d7b49456f224788a12adf4eba4c0f9d", "Jan 19, 2015 at 1:45 pm", "Happy Birthday!");
                  wallPosts.put(post.createJSONObject());

                  post = new Post(TYPE_STATUS, "eac054c17d7b49456f224788a12adf4eba4c0f9d", "Jan 19, 2015 at 1:45 pm", "Happy Birthday!!");
                  wallPosts.put(post.createJSONObject());

                  post = new Post(TYPE_PHOTO, "177ab489aa8cb82323ed02c2adb051c49c0c847d", "Jan 19, 2015 at 1:45 pm", "test.jpg");
                  wallPosts.put(post.createJSONObject());

                  JSONObject object = new JSONObject();
                  object.put("posts", wallPosts);

                  String jsonStr = object.toString();

                  FileWriter fw = new FileWriter(app.wallFilePath + "/user_wall.txt");
                  BufferedWriter bw = new BufferedWriter(fw);
                  bw.write(jsonStr);
                  bw.close();

               }
            catch (JSONException | IOException e)
               {
                  e.printStackTrace();
               }*/
         }


      public void updateWallPosts()
         {
            createWallPostsList();

            // notify the adapter about the data change
            adapter.notifyDataSetChanged();
         }


   }
