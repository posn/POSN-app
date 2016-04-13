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
import com.posn.datatypes.RequestedFriend;

import java.util.ArrayList;


class ViewHolder
   {

      TextView nameText;
      CheckBox checkBox;
      boolean checked = false;
   }


public class FriendGroupArrayAdapter extends ArrayAdapter<Group>
   {

      private final Context context;
      ArrayList<Group> groupList;


      RequestedFriend requestedFriend;



      public FriendGroupArrayAdapter(Context context, ArrayList<Group> groups, RequestedFriend requestedFriend)
         {
            super(context, R.layout.listview_group_item, groups);
            this.context = context;
            this.groupList = groups;
            this.requestedFriend = requestedFriend;
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
                  requestedFriend.groups.remove(item.ID);
               }
            else
               {
                  item.selected = true;
                  System.out.println("CHECKED");
                  requestedFriend.groups.add(item.ID);
               }
         }

   }
