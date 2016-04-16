package com.posn.main.wall.posts;

import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Post;
import com.posn.main.wall.VideoPlayerActivity;
import com.posn.main.wall.WallArrayAdapter.PostType;
import com.posn.main.wall.comments.CommentActivity;

import java.io.File;
import java.io.IOException;


public class VideoPostItem implements ListViewPostItem, SurfaceTextureListener, OnClickListener
   {
      static class ViewHolderItem
         {
            TextView nameText;
            TextView dateText;
            TextureView textureView;

            RelativeLayout commentButton;
            RelativeLayout shareButton;
         }

      private Context context;
      private Post postData;
      private Uri dataLink;
      private String friendName;

      ViewHolderItem viewHolder;



      public VideoPostItem(Context context, String friendName, Post postData, String directory)
         {
            this.context = context;
            this.postData = postData;
            this.dataLink = Uri.fromFile(new File(directory + "/" + postData.postID));
            this.friendName = friendName;
         }


      @Override
      public int getViewType()
         {
            return PostType.VIDEO_POST_ITEM.ordinal();
         }


      @Override
      public View getView(LayoutInflater inflater, View convertView)
         {
            if (convertView == null)
               {
                  // inflate the layout
                  convertView = inflater.inflate(R.layout.listview_wall_video_item, null);

                  // well set up the ViewHolder
                  viewHolder = new ViewHolderItem();
                  viewHolder.nameText = (TextView) convertView.findViewById(R.id.name);
                  viewHolder.dateText = (TextView) convertView.findViewById(R.id.date);
                  viewHolder.textureView = (TextureView) convertView.findViewById(R.id.video);

                  viewHolder.commentButton = (RelativeLayout) convertView.findViewById(R.id.comment_button);
                  viewHolder.shareButton = (RelativeLayout) convertView.findViewById(R.id.share_button);

                  viewHolder.commentButton.setOnClickListener(this);
                  viewHolder.shareButton.setOnClickListener(this);

                  viewHolder.textureView.setSurfaceTextureListener(this);
                  viewHolder.textureView.setOnClickListener(this);

                  // store the holder with the view.
                  convertView.setTag(viewHolder);
               }
            else
               {
                  viewHolder = (ViewHolderItem) convertView.getTag();
               }

            // set the data into the views
            viewHolder.nameText.setText(friendName);
            viewHolder.dateText.setText(postData.date);



            return convertView;
         }


      public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
         {
            Surface s = new Surface(surface);

            try
               {
                  MediaPlayer mMediaPlayer = new MediaPlayer();
                  mMediaPlayer.setDataSource(context, dataLink);
                  mMediaPlayer.setSurface(s);
                  mMediaPlayer.prepare();
                  mMediaPlayer.setVolume(0, 0);
                  mMediaPlayer.start();
               }
            catch (SecurityException | IllegalStateException | IllegalArgumentException | IOException e)
               {
                  e.printStackTrace();
               }
         }


      @Override
      public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height)
         {
         }


      @Override
      public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
         {
            return false;
         }


      @Override
      public void onSurfaceTextureUpdated(SurfaceTexture surface)
         {
         }

      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.comment_button:

                     // launch comment activity
                     Intent intent = new Intent(context, CommentActivity.class);
                     context.startActivity(intent);

                     break;

                  case R.id.share_button:
                     break;

                  case R.id.video:

                     intent = new Intent(context, VideoPlayerActivity.class);
                     intent.setData(dataLink);

                     context.startActivity(intent);

                     System.out.println("clicked!");

                     break;
               }
         }
   }
