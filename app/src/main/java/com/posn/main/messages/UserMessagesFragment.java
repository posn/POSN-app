package com.posn.main.messages;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.posn.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;


public class UserMessagesFragment extends Fragment implements OnClickListener
   {

      // declare variables
      Context context;
      ArrayList<Message> notificationsList = new ArrayList<Message>();
      ListView lv;


      @Override
      public void onResume()
         {
            super.onResume();
         }


      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            // load the system tab layout
            View view = inflater.inflate(R.layout.fragment_user_messages, container, false);
            context = getActivity();

            lv = (ListView) view.findViewById(R.id.listView1);

            // fill with fake data
            createData();
            final MessageArrayAdapter adapter = new MessageArrayAdapter(getActivity(), notificationsList);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new OnItemClickListener()
            {

               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                  {
                     Intent intent = new Intent(context, HelloBubblesActivity.class);

                     Message message = (Message) parent.getItemAtPosition(position);

                     String name = message.friendName;

                     intent.putExtra("name", name);

                     context.startActivity(intent);
                  }


            });

            return view;
         }


      @Override
      public void onActivityCreated(Bundle savedInstanceState)
         {
            super.onActivityCreated(savedInstanceState);
            onResume();
         }


      @Override
      public void onAttach(Activity activity)
         {
            // TODO Auto-generated method stub
            super.onAttach(activity);
            context = getActivity();
         }


      @Override
      public void onClick(View arg0)
         {
            // TODO Auto-generated method stub

         }


      public void createData()
         {
            HashSet<String> emlRecsHS = new HashSet<String>();
            int count = 0;
            ContentResolver cr = getActivity().getContentResolver();
            String[] PROJECTION = new String[]{ContactsContract.RawContacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_ID, ContactsContract.CommonDataKinds.Email.DATA, ContactsContract.CommonDataKinds.Photo.CONTACT_ID};
            String order = "CASE WHEN " + ContactsContract.Contacts.DISPLAY_NAME + " NOT LIKE '%@%' THEN 1 ELSE 2 END, " + ContactsContract.Contacts.DISPLAY_NAME + ", " + ContactsContract.CommonDataKinds.Email.DATA + " COLLATE NOCASE";
            String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
            Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
            if (cur.moveToFirst())
               {
                  do
                     {
                        Message message = new Message();

                        // names comes in hand sometimes
                        message.friendName = cur.getString(1);

                        if (emlRecsHS.add(message.friendName))
                           {
                              message.dateTime = new Date();
                              message.lastMessage = "Test!";

                              notificationsList.add(message);
                              count++;
                           }

                     }
                  while (cur.moveToNext() && count < 10);
               }

            cur.close();
         }
   }
