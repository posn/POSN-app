package com.posn.main.wall.posts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Post;
import com.posn.main.wall.WallArrayAdapter.PostType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class StatusPostItem implements ListViewPostItem
   {

      private Post postData;
      private String friendName;
      private View.OnClickListener buttonListener;


      static class ViewHolderItem
         {
            TextView nameText;
            TextView dateText;
            TextView statusText;

            RelativeLayout commentButton;
            RelativeLayout shareButton;
         }


      public StatusPostItem(View.OnClickListener buttonListener, String friendName, Post postData)
         {
            this.postData = postData;
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
            ViewHolderItem viewHolder;

            if (convertView == null)
               {
                  // inflate the layout
                  convertView = inflater.inflate(R.layout.listview_wall_status_item, parent, false);

                  // well set up the ViewHolder
                  viewHolder = new ViewHolderItem();
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
                  viewHolder = (ViewHolderItem) convertView.getTag();
               }

            // set the data into the views
            viewHolder.nameText.setText(friendName);
            viewHolder.dateText.setText(postData.date);
            viewHolder.statusText.setText(postData.textContent);

            viewHolder.commentButton.setTag(postData);
            viewHolder.shareButton.setTag(postData);


            return convertView;
         }


      @Override
      public Date getDate()
         {
            try
               {
                  SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd 'at' h:mmaa", Locale.US);
                  return dateformat.parse(postData.date);
               }
            catch (ParseException e)
               {
                  e.printStackTrace();
               }
            return null;
         }

   }
