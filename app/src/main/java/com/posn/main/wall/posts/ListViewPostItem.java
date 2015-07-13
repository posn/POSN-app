package com.posn.main.wall.posts;

import android.view.LayoutInflater;
import android.view.View;


public interface ListViewPostItem
	{

		public int getViewType();


		public View getView(LayoutInflater inflater, View convertView);
	}
