package com.posn.main.wall;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.posn.asynctasks.wall.AsyncResponseWall;
import com.posn.asynctasks.wall.LoadWallPostsAsyncTask;
import com.posn.asynctasks.wall.SaveWallPostsAsyncTask;
import com.posn.datatypes.Post;
import com.posn.main.MainActivity;
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


public class UserWallFragment extends Fragment implements OnClickListener, OnRefreshListener, AsyncResponseWall
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

      ArrayList<ListViewPostItem> wallPostList = new ArrayList<ListViewPostItem>();
      ArrayList<Post> wallPostData;
      SwipeRefreshLayout swipeLayout;
      WallArrayAdapter adapter;

      POSNApplication app;
      private ProgressDialog pDialog;

      LoadWallPostsAsyncTask asyncTask;


      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            // load the system tab layout
            View view = inflater.inflate(R.layout.fragment_user_wall, container, false);
            context = getActivity();

            // get the application
            app = (POSNApplication) getActivity().getApplication();

            // get the wall post data from activity
            wallPostData = ((MainActivity) getActivity()).wallPostData;

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
            // createWallPosts();
            // getWallPosts();


            adapter = new WallArrayAdapter(getActivity(), wallPostList);
            lv.setAdapter(adapter);

            if(wallPostData.isEmpty())
               {
                  loadWallPosts();
               }
            else
               {
                  createWallPostsList();
               }

            return view;
         }


      @Override
      public void onActivityCreated(Bundle savedInstanceState)
         {
            super.onActivityCreated(savedInstanceState);
            onResume();
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

                     // Close all views before launching Employer
                     // homePage
                     // startActivity(intent);
                     startActivityForResult(intent, STATUS_RESULT);

                     System.out.println("STATUS!");

                     break;

                  case R.id.photo_button:
                     System.out.println("PHOTO!");

                     break;

                  case R.id.checkin_button:
                     System.out.println("CHECK IN!");

                     break;

               }
         }


      @Override
      public void onActivityResult(int requestCode, int resultCode, Intent data)
         {
            if (requestCode == STATUS_RESULT && resultCode == Activity.RESULT_OK)
               {
                  Post post = new Post(TYPE_STATUS, app.getFirstName() + " " + app.getLastName(), "Jan 19, 2015 at 1:45 pm", data.getStringExtra("status"));
                  wallPostList.add(0, new StatusPostItem(getActivity(), post));
                  wallPostData.add(post);
                  adapter.notifyDataSetChanged();
                  saveWallPosts();
               }
         }

      public void createWallPostsList()
         {
            wallPostList.clear();
            System.out.println("GETTING WALL POSTS!!!");

            for (int n = 0; n < wallPostData.size(); n++)
               {
                  Post post = wallPostData.get(n);

                  if (post.type == TYPE_PHOTO)
                     {
                        String photoPath = app.multimediaFilePath + "/" + post.content;
                        File imgFile = new File(photoPath);
                        if (imgFile.exists())
                           {
                              wallPostList.add(new PhotoPostItem(getActivity(), post, app.multimediaFilePath));
                           }
                     }
                  else if (post.type == TYPE_STATUS)
                     {
                        wallPostList.add(new StatusPostItem(getActivity(), post));
                     }
                  else if (post.type == TYPE_VIDEO)
                     {
                        wallPostList.add(new VideoPostItem(getActivity(), post, app.multimediaFilePath));
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
                  Post post = new Post(TYPE_STATUS, "Allen Rice", "Jan 19, 2015 at 1:45 pm", "This is a test post from a file.");
                  wallPosts.put(post.createJOSNObject());

                  post = new Post(TYPE_PHOTO, "John Hunter", "Jan 19, 2015 at 1:45 pm", "test.jpg");
                  wallPosts.put(post.createJOSNObject());

                  post = new Post(TYPE_STATUS, "Bob Smith", "Jan 19, 2015 at 1:45 pm", "Happy Birthday");
                  wallPosts.put(post.createJOSNObject());

                  post = new Post(TYPE_VIDEO, "Daniel Chavez", "Jan 19, 2015 at 1:45 pm", "test.mp4");
                  wallPosts.put(post.createJOSNObject());

                  post = new Post(TYPE_STATUS, "Cam Rowe", "Jan 19, 2015 at 1:45 pm", "Test! TeSt!! TEST!!!");
                  wallPosts.put(post.createJOSNObject());

                  post = new Post(TYPE_PHOTO, "Rachel Klukovich", "Jan 19, 2015 at 1:45 pm", "test.jpg");
                  wallPosts.put(post.createJOSNObject());

                  post = new Post(TYPE_STATUS, "Bob Smith", "Jan 19, 2015 at 1:45 pm", "Happy Birthday!");
                  wallPosts.put(post.createJOSNObject());

                  post = new Post(TYPE_STATUS, "Bob Smith", "Jan 19, 2015 at 1:45 pm", "Happy Birthday!!");
                  wallPosts.put(post.createJOSNObject());

                  post = new Post(TYPE_PHOTO, "Eric Klukovich", "Jan 19, 2015 at 1:45 pm", "test.jpg");
                  wallPosts.put(post.createJOSNObject());

                  JSONObject object = new JSONObject();
                  object.put("posts", wallPosts);

                  String jsonStr = object.toString();

                  FileWriter fw = new FileWriter(app.wallFilePath + "/user_wall.txt");
                  BufferedWriter bw = new BufferedWriter(fw);
                  bw.write(jsonStr);
                  bw.close();

               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
            catch (IOException e)
               {
                  e.printStackTrace();
               }
         }


      public void saveWallPosts()
         {
            new SaveWallPostsAsyncTask(getActivity(), app.wallFilePath + "/user_wall.txt", wallPostData).execute();
         }

      public void loadWallPosts()
         {
            asyncTask = new LoadWallPostsAsyncTask(getActivity(), app.wallFilePath + "/user_wall.txt");
            asyncTask.delegate = this;
            asyncTask.execute();
         }

      public void loadingWallFinished(ArrayList<Post> wallData)
         {
            // add the loaded data to the array list and hashmap
            this.wallPostData.addAll(wallData);

            createWallPostsList();

            // notify the adapter about the data change
            adapter.notifyDataSetChanged();
         }


   }
