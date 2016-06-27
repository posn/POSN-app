package com.posn.main.wall.comments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Comment;
import com.posn.datatypes.Friend;
import com.posn.datatypes.User;

import java.util.ArrayList;
import java.util.HashMap;


class CommentViewHolder
	{
		TextView nameText;
		TextView dateText;
		TextView commentText;
		ImageView thumb_image;
	}


public class CommentArrayAdapter extends ArrayAdapter<Comment>
	{

		private final Context context;
		private ArrayList<Comment> values;
		private HashMap<String, Friend> friends;
		private User user;
		CommentViewHolder mViewHolder = null;


		public CommentArrayAdapter(Context context, ArrayList<Comment> values, HashMap<String, Friend> friends, User user)
			{
				super(context, R.layout.listview_comment_item, values);
				this.context = context;
				this.values = values;
				this.friends = friends;
				this.user = user;
			}


		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
			{

				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				convertView = inflater.inflate(R.layout.listview_comment_item, parent, false);

				mViewHolder = new CommentViewHolder();

				mViewHolder.nameText = (TextView) convertView.findViewById(R.id.name);
				mViewHolder.dateText = (TextView) convertView.findViewById(R.id.date);
				mViewHolder.thumb_image = (ImageView) convertView.findViewById(R.id.image);
				
				mViewHolder.commentText = (TextView) convertView.findViewById(R.id.comment);


				// get friendID name
				String name;

				if(friends.containsKey(values.get(position).userID))
					{
						name = friends.get(values.get(position).userID).name;
					}
				else
					{
						name = user.firstName + " " + user.lastName;
					}

				mViewHolder.nameText.setText(name);
				mViewHolder.dateText.setText(values.get(position).date);
				
				mViewHolder.commentText.setText(values.get(position).comment);


				return convertView;
			}


		@Override
		public int getCount()
			{
				return values.size();
			}


	

	}
