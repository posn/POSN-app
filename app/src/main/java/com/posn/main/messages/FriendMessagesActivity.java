package com.posn.main.messages;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.posn.R;
import com.posn.application.POSNApplication;
import com.posn.asynctasks.messages.AsyncResponseConversation;
import com.posn.asynctasks.messages.LoadConversationAsyncTask;
import com.posn.asynctasks.messages.SaveConversationAsyncTask;
import com.posn.datatypes.ConversationMessage;
import com.posn.datatypes.Friend;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class FriendMessagesActivity extends Activity implements AsyncResponseConversation, View.OnClickListener
   {
      private final int FRIEND_MESSAGE = 0;
      private final int USER_MESSAGAGE = 1;

      private DiscussArrayAdapter adapter;
      private ListView lv;
      private EditText messageEditText;

      private POSNApplication app;

      LoadConversationAsyncTask asyncTaskConversation;

      private ArrayList<ListViewConversationItem> messageList = new ArrayList<>();
      private Map<String, ArrayList<ConversationMessage>> conversationMessages;

      String friendID;

      @Override
      public void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_message_converstation);

            // get the application
            app = (POSNApplication) this.getApplication();

            lv = (ListView) findViewById(R.id.listView1);

            adapter = new DiscussArrayAdapter(getApplicationContext(), messageList);
            lv.setAdapter(adapter);

            Button sendButton = (Button) findViewById(R.id.send_button);
            sendButton.setOnClickListener(this);

            messageEditText = (EditText) findViewById(R.id.editText1);
            messageEditText.clearFocus();
            messageEditText.setOnKeyListener(new OnKeyListener()
            {
               public boolean onKey(View v, int keyCode, KeyEvent event)
                  {
                     // If the event is a key-down event on the "enter" button
                     if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                        {
                           // get the current date/time the post was made
                           Date date = new Date();

                           // add the message to the hashmap and listview
                           addNewMessage(new ConversationMessage(USER_MESSAGAGE, date, messageEditText.getText().toString()));

                           // clear the edit text
                           messageEditText.setText("");

                           // true if the event was handled
                           return true;
                        }
                     return false;
                  }
            });

            Intent intent = getIntent();
            friendID = intent.getExtras().getString("friendID");
            Friend friend = intent.getParcelableExtra("friend");

            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Conversation with " + friend.name);

            // addItems();
            loadConversation();
         }


      private void addItems()
         {
            /*
            Date date = new Date();
            conversationMessages.add(new ConversationMessage(FRIEND_MESSAGE, date, "Hey!"));
            conversationMessages.add(new ConversationMessage(USER_MESSAGAGE, date, "Hey man"));
            conversationMessages.add(new ConversationMessage(USER_MESSAGAGE, date, "How are you?"));
            conversationMessages.add(new ConversationMessage(FRIEND_MESSAGE, date, "I'm doing pretty good, you?"));
            conversationMessages.add(new ConversationMessage(USER_MESSAGAGE, date, "I'm doing good"));
            conversationMessages.add(new ConversationMessage(USER_MESSAGAGE, date, "Did you happen to do the homework yet? I'm stuck on the second problem and I was hoping you could help"));
            conversationMessages.add(new ConversationMessage(FRIEND_MESSAGE, date, "I'm not that far yet"));
            conversationMessages.add(new ConversationMessage(FRIEND_MESSAGE, date, "I will let you know when I am though"));
            conversationMessages.add(new ConversationMessage(USER_MESSAGAGE, date, "Sounds good! Thanks :)"));
            conversationMessages.add(new ConversationMessage(FRIEND_MESSAGE, date, "I'm not sure how to even approach that problem to be honest"));
            conversationMessages.add(new ConversationMessage(USER_MESSAGAGE, date, "Yeah its tricky, hopefully we can figure it out soon haha"));

            adapter.notifyDataSetChanged();

            saveConversation();
            */
         }


      @Override
      public boolean onOptionsItemSelected(MenuItem menuItem)
         {
            onBackPressed();
            return true;
         }

      public void saveConversation()
         {
            SaveConversationAsyncTask task = new SaveConversationAsyncTask(this, app.messagesFilePath + "/" + friendID + ".txt", conversationMessages);
            task.execute();
         }

      public void loadConversation()
         {
            asyncTaskConversation = new LoadConversationAsyncTask(this, app.messagesFilePath + "/" + friendID + ".txt");
            asyncTaskConversation.delegate = this;
            asyncTaskConversation.execute();
         }

      public void loadingConversationFinished(HashMap<String, ArrayList<ConversationMessage>> messageList)
         {
            this.conversationMessages = new TreeMap<>(messageList);

            createConversation();
         }

      public void createConversation()
         {
            // loop through all the dates
            for (Map.Entry<String, ArrayList<ConversationMessage>> entry : conversationMessages.entrySet())
               {
                  // get the messages for the date
                  ArrayList<ConversationMessage> conversation = entry.getValue();

                  // add the date header
                  messageList.add(new ConversationHeaderItem(conversation.get(0).getHeaderDateString()));

                  // add all of the messages
                  for (int i = 0; i < conversation.size(); i++)
                     {
                        messageList.add(new ConversationMessageItem(conversation.get(i)));
                     }
               }

            // update the listview with the changes
            adapter.notifyDataSetChanged();
         }

      public void addNewMessage(ConversationMessage message)
         {
            String key = message.getKeyDateString();

            // check if the hashmap has the date
            if (conversationMessages.containsKey(key))
               {
                  // if it does then add the message to the date
                  conversationMessages.get(key).add(message);
               }
            else
               {
                  // if it does not then create a key arraylist
                  ArrayList<ConversationMessage> conversation = new ArrayList<>();

                  // add the message to the list
                  conversation.add(message);
                  conversationMessages.put(key, conversation);

                  // create a new header in the listview
                  messageList.add(new ConversationHeaderItem(message.getHeaderDateString()));
               }

            messageList.add(new ConversationMessageItem(message));

            adapter.notifyDataSetChanged();

            saveConversation();
         }


      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.send_button:
                     // get the current date/time the post was made
                     Date date = new Date();

                     // add the message to the hashmap and listview
                     addNewMessage(new ConversationMessage(USER_MESSAGAGE, date, messageEditText.getText().toString()));

                     // clear the edit text
                     messageEditText.setText("");
                     break;
               }
         }


   }
