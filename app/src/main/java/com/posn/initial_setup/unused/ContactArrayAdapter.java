package com.posn.initial_setup.unused;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Friend;

import java.util.ArrayList;


class ViewHolder
   {

      TextView nameText;
      TextView emailText;
      ImageView thumb_image;
      CheckBox checkBox;

   }


public class ContactArrayAdapter extends ArrayAdapter<Friend>
   {

      private final Context context;
      private ArrayList<Friend> values;
      ArrayList<Friend> selectedContacts = new ArrayList<>();
      ViewHolder mViewHolder = null;


      public ContactArrayAdapter(Context context, ArrayList<Friend> values)
         {
            super(context, R.layout.listview_contact_item, values);
            this.context = context;
            this.values = values;
            System.out.println(values.size());
            System.out.println(this.values.size());

         }


      @Override
      public View getView(final int position, View convertView, ViewGroup parent)
         {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.listview_contact_item, parent, false);

            ViewHolder mViewHolder = new ViewHolder();

            mViewHolder.nameText = (TextView) convertView.findViewById(R.id.name);
            mViewHolder.emailText = (TextView) convertView.findViewById(R.id.email_address);
            mViewHolder.thumb_image = (ImageView) convertView.findViewById(R.id.image);
            mViewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);

            mViewHolder.checkBox.setOnClickListener(new OnClickListener()
            {

               @Override
               public void onClick(View v)
                  {
                     updateContactList(values.get(position));
                  }
            });

            mViewHolder.nameText.setText(values.get(position).name);
            mViewHolder.emailText.setText(values.get(position).email);
            mViewHolder.checkBox.setChecked(values.get(position).selected);

            return convertView;
         }


      @Override
      public int getCount()
         {
            return values.size();
         }


      public void updateContactList(Friend item)
         {
            if (item.selected)
               {
                  item.selected = false;
                  System.out.println("NOT CHECKED");
                  selectedContacts.remove(item);
               }
            else
               {
                  item.selected = true;
                  System.out.println("CHECKED");
                  selectedContacts.add(item);
               }
         }


      public void selectAllContacts()
         {
            for (int i = 0; i < values.size(); i++)
               {
                  values.get(i).selected = true;
                  if (!selectedContacts.contains(values.get(i)))
                     selectedContacts.add(values.get(i));
               }
         }


      public void clearSelectedContacts()
         {
            for (int i = 0; i < values.size(); i++)
               {
                  values.get(i).selected = false;
               }
            selectedContacts.clear();
         }
   }
