package com.posn.main.wall.posts;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.posn.Constants;
import com.posn.R;
import com.posn.datatypes.WallPost;
import com.posn.main.wall.WallArrayAdapter.PostType;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class VideoPostItem implements ListViewPostItem, SurfaceTextureListener
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
      private WallPost wallPostData;
      private String friendName;

      ViewHolderItem viewHolder;
      View.OnClickListener onClickListener;


      public VideoPostItem(View.OnClickListener onClickListener, String friendName, WallPost wallPostData)
         {
            this.wallPostData = wallPostData;
            this.friendName = friendName;
            this.onClickListener = onClickListener;
         }


      @Override
      public int getViewType()
         {
            return PostType.VIDEO_POST_ITEM.ordinal();
         }


      @Override
      public View getView(LayoutInflater inflater, View convertView, ViewGroup parent)
         {
            if (convertView == null)
               {
                  // inflate the layout
                  convertView = inflater.inflate(R.layout.listview_wall_video_item, parent, false);

                  // well set up the ViewHolder
                  viewHolder = new ViewHolderItem();
                  viewHolder.nameText = (TextView) convertView.findViewById(R.id.name);
                  viewHolder.dateText = (TextView) convertView.findViewById(R.id.date);
                  viewHolder.textureView = (TextureView) convertView.findViewById(R.id.video);

                  viewHolder.commentButton = (RelativeLayout) convertView.findViewById(R.id.comment_button);
                  viewHolder.shareButton = (RelativeLayout) convertView.findViewById(R.id.share_button);

                  viewHolder.commentButton.setOnClickListener(onClickListener);
                  viewHolder.shareButton.setOnClickListener(onClickListener);

                  viewHolder.textureView.setSurfaceTextureListener(this);
                  viewHolder.textureView.setOnClickListener(onClickListener);

                  // store the holder with the view.
                  convertView.setTag(viewHolder);
               }
            else
               {
                  viewHolder = (ViewHolderItem) convertView.getTag();
               }

            // set the data into the views
            viewHolder.nameText.setText(friendName);
            viewHolder.dateText.setText(wallPostData.date);


            viewHolder.textureView.setTag(wallPostData);
            viewHolder.commentButton.setTag(wallPostData);
            viewHolder.shareButton.setTag(wallPostData);


            return convertView;
         }


      public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
         {
            Surface s = new Surface(surface);

            try
               {
                  MediaPlayer mMediaPlayer = new MediaPlayer();
                  mMediaPlayer.setDataSource(Constants.multimediaFilePath + "/" + wallPostData.postID + ".mp4");
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
      public Date getDate()
         {
            try
               {
                  SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd 'at' h:mmaa", Locale.US);
                  return dateformat.parse(wallPostData.date);
               }
            catch (ParseException e)
               {
                  e.printStackTrace();
               }
            return null;
         }
   }
