package com.posn.main.main.wall.listview_items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.WallPost;
import com.posn.main.main.wall.WallArrayAdapter.PostType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class creates a status wall post listview item for the wall post listview.
 * Implements the functions defined in the ListViewPostItem interface.
 * Uses a viewholder pattern: https://developer.android.com/training/improving-layouts/smooth-scrolling.html
 **/
public class StatusPostItem implements ListViewPostItem
   {
      static class StatusPostViewHolder
         {
            TextView nameText;
            TextView dateText;
            TextView statusText;

            RelativeLayout commentButton;
            RelativeLayout shareButton;
         }

      // listview item data
      private WallPost wallPostData;
      private String friendName;
      private View.OnClickListener buttonListener;


      public StatusPostItem(View.OnClickListener buttonListener, String friendName, WallPost wallPostData)
         {
            this.wallPostData = wallPostData;
            this.friendName = friendName;
            this.buttonListener = buttonListener;
         }


      @Override
      public int getViewType()
         {
            return PostType.STATUS_POST_ITEM.ordinal();
         }


      @Override
      public View getView(LayoutInflater inflater, View convertView, ViewGroup parent)
         {
            //  View view = convertView;
            StatusPostViewHolder viewHolder;

            if (convertView != null && !(convertView.getTag() instanceof StatusPostViewHolder))
               {
                  convertView = null;
               }

            if (convertView == null)
               {
                  // inflate the layout
                  convertView = inflater.inflate(R.layout.listview_wall_status_item, parent, false);

                  // set up the ViewHolder
                  viewHolder = new StatusPostViewHolder();
                  viewHolder.nameText = (TextView) convertView.findViewById(R.id.name);
                  viewHolder.dateText = (TextView) convertView.findViewById(R.id.date);
                  viewHolder.statusText = (TextView) convertView.findViewById(R.id.status);

                  viewHolder.commentButton = (RelativeLayout) convertView.findViewById(R.id.comment_button);
                  viewHolder.shareButton = (RelativeLayout) convertView.findViewById(R.id.share_button);

                  viewHolder.commentButton.setOnClickListener(buttonListener);
                  viewHolder.shareButton.setOnClickListener(buttonListener);

                  // store the holder with the view.
                  convertView.setTag(viewHolder);
               }
            else
               {
                  viewHolder = (StatusPostViewHolder) convertView.getTag();
               }

            // set the data into the views
            viewHolder.nameText.setText(friendName);
            viewHolder.dateText.setText(wallPostData.date);
            viewHolder.statusText.setText(wallPostData.textContent);

            viewHolder.commentButton.setTag(wallPostData);
            viewHolder.shareButton.setTag(wallPostData);


            return convertView;
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
