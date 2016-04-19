package com.posn.main.wall.posts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.posn.R;
import com.posn.asynctasks.LoadImageAsyncTask;
import com.posn.datatypes.Post;
import com.posn.main.wall.PhotoViewerActivity;
import com.posn.main.wall.WallArrayAdapter.PostType;
import com.posn.main.wall.comments.CommentActivity;
import com.posn.main.wall.views.SquareImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class PhotoPostItem implements ListViewPostItem, OnClickListener
   {
      static class ViewHolderItem
         {
            TextView nameText;
            TextView dateText;
            SquareImageView photoImage;

            RelativeLayout commentButton;
            RelativeLayout shareButton;
            RelativeLayout loadingCircle;
         }

      private Context context;
      private Post post;
      private String devicePath;
      ViewHolderItem viewHolder;
      String friendName;

      public PhotoPostItem(Context context, String name, Post postData, String devicePath)
         {
            this.context = context;
            this.post = postData;
            this.devicePath = devicePath;
            this.friendName = name;
         }


      @Override
      public int getViewType()
         {
            return PostType.PHOTO_POST_ITEM.ordinal();
         }


      @Override
      public View getView(LayoutInflater inflater, View convertView, ViewGroup parent)
         {
            if (convertView == null)
               {
                  // inflate the layout
                  convertView = inflater.inflate(R.layout.listview_wall_photo_item, parent, false);

                  // well set up the ViewHolder
                  viewHolder = new ViewHolderItem();
                  viewHolder.nameText = (TextView) convertView.findViewById(R.id.name);
                  viewHolder.dateText = (TextView) convertView.findViewById(R.id.date);
                  viewHolder.photoImage = (SquareImageView) convertView.findViewById(R.id.photo);

                  viewHolder.loadingCircle = (RelativeLayout) convertView.findViewById(R.id.loadingPanel);
                  viewHolder.commentButton = (RelativeLayout) convertView.findViewById(R.id.comment_button);
                  viewHolder.shareButton = (RelativeLayout) convertView.findViewById(R.id.share_button);

                  viewHolder.commentButton.setOnClickListener(this);
                  viewHolder.shareButton.setOnClickListener(this);
                  viewHolder.photoImage.setOnClickListener(this);

                  // store the holder with the view.
                  convertView.setTag(viewHolder);
               }
            else
               {
                  viewHolder = (ViewHolderItem) convertView.getTag();
               }

            // set the data into the views
            viewHolder.nameText.setText(friendName);
            viewHolder.dateText.setText(post.date);

            viewHolder.photoImage.setTag(R.id.photo_path, devicePath);
            viewHolder.photoImage.setTag(R.id.photo_key, post);
            new LoadImageAsyncTask(viewHolder.photoImage, viewHolder.loadingCircle).execute();
            viewHolder.photoImage.setImageResource(android.R.color.transparent);
            viewHolder.loadingCircle.setVisibility(View.VISIBLE);
            return convertView;
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

                  case R.id.photo:

                     intent = new Intent(context, PhotoViewerActivity.class);
                     intent.putExtra("photoPath", viewHolder.photoImage.getTag(R.id.photo_path).toString());
                     intent.putExtra("post", (Post)viewHolder.photoImage.getTag(R.id.photo_key));

                     context.startActivity(intent);
                     break;
               }
         }

      @Override
      public Date getDate()
         {
            try
               {
                  SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd 'at' h:mmaa", Locale.US);
                  return dateformat.parse(post.date);
               }
            catch (ParseException e)
               {
                  e.printStackTrace();
               }
            return null;
         }



   }
