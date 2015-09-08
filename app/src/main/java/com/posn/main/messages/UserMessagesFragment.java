package com.posn.main.messages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.posn.R;
import com.posn.application.POSNApplication;
import com.posn.datatypes.Friend;
import com.posn.datatypes.Message;
import com.posn.main.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


public class UserMessagesFragment extends Fragment implements OnClickListener
   {

      // declare variables
      Context context;
      ArrayList<Message> messageList;
      ListView lv;

      MainActivity main;
      POSNApplication app;

      MessageArrayAdapter adapter;


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
Date date;

            
            // get the application
            main = (MainActivity) getActivity();
            app = (POSNApplication) getActivity().getApplication();

            // fill with fake data
            // createData();

            messageList = main.messageData;

            adapter = new MessageArrayAdapter(getActivity(), messageList);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new OnItemClickListener()
            {

               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                  {
                     Intent intent = new Intent(context, FriendMessagesActivity.class);

                     Message message = (Message) parent.getItemAtPosition(position);

                     String FriendID = message.friend;
                     Friend friend = main.masterFriendList.get(FriendID);
                     intent.putExtra("friendID", FriendID);
                     intent.putExtra("friend", friend);


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
            super.onAttach(activity);
            context = getActivity();
         }


      @Override
      public void onClick(View arg0)
         {

         }


      public void createData()
         {
            JSONArray messages = new JSONArray();

            try
               {
                  Message message = new Message("ec3591b0907170cc48c6759c013333f712141eb8", "Jan 19, 2015 at 1:45 pm", "Hello");
                  messages.put(message.createJOSNObject());
                  // messageList.add(message);

                  message = new Message("726e60c84e88dd01b49ecf6f0de42843383bffad", "Jan 19, 2015 at 1:45 pm", "Hey");
                  messages.put(message.createJOSNObject());

                  message = new Message("dc66ae1b5fa5c84cf12b82e2ec07f6b91233e8d4", "Jan 19, 2015 at 1:45 pm", "Hi");
                  messages.put(message.createJOSNObject());

                  message = new Message("413e990ba1e5984d8fd41f1a1acaf3d154b21cab", "Jan 19, 2015 at 1:45 pm", "Test! TeSt!! TEST!!!");
                  messages.put(message.createJOSNObject());

                  message = new Message("f9febf09f9d7632a7611598bc03baed8d5c7357d", "Jan 19, 2015 at 1:45 pm", "asdasd");
                  messages.put(message.createJOSNObject());

                  message = new Message("eac054c17d7b49456f224788a12adf4eba4c0f9d", "Jan 19, 2015 at 1:45 pm", "Happy Birthday!");
                  messages.put(message.createJOSNObject());

                  message = new Message("177ab489aa8cb82323ed02c2adb051c49c0c847d", "Jan 19, 2015 at 1:45 pm", "Whats Up!");
                  messages.put(message.createJOSNObject());

                  JSONObject object = new JSONObject();
                  object.put("messages", messages);

                  String jsonStr = object.toString();

                  FileWriter fw = new FileWriter(app.messagesFilePath + "/user_messages.txt");
                  BufferedWriter bw = new BufferedWriter(fw);
                  bw.write(jsonStr);
                  bw.close();

               }
            catch (JSONException | IOException e)
               {
                  e.printStackTrace();
               }
            /*
            HashSet<String> emlRecsHS = new HashSet<>();
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
                        message.friend = cur.getString(1);

                        if (emlRecsHS.add(message.friend))
                           {
                              message.date = new Date().toString();
                              message.lastMessage = "Test!";

                              messageList.add(message);
                              count++;
                           }

                     }
                  while (cur.moveToNext() && count < 10);
               }

            cur.close();
            */
         }

      public void updateMessages()
         {
            System.out.println("CREATING MESSAGES!!!");

            // notify the adapter about the data change
            adapter.notifyDataSetChanged();
         }
   }
