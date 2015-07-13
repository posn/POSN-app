package com.posn.main.friends;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.posn.R;
import com.posn.application.POSNApplication;
import com.posn.datatypes.Friend;
import com.posn.email.EmailSender;
import com.posn.initial_setup.ContactArrayAdapter;

import java.util.ArrayList;


public class AddFriendsActivity extends FragmentActivity implements OnClickListener
   {

      // declare variables
      Button add, addFriends;
      EditText name, email;
      ListView lv;

      ArrayList<Friend> friendList;
      ArrayList<Friend> contactList = new ArrayList<Friend>();

      POSNApplication app;


      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            // get the XML layout
            setContentView(R.layout.activity_add_friends);

            // get the listview from the layout
            lv = (ListView) findViewById(R.id.listView1);

            // get the EditText from the layout
            name = (EditText) findViewById(R.id.name_text);
            email = (EditText) findViewById(R.id.email_text);

            // get the buttons from the layout
            add = (Button) findViewById(R.id.add_button);
            addFriends = (Button) findViewById(R.id.add_friends_button);

            // set onclick listener for each button
            addFriends.setOnClickListener(this);
            add.setOnClickListener(this);

            // get all the phone contacts.

            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            lv.setItemsCanFocus(true);

            // create a custom adapter for each contact item in the listview
            final ContactArrayAdapter adapter = new ContactArrayAdapter(this, contactList);

            // set the adapter to the listview
            lv.setAdapter(adapter);

            // set onItemClick listener
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {

               @Override
               public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
                  {
                     // get the contact that was click and toggle the check box
                     final Friend item = (Friend) parent.getItemAtPosition(position);
                     adapter.updateContactList(item);

                     // refresh the listview
                     adapter.notifyDataSetChanged();
                  }

            });

            // get the action bar and set the title
            ActionBar actionBar = getActionBar();
            actionBar.setTitle("Add Additional Friends");

            app = (POSNApplication) getApplication();

            friendList = app.friendList;
         }


      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {

                  case R.id.add_friends_button:

                     if(!contactList.isEmpty())
                        {
                           Friend newFriend = contactList.get(0);

                           EmailSender test = new EmailSender("projectcloudbook@gmail.com", "cnlpass!!");
                           // test.sendMail("POSN TEST!", "SUCCESS!\n\nhttp://posn.com/data1/data2/data3_data4", "POSN", "eklukovich92@hotmail.com");
                           test.sendMail("POSN - New Friend Request", "SUCCESS!\n\nhttp://posn.com/request/" + app.getFirstName() + "/" + app.getLastName() + "/" + app.getEmailAddress(), "POSN", newFriend.email);
                           newFriend.status = 3;
                           friendList.add(newFriend);

                           Intent resultIntent = new Intent();
                           setResult(Activity.RESULT_OK, resultIntent);
                           finish();
                        }
                     else
                        {
                           Toast.makeText(this, "You must add at least one friend.", Toast.LENGTH_SHORT).show();
                        }
                     break;

                  case R.id.add_button:
                     ContactArrayAdapter adapter = (ContactArrayAdapter) lv.getAdapter();

                     Friend newContact = new Friend();

                     if (!isEmpty(name) && !isEmpty(email))
                        {
                           newContact.name = name.getText().toString();
                           newContact.email = email.getText().toString();
                           newContact.selected = true;

                           adapter.add(newContact);
                           adapter.notifyDataSetChanged();

                           name.getText().clear();
                           email.getText().clear();
                        }
                     else
                        {
                           Toast.makeText(this, "You must enter a name and email address.", Toast.LENGTH_SHORT).show();
                        }
                     break;

               }

         }


      private boolean isEmpty(EditText etText)
         {
            if (etText.getText().toString().trim().length() > 0)
               {
                  return false;
               }
            else
               {
                  return true;
               }
         }
   }
