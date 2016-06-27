package com.posn.main.wall.posts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.posn.R;
import com.posn.asynctasks.wall.LoadImageAsyncTask;
import com.posn.datatypes.WallPost;
import com.posn.main.wall.WallArrayAdapter.PostType;
import com.posn.main.wall.views.SquareImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * This class creates a photo wall post listview item for the wall post listview.
 * Implements the functions defined in the ListViewPostItem interface.
 * Uses a viewholder pattern: https://developer.android.com/training/improving-layouts/smooth-scrolling.html
 **/
public class PhotoPostItem implements ListViewPostItem
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

      private WallPost wallPost;
      ViewHolderItem viewHolder;
      String friendName;
      private View.OnClickListener onClickListener;


      public PhotoPostItem(View.OnClickListener onClickListener, String name, WallPost wallPostData)
         {
            this.wallPost = wallPostData;
            this.friendName = name;
            this.onClickListener = onClickListener;
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

                  viewHolder.commentButton.setOnClickListener(onClickListener);
                  viewHolder.shareButton.setOnClickListener(onClickListener);
                  viewHolder.photoImage.setOnClickListener(onClickListener);

                  // store the holder with the view.
                  convertView.setTag(viewHolder);
               }
            else
               {
                  viewHolder = (ViewHolderItem) convertView.getTag();
               }

            // set the data into the views
            viewHolder.nameText.setText(friendName);
            viewHolder.dateText.setText(wallPost.date);

            viewHolder.photoImage.setTag(wallPost);
            viewHolder.commentButton.setTag(wallPost);
            viewHolder.shareButton.setTag(wallPost);

            new LoadImageAsyncTask(viewHolder.photoImage, viewHolder.loadingCircle).execute();
            viewHolder.photoImage.setImageResource(android.R.color.transparent);
            viewHolder.loadingCircle.setVisibility(View.VISIBLE);
            return convertView;
         }


      @Override
      public Date getDate()
         {
            try
               {
                  SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd 'at' h:mmaa", Locale.US);
                  return dateformat.parse(wallPost.date);
               }
            catch (ParseException e)
               {
                  e.printStackTrace();
               }
            return null;
         }



   }
