package com.posn.main.main.groups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.posn.main.main.groups.listview_items.ListViewManageGroupItem;

import java.util.ArrayList;

/**
 * This adapter class handles the listview items in the Friend Fragment
 **/
public class ManageGroupArrayAdapter extends ArrayAdapter<ListViewManageGroupItem>
   {

      private LayoutInflater mInflater;


      public enum ManageGroupRowType
         {
            FRIEND_ITEM, HEADER_ITEM, NO_FRIEND_ITEM
         }


      public ManageGroupArrayAdapter(Context context, ArrayList<ListViewManageGroupItem> values)
         {
            super(context, 0, values);
            mInflater = LayoutInflater.from(context);
         }


      @Override
      public int getViewTypeCount()
         {
            return ManageGroupRowType.values().length;

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
      public boolean areAllItemsEnabled()
         {
            return false;
         }

      @Override
      public boolean isEnabled(int position)
         {
            return false;
         }
   }
