package com.posn.main.wall.comments;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.posn.R;


class CommentViewHolder
	{
		TextView nameText;
		TextView dateText;
		TextView commentText;
		ImageView thumb_image;
	}


public class CommentArrayAdapter extends ArrayAdapter<CommentItem>
	{

		private final Context context;
		private ArrayList<CommentItem> values;
		CommentViewHolder mViewHolder = null;


		public CommentArrayAdapter(Context context, ArrayList<CommentItem> values)
			{
				super(context, R.layout.listview_comment_item, values);
				this.context = context;
				this.values = values;


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


				

				mViewHolder.nameText.setText(values.get(position).name);
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
