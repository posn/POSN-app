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

import com.posn.R;
import com.posn.application.POSNApplication;
import com.posn.datatypes.Friend;
import com.posn.datatypes.Post;
import com.posn.main.MainActivity;
import com.posn.main.wall.posts.LinkPostItem;
import com.posn.main.wall.posts.ListViewPostItem;
import com.posn.main.wall.posts.PhotoPostItem;
import com.posn.main.wall.posts.StatusPostItem;
import com.posn.main.wall.posts.VideoPostItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class UserWallFragment extends Fragment implements OnClickListener, OnRefreshListener
   {
      int STATUS_RESULT = 1;

      int TYPE_STATUS = 0;
      int TYPE_LINK = 1;
      int TYPE_PHOTO = 2;
      int TYPE_VIDEO = 3;


      // declare variables
      Context context;
      RelativeLayout statusButton, photoButton, checkInButton;
      ListView lv;
      TableRow statusBar;

      ArrayList<ListViewPostItem> wallPostList = new ArrayList<>();
      ArrayList<Post> wallPostData;
      SwipeRefreshLayout swipeLayout;
      WallArrayAdapter adapter;

      POSNApplication app;
      MainActivity activity;


      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            // load the system tab layout
            View view = inflater.inflate(R.layout.fragment_user_wall, container, false);
            context = getActivity();

            // get the application
            app = (POSNApplication) getActivity().getApplication();

            // get the main activity
            activity = (MainActivity) getActivity();

            // get the wall post data from activity
            wallPostData = activity.wallPostData;

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

            // fill with fake data
            // getNameEmailDetails();
            //createWallPosts();
            // saveWallPosts();
            // getWallPosts();


            adapter = new WallArrayAdapter(getActivity(), wallPostList);
            lv.setAdapter(adapter);

            return view;
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

                  case R.id.status_button:
                     Intent intent = new Intent(context, PostStatusActivity.class);
                     startActivityForResult(intent, STATUS_RESULT);

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
            if (requestCode == STATUS_RESULT && resultCode == Activity.RESULT_OK)
               {
                  Post post = new Post(TYPE_STATUS, app.getId(), "Jan 19, 2015 at 1:45 pm", data.getStringExtra("status"));
                  wallPostList.add(0, new StatusPostItem(getActivity(), app.getFirstName() + " " + app.getLastName(), post));
                  wallPostData.add(post);
                  adapter.notifyDataSetChanged();
                  activity.saveWallPosts();
               }
         }

      public void createWallPostsList()
         {
            wallPostList.clear();
            System.out.println("CREATING WALL POSTS!!!");
            String name;

            for (int n = 0; n < wallPostData.size(); n++)
               {
                  Post post = wallPostData.get(n);

                  //
                  if (post.friend.equals(app.getId()))
                     {
                        name = app.getFirstName() + " " + app.getLastName();
                     }
                  else
                     {
                        System.out.println(activity.masterFriendList.size() + " Post:" + post.content);
                        Friend friend = activity.masterFriendList.get(post.friend);
                        name = friend.name;
                     }

                  if (post.type == TYPE_PHOTO)
                     {
                        String photoPath = app.multimediaFilePath + "/" + post.content;
                        File imgFile = new File(photoPath);
                        if (imgFile.exists())
                           {
                              wallPostList.add(new PhotoPostItem(getActivity(), name, post, app.multimediaFilePath));
                           }
                     }
                  else if (post.type == TYPE_STATUS)
                     {
                        wallPostList.add(new StatusPostItem(getActivity(), name, post));
                     }
                     else if(post.type == TYPE_LINK)
                     {
                        wallPostList.add(new LinkPostItem(getActivity(), name, post));
                     }
                  else if (post.type == TYPE_VIDEO)
                     {
                        wallPostList.add(new VideoPostItem(getActivity(), name, post, app.multimediaFilePath));
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
                  }
            }, 2000);
         }


      public void createWallPosts()
         {
            JSONArray wallPosts = new JSONArray();

            try
               {
                  Post post = new Post(TYPE_STATUS, "ec3591b0907170cc48c6759c013333f712141eb8", "Jan 19, 2015 at 1:45 pm", "This is a test post from a file.");
                  wallPosts.put(post.createJOSNObject());

                  post = new Post(TYPE_PHOTO, "726e60c84e88dd01b49ecf6f0de42843383bffad", "Jan 19, 2015 at 1:45 pm", "test.jpg");
                  wallPosts.put(post.createJOSNObject());

                  post = new Post(TYPE_STATUS, "eac054c17d7b49456f224788a12adf4eba4c0f9d", "Jan 19, 2015 at 1:45 pm", "Happy Birthday");
                  wallPosts.put(post.createJOSNObject());

                  post = new Post(TYPE_VIDEO, "dc66ae1b5fa5c84cf12b82e2ec07f6b91233e8d4", "Jan 19, 2015 at 1:45 pm", "test.mp4");
                  wallPosts.put(post.createJOSNObject());

                  post = new Post(TYPE_STATUS, "413e990ba1e5984d8fd41f1a1acaf3d154b21cab", "Jan 19, 2015 at 1:45 pm", "Test! TeSt!! TEST!!!");
                  wallPosts.put(post.createJOSNObject());

                  post = new Post(TYPE_PHOTO, "f9febf09f9d7632a7611598bc03baed8d5c7357d", "Jan 19, 2015 at 1:45 pm", "test.jpg");
                  wallPosts.put(post.createJOSNObject());

                  post = new Post(TYPE_STATUS, "eac054c17d7b49456f224788a12adf4eba4c0f9d", "Jan 19, 2015 at 1:45 pm", "Happy Birthday!");
                  wallPosts.put(post.createJOSNObject());

                  post = new Post(TYPE_STATUS, "eac054c17d7b49456f224788a12adf4eba4c0f9d", "Jan 19, 2015 at 1:45 pm", "Happy Birthday!!");
                  wallPosts.put(post.createJOSNObject());

                  post = new Post(TYPE_PHOTO, "177ab489aa8cb82323ed02c2adb051c49c0c847d", "Jan 19, 2015 at 1:45 pm", "test.jpg");
                  wallPosts.put(post.createJOSNObject());

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
               }
         }


      public void updateWallPosts()
         {
            createWallPostsList();

            // notify the adapter about the data change
            adapter.notifyDataSetChanged();
         }


   }
