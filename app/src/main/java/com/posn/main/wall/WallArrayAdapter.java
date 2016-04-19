package com.posn.main.wall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.posn.main.wall.posts.ListViewPostItem;

import java.util.ArrayList;


public class WallArrayAdapter extends ArrayAdapter<ListViewPostItem>
   {

      private LayoutInflater mInflater;
      private ArrayList<ListViewPostItem> values;


      public enum PostType
         {
            LINK_POST_ITEM, PHOTO_POST_ITEM, STATUS_POST_ITEM, VIDEO_POST_ITEM
         }


      public WallArrayAdapter(Context context, ArrayList<ListViewPostItem> values)
         {
            super(context, 0, values);
            mInflater = LayoutInflater.from(context);

            this.values = values;
            System.out.println(values.size());
            System.out.println(this.values.size());

         }


      @Override
      public int getViewTypeCount()
         {
            return PostType.values().length;

         }


      @Override
      public int getItemViewType(int position)
         {
            return getItem(position).getViewType();
         }


      @Override
      public View getView(int position, View convertView, ViewGroup parent)
         {
            return getItem(position).getView(mInflater, convertView, parent);
         }


      @Override
      public boolean isEnabled(int position)
         {
            return false;
         }
   }
