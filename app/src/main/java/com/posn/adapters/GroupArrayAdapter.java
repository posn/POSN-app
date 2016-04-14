package com.posn.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Group;

import java.util.ArrayList;


public class GroupArrayAdapter extends ArrayAdapter<Group>
   {

      private final Context context;
      ArrayList<Group> groupList;
      ArrayList<String> selectedList;





      public GroupArrayAdapter(Context context, ArrayList<Group> groups, ArrayList<String> selected)
         {
            super(context, R.layout.listview_group_item, groups);
            this.context = context;
            this.groupList = groups;
            selectedList = selected;
         }


      @Override
      public View getView(final int position, View convertView, ViewGroup parent)
         {
            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null)
               {
                  LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                  convertView = vi.inflate(R.layout.listview_group_item, null);

                  holder = new ViewHolder();
                  holder.nameText = (TextView) convertView.findViewById(R.id.name);
                  holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
                  convertView.setTag(holder);

                  holder.checkBox.setOnClickListener(new View.OnClickListener()
                     {
                        public void onClick(View v)
                           {
                              updateSelectedGroupList(groupList.get(position));
                           }
                     });
               }
            else
               {
                  holder = (ViewHolder) convertView.getTag();
               }

            Group group = groupList.get(position);
            holder.nameText.setText(group.name);
            holder.checkBox.setChecked(group.selected);
            holder.nameText.setTag(group);


            return convertView;
         }


      @Override
      public int getCount()
         {
            return groupList.size();
         }


      public void updateSelectedGroupList(Group item)
         {
            if (item.selected)
               {
                  item.selected = false;
                  System.out.println("NOT CHECKED");
                  selectedList.remove(item.name);
               }
            else
               {
                  item.selected = true;
                  System.out.println("CHECKED");
                  selectedList.add(item.name);
               }
         }

   }
