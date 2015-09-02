package com.posn.main.messages;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.posn.R;

import java.util.ArrayList;
import java.util.List;


public class DiscussArrayAdapter extends ArrayAdapter<OneComment>
   {

      private TextView countryName;
      private List<OneComment> countries = new ArrayList<OneComment>();
      private LinearLayout wrapper;


      @Override
      public void add(OneComment object)
         {
            countries.add(object);
            super.add(object);
         }


      public DiscussArrayAdapter(Context context, int textViewResourceId)
         {
            super(context, textViewResourceId);
         }


      public int getCount()
         {
            return this.countries.size();
         }


      public OneComment getItem(int index)
         {
            return this.countries.get(index);
         }


      public View getView(int position, View convertView, ViewGroup parent)
         {
            View row = convertView;
            if (row == null)
               {
                  LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                  row = inflater.inflate(R.layout.listview_message_converstation_item, parent, false);
               }

            wrapper = (LinearLayout) row.findViewById(R.id.wrapper);

            OneComment coment = getItem(position);

            countryName = (TextView) row.findViewById(R.id.comment);

            countryName.setText(coment.comment);

            countryName.setBackgroundResource(coment.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
            wrapper.setGravity(coment.left ? Gravity.LEFT : Gravity.RIGHT);

            return row;
         }


      public Bitmap decodeToBitmap(byte[] decodedByte)
         {
            return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
         }

   }
