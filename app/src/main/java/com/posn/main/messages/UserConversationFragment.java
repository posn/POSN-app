package com.posn.main.messages;

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
import android.widget.TextView;

import com.posn.Constants;
import com.posn.R;
import com.posn.datatypes.Conversation;
import com.posn.datatypes.Friend;
import com.posn.main.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class UserConversationFragment extends Fragment implements OnClickListener
   {

      // declare variables
      Context context;
      ArrayList<Conversation> conversationList;
      ListView lv;
      TextView noConversationsText;

      MainActivity main;

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
            System.out.println("MESSAGES ON CREATE!!!!!!!!!!!!!!");

            // load the system tab layout
            View view = inflater.inflate(R.layout.fragment_user_messages, container, false);
            context = getActivity();

            lv = (ListView) view.findViewById(R.id.listView1);
            noConversationsText = (TextView) view.findViewById(R.id.notification_text);

            // get the application
            main = (MainActivity) getActivity();

            // get the conversation list from the main activity
            conversationList = main.conversationList.conversations;

            // check if there are any notifications, if so then update listview
            if (conversationList.size() > 0)
               {
                  updateConversations();
               }

            adapter = new MessageArrayAdapter(getActivity(), conversationList);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new OnItemClickListener()
               {

                  @Override
                  public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                     {
                        Intent intent = new Intent(context, FriendMessagesActivity.class);

                        Conversation conversation = (Conversation) parent.getItemAtPosition(position);

                        String FriendID = conversation.friend;
                        Friend friend = main.masterFriendList.currentFriends.get(FriendID);
                        intent.putExtra("friendID", FriendID);
                        intent.putExtra("friend", friend);


                        context.startActivity(intent);
                     }


               });


            return view;
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
                  Conversation conversation = new Conversation("ec3591b0907170cc48c6759c013333f712141eb8", "Jan 19, 2015 at 1:45 pm", "Hello");
                  messages.put(conversation.createJSONObject());
                  // messageList.add(message);

                  conversation = new Conversation("726e60c84e88dd01b49ecf6f0de42843383bffad", "Jan 19, 2015 at 1:45 pm", "Hey");
                  messages.put(conversation.createJSONObject());

                  conversation = new Conversation("dc66ae1b5fa5c84cf12b82e2ec07f6b91233e8d4", "Jan 19, 2015 at 1:45 pm", "Hi");
                  messages.put(conversation.createJSONObject());

                  conversation = new Conversation("413e990ba1e5984d8fd41f1a1acaf3d154b21cab", "Jan 19, 2015 at 1:45 pm", "Test! TeSt!! TEST!!!");
                  messages.put(conversation.createJSONObject());

                  conversation = new Conversation("f9febf09f9d7632a7611598bc03baed8d5c7357d", "Jan 19, 2015 at 1:45 pm", "asdasd");
                  messages.put(conversation.createJSONObject());

                  conversation = new Conversation("eac054c17d7b49456f224788a12adf4eba4c0f9d", "Jan 19, 2015 at 1:45 pm", "Happy Birthday!");
                  messages.put(conversation.createJSONObject());

                  conversation = new Conversation("177ab489aa8cb82323ed02c2adb051c49c0c847d", "Jan 19, 2015 at 1:45 pm", "Whats Up!");
                  messages.put(conversation.createJSONObject());

                  JSONObject object = new JSONObject();
                  object.put("messages", messages);

                  String jsonStr = object.toString();

                  FileWriter fw = new FileWriter(Constants.applicationDataFilePath + Constants.converstationListFile);
                  BufferedWriter bw = new BufferedWriter(fw);
                  bw.write(jsonStr);
                  bw.close();

               }
            catch (JSONException | IOException e)
               {
                  e.printStackTrace();
               }
         }

      public void updateConversations()
         {
            System.out.println("CREATING MESSAGES!!!");

            if (conversationList.size() > 0)
               {
                  noConversationsText.setVisibility(View.GONE);
               }
            else
               {
                  noConversationsText.setVisibility(View.VISIBLE);
               }

            // notify the adapter about the data change
            adapter.notifyDataSetChanged();
         }
   }
