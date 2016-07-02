package com.posn.main.main.wall.comments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Comment;
import com.posn.datatypes.Friend;
import com.posn.managers.UserManager;

import java.util.ArrayList;
import java.util.HashMap;


public class CommentArrayAdapter extends ArrayAdapter<Comment>
   {
      class CommentViewHolder
         {
            TextView nameText;
            TextView dateText;
            TextView commentText;
         }

      private ArrayList<Comment> values;
      private HashMap<String, Friend> friends;
      private UserManager userManager;
      LayoutInflater inflater;

      public CommentArrayAdapter(Context context, ArrayList<Comment> values, HashMap<String, Friend> friends, UserManager userManager)
         {
            super(context, R.layout.listview_comment_item, values);
            this.values = values;
            this.friends = friends;
            this.userManager = userManager;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         }


      @Override
      public View getView(final int position, View convertView, ViewGroup parent)
         {
            CommentViewHolder viewHolder;

            if (convertView != null && !(convertView.getTag() instanceof CommentViewHolder))
               {
                  convertView = null;
               }

            if (convertView == null)
               {
                  convertView = inflater.inflate(R.layout.listview_comment_item, parent, false);

                  // set up the ViewHolder
                  viewHolder = new CommentViewHolder();
                  viewHolder.nameText = (TextView) convertView.findViewById(R.id.name);
                  viewHolder.dateText = (TextView) convertView.findViewById(R.id.date);
                  viewHolder.commentText = (TextView) convertView.findViewById(R.id.comment);

                  // store the holder with the view.
                  convertView.setTag(viewHolder);
               }
            else
               {
                  viewHolder = (CommentViewHolder) convertView.getTag();
               }


            // get friend name
            String name;

            if (friends.containsKey(values.get(position).userID))
               {
                  name = friends.get(values.get(position).userID).name;
               }
            else
               {
                  name = userManager.firstName + " " + userManager.lastName;
               }

            viewHolder.nameText.setText(name);
            viewHolder.dateText.setText(values.get(position).date);
            viewHolder.commentText.setText(values.get(position).commentText);

            return convertView;
         }


      @Override
      public int getCount()
         {
            return values.size();
         }
   }
