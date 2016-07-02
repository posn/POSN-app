package com.posn.main.main.friends;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.posn.constants.Constants;
import com.posn.R;
import com.posn.main.main.groups.SelectGroupArrayAdapter;
import com.posn.datatypes.RequestedFriend;
import com.posn.datatypes.UserGroup;
import com.posn.utility.UserInterfaceHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * This activity class implements the functionality to get the data to add a new friend or get the friend groups after a friend request was accepted
 **/
public class AddFriendsActivity extends FragmentActivity implements OnClickListener
   {
      // user interface variables
      private EditText name, email;
      private int type;

      private RequestedFriend requestedFriend;

      private SelectGroupArrayAdapter adapter;


      /**
       * This method is called when the activity needs to be created and sets up the user interface
       * A single layout file is used, but different interface elements are removed depending on the type of data that is connected
       * Two types: TYPE_FRIEND_REQUEST_NEW - shows all of the interface, TYPE_FRIEND_REQUEST_ACCEPT - hides the fields to get the friend's name and email
       **/
      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            setContentView(R.layout.activity_add_friends);

            // get the group list and type from the intent extras
            ArrayList<UserGroup> userGroupList = getIntent().getExtras().getParcelableArrayList("groups");
            type = getIntent().getExtras().getInt("type");

            // check the layout type
            if (type == Constants.TYPE_FRIEND_REQUEST_NEW)
               {
                  requestedFriend = new RequestedFriend();
               }
            else
               {
                  requestedFriend = (RequestedFriend) getIntent().getExtras().get("requestedFriend");

                  // get the layout that holds the fields for name and email
                  LinearLayout friendInfoFields = (LinearLayout) findViewById(R.id.friend_info_layout);

                  // hide the fields
                  friendInfoFields.setVisibility(View.GONE);
               }


            // sort the grouplist by group name
            Collections.sort(userGroupList, new Comparator<UserGroup>()
               {
                  public int compare(UserGroup o1, UserGroup o2)
                     {
                        return o1.name.compareTo(o2.name);
                     }
               });

            // get the listview from the layout
            ListView lv = (ListView) findViewById(R.id.listView1);

            // get the EditText from the layout
            name = (EditText) findViewById(R.id.name_text);
            email = (EditText) findViewById(R.id.email_text);

            // get the buttons from the layout
            Button addFriend = (Button) findViewById(R.id.add_friend_button);

            // set onclick listener for each button
            addFriend.setOnClickListener(this);

            // create a custom adapter for each contact item in the listview
            adapter = new SelectGroupArrayAdapter(this, userGroupList, requestedFriend.groups);

            // set up the listview
            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            lv.setItemsCanFocus(true);
            lv.setAdapter(adapter);

            // set onItemClick listener to toggle the check box when the item is selected (not just the check box)
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
               {
                  @Override
                  public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
                     {
                        // get the checkbox from the layout and toggle it
                        CheckBox currentCheckBox = (CheckBox) view.findViewById(R.id.checkBox1);
                        currentCheckBox.toggle();

                        // get the user group that was selected
                        UserGroup userGroup = (UserGroup) parent.getItemAtPosition(position);

                        // update the adapter about the checked group
                        adapter.updateSelectedGroupList(userGroup);
                     }
               });

            // get the action bar and set the title
            ActionBar actionBar = getActionBar();
            if (actionBar != null)
               {
                  actionBar.setTitle("Add New Friend");
               }

         }


      /**
       * This method is called when the user clicks the different user interface elements and implements each element's functionality
       **/
      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.add_friend_button:

                     if (type == Constants.TYPE_FRIEND_REQUEST_NEW)
                        {
                           if ((!UserInterfaceHelper.isEditTextEmpty(email)))
                              {
                                 // change the status to pending
                                 requestedFriend.status = Constants.STATUS_PENDING;

                                 // get the friend's name from the edit text
                                 requestedFriend.name = name.getText().toString();

                                 // get the friend's email from the edit text
                                 requestedFriend.email = email.getText().toString();

                                 // create nonce
                                 requestedFriend.nonce = Integer.toString((int) (System.currentTimeMillis() / 1000));

                                 // return to the main activity and pass the requested friend back
                                 Intent resultIntent = new Intent();
                                 setResult(Activity.RESULT_OK, resultIntent);
                                 resultIntent.putExtra("requestedFriend", requestedFriend);
                                 finish();
                              }
                           else
                              {
                                 Toast.makeText(this, "You must add at least one friend.", Toast.LENGTH_SHORT).show();
                              }
                        }
                     else
                        {
                           // create a second nonce value
                           requestedFriend.nonce2 = Integer.toString((int) (System.currentTimeMillis() / 1000));

                           // return the requested friend back to the main activity with the selected groups
                           Intent resultIntent = new Intent();
                           setResult(Activity.RESULT_OK, resultIntent);
                           resultIntent.putExtra("requestedFriend", requestedFriend);
                           finish();
                        }
                     break;
               }
         }
   }
