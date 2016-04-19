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
import com.posn.datatypes.UserGroup;

import java.util.ArrayList;


class ViewHolder
   {

      TextView nameText;
      CheckBox checkBox;
      boolean checked = false;
   }


public class SelectGroupArrayAdapter extends ArrayAdapter<UserGroup>
   {

      private final Context context;
      ArrayList<UserGroup> userGroupList;


      ArrayList<String> selectedGroupIDs;



      public SelectGroupArrayAdapter(Context context, ArrayList<UserGroup> userGroups, ArrayList<String> selectedGroupIDs)
         {
            super(context, R.layout.listview_group_item, userGroups);
            this.context = context;
            this.userGroupList = userGroups;
            this.selectedGroupIDs = selectedGroupIDs;
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
                  holder.checkBox.setChecked(userGroupList.get(position).selected);
                  convertView.setTag(holder);

                  holder.checkBox.setOnClickListener(new View.OnClickListener()
                     {
                        public void onClick(View v)
                           {
                              updateSelectedGroupList(userGroupList.get(position));
                           }
                     });
               }
            else
               {
                  holder = (ViewHolder) convertView.getTag();
               }

            UserGroup userGroup = userGroupList.get(position);
            holder.nameText.setText(userGroup.name);
            holder.checkBox.setChecked(userGroup.selected);
            holder.nameText.setTag(userGroup);


            return convertView;
         }


      @Override
      public int getCount()
         {
            return userGroupList.size();
         }


      public void updateSelectedGroupList(UserGroup item)
         {

            if (item.selected)
               {
                  item.selected = false;
                  System.out.println("NOT CHECKED");
                  selectedGroupIDs.remove(item.ID);
               }
            else
               {
                  item.selected = true;
                  System.out.println("CHECKED");
                  selectedGroupIDs.add(item.ID);
               }
         }

   }
