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
import com.posn.datatypes.WallPost;
import com.posn.main.wall.WallArrayAdapter.PostType;
import com.posn.main.wall.comments.CommentActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * This class creates a link wall post listview item for the wall post listview.
 * Implements the functions defined in the ListViewPostItem interface.
 * Uses a viewholder pattern: https://developer.android.com/training/improving-layouts/smooth-scrolling.html
 *
 * Note: Link posts are not currently implemented
 **/
public class LinkPostItem implements ListViewPostItem, OnClickListener
   {
      private Context context;
      private WallPost wallPostData;
      private String friendName;

      static class ViewHolderItem
         {
            TextView nameText;
            TextView dateText;
            TextView linkText;

            RelativeLayout commentButton;
            RelativeLayout shareButton;
         }

      public LinkPostItem(Context context, String friendName, WallPost wallPostData)
         {
            this.context = context;
            this.wallPostData = wallPostData;
            this.friendName = friendName;
         }

      @Override
      public int getViewType()
         {
            return PostType.LINK_POST_ITEM.ordinal();
         }


      @Override
      public View getView(LayoutInflater inflater, View convertView, ViewGroup parent)
         {
            ViewHolderItem viewHolder;

            if (convertView == null)
               {
                  // inflate the layout
                  convertView = inflater.inflate(R.layout.listview_wall_status_item, parent, false);

                  // well set up the ViewHolder
                  viewHolder = new ViewHolderItem();
                  viewHolder.nameText = (TextView) convertView.findViewById(R.id.name);
                  viewHolder.dateText = (TextView) convertView.findViewById(R.id.date);
                  viewHolder.linkText = (TextView) convertView.findViewById(R.id.status);

                  viewHolder.commentButton = (RelativeLayout) convertView.findViewById(R.id.comment_button);
                  viewHolder.shareButton = (RelativeLayout) convertView.findViewById(R.id.share_button);

                  viewHolder.commentButton.setOnClickListener(this);
                  viewHolder.shareButton.setOnClickListener(this);

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
            viewHolder.linkText.setText(wallPostData.textContent);

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
               }
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
