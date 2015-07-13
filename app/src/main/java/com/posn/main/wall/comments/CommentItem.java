package com.posn.main.wall.comments;

import android.graphics.Bitmap;


public class CommentItem
	{

		public String name;
		public String date;
		public String comment;
		public Bitmap image;
		
		public CommentItem(String name, String date, String comment)
			{
				this.name = name;
				this.date = date;
				this.comment = comment;
			}

	}
