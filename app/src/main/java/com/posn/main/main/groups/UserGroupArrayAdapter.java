package com.posn.main.main.groups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.UserGroup;
import com.posn.utility.LetterTileProvider;

import java.util.ArrayList;

/**
 * This adapter class handles the listview items in the User Group Fragment
 **/
public class UserGroupArrayAdapter extends ArrayAdapter<UserGroup>
   {
      class UserGroupViewHolder
         {
            TextView groupNameText;
            ImageView letterTile;
            TextView numMembersText;
         }

      private LayoutInflater inflater;

      private ArrayList<UserGroup> groupList;

      private LetterTileProvider tileProvider;

      private int tileSize;

      public UserGroupArrayAdapter(Context context, ArrayList<UserGroup> groupList)
         {
            super(context, R.layout.listview_user_group_item, groupList);
            inflater = LayoutInflater.from(context);

            this.groupList = groupList;

            tileProvider = new LetterTileProvider(context);
            tileSize = context.getResources().getDimensionPixelSize(R.dimen.letter_tile_size);

         }


      @Override
      public int getCount()
         {
            return groupList.size();
         }

      @Override
      public View getView(int position, View convertView, ViewGroup parent)
         {
            UserGroupViewHolder viewHolder;

            if (convertView != null && !(convertView.getTag() instanceof UserGroupViewHolder))
               {
                  convertView = null;
               }

            if (convertView == null)
               {
                  convertView = inflater.inflate(R.layout.listview_user_group_item, parent, false);

                  // set up the ViewHolder
                  viewHolder = new UserGroupViewHolder();
                  viewHolder.groupNameText = (TextView) convertView.findViewById(R.id.group_name_text);
                  viewHolder.letterTile = (ImageView) convertView.findViewById(R.id.gmailitem_letter);
                  viewHolder.numMembersText = (TextView) convertView.findViewById(R.id.num_members_text);

                  // store the holder with the view.
                  convertView.setTag(viewHolder);
               }
            else
               {
                  viewHolder = (UserGroupViewHolder) convertView.getTag();
               }

            viewHolder.groupNameText.setText(groupList.get(position).name);
            viewHolder.numMembersText.setText("Members: " + groupList.get(position).friendsList.size());

            viewHolder.letterTile.setImageBitmap(tileProvider.getLetterTile(groupList.get(position).name, groupList.get(position).name, tileSize, tileSize));


            return convertView;
         }


      @Override
      public boolean isEnabled(int position)
         {
            return true;
         }
   }
