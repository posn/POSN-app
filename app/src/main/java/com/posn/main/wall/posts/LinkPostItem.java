package com.posn.main.wall.posts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Post;
import com.posn.main.wall.WallArrayAdapter.PostType;
import com.posn.main.wall.comments.CommentActivity;


public class LinkPostItem implements ListViewPostItem, OnClickListener
	{

		private Post postData;
		private Context context;

		public LinkPostItem(Context context, Post postData)
			{
				this.postData = postData;
			}


		@Override
		public int getViewType()
			{
				return PostType.LINK_POST_ITEM.ordinal();
			}


		@Override
		public View getView(LayoutInflater inflater, View convertView)
			{
				View view = convertView;

				if (view == null)
					{
						view = (View) inflater.inflate(R.layout.listview_friend_accepted_item, null);
					}
				
				// get comment and share buttons from the layout
				RelativeLayout commentButton = (RelativeLayout) view.findViewById(R.id.comment_button);
				RelativeLayout shareButton = (RelativeLayout) view.findViewById(R.id.share_button);

				// set listeners for the buttons
				commentButton.setOnClickListener(this);
				shareButton.setOnClickListener(this);

				TextView text1 = (TextView) view.findViewById(R.id.name);
				text1.setText(postData.content);

				return view;
			}
		@Override
		public void onClick(View v)
			{
				switch(v.getId())
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

	}
