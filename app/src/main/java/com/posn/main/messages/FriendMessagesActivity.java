package com.posn.main.messages;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
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
import java.util.Random;

import de.svenjacobs.loremipsum.LoremIpsum;


public class FriendMessagesActivity extends Activity implements AsyncResponseConversation
   {
      private final int FRIEND_MESSAGE = 0;
      private final int USER_MESSAGAGE = 1;

      private DiscussArrayAdapter adapter;
      private ListView lv;
      private LoremIpsum ipsum;
      private EditText messageEditText;
      private static Random random;

      private POSNApplication app;

      LoadConversationAsyncTask asyncTaskConversation;

      private ArrayList<ConversationMessage> messageList = new ArrayList<>();

      String friendID;

      @Override
      public void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_message_converstation);
            random = new Random();
            ipsum = new LoremIpsum();

            // get the application
            app = (POSNApplication) this.getApplication();

            lv = (ListView) findViewById(R.id.listView1);

            adapter = new DiscussArrayAdapter(getApplicationContext(), messageList, R.layout.listview_message_converstation_item);
            lv.setAdapter(adapter);


            messageEditText = (EditText) findViewById(R.id.editText1);
            messageEditText.setOnKeyListener(new OnKeyListener()
            {
               public boolean onKey(View v, int keyCode, KeyEvent event)
                  {
                     // If the event is a key-down event on the "enter" button
                     if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                        {
                           // Perform action on key press
                           messageList.add(0, new ConversationMessage(USER_MESSAGAGE, messageEditText.getText().toString()));
                           //adapter.add(new ConversationMessage(USER_MESSAGAGE, messageEditText.getText().toString()));
                           messageEditText.setText("");

                           adapter.notifyDataSetChanged();

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

            addItems();
         }


      private void addItems()
         {
            messageList.add(new ConversationMessage(FRIEND_MESSAGE, "Hey!"));
            messageList.add(new ConversationMessage(USER_MESSAGAGE, "Hey man"));
            messageList.add(new ConversationMessage(USER_MESSAGAGE, "How are you?"));
            messageList.add(new ConversationMessage(FRIEND_MESSAGE, "I'm doing pretty good, you?"));
            messageList.add(new ConversationMessage(USER_MESSAGAGE, "I'm doing good"));
            messageList.add(new ConversationMessage(USER_MESSAGAGE, "Did you happen to do the homework yet? I'm stuck on the second problem and I was hoping you could help"));
            messageList.add(new ConversationMessage(FRIEND_MESSAGE, "I'm not that far yet"));
            messageList.add(new ConversationMessage(FRIEND_MESSAGE, "I will let you know when I am though"));
            messageList.add(new ConversationMessage(USER_MESSAGAGE, "Sounds good! Thanks :)"));
            messageList.add(new ConversationMessage(FRIEND_MESSAGE, "I'm not sure how to even approach that problem to be honest"));
            messageList.add(new ConversationMessage(USER_MESSAGAGE, "Yeah its tricky, hopefully we can figure it out soon haha"));




            /*
            for (int i = 0; i < 10; i++)
               {
                  int type = getRandomInteger(0, 1) == 0 ? FRIEND_MESSAGE : USER_MESSAGAGE;
                  int word = getRandomInteger(1, 10);
                  int start = getRandomInteger(1, 40);
                  String words = ipsum.getWords(word, start);

                  messageList.add(new ConversationMessage(type, words));
               }
            */
            adapter.notifyDataSetChanged();

            saveConversation();
         }


      private static int getRandomInteger(int aStart, int aEnd)
         {
            if (aStart > aEnd)
               {
                  throw new IllegalArgumentException("Start cannot exceed End.");
               }
            long range = (long) aEnd - (long) aStart + 1;
            long fraction = (long) (range * random.nextDouble());
            int randomNumber = (int) (fraction + aStart);
            return randomNumber;
         }


      @Override
      public boolean onOptionsItemSelected(MenuItem menuItem)
         {
            onBackPressed();
            return true;
         }

      public void saveConversation()
         {
            SaveConversationAsyncTask task = new SaveConversationAsyncTask(this, app.messagesFilePath + "/" +  friendID + ".txt", messageList);
            task.execute();
         }

      public void loadConversation()
         {
            asyncTaskConversation = new LoadConversationAsyncTask(this, app.messagesFilePath + "/" +  friendID + ".txt");
            asyncTaskConversation.delegate = this;
            asyncTaskConversation.execute();
         }

      public void loadingConversationFinished(ArrayList<ConversationMessage> messageList)
         {

         }


   }
