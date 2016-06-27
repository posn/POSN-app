package com.posn.initial_setup;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.posn.R;
import com.posn.main.groups.SetupInitialGroupsArrayAdapter;
import com.posn.asynctasks.SetupFilesAsyncTask;
import com.posn.datatypes.User;
import com.posn.datatypes.UserGroup;
import com.posn.main.BaseActivity;
import com.posn.main.LoginActivity;
import com.posn.utility.UserInterfaceManager;

import java.util.ArrayList;


/**
 * This activity class implements the functionality for the new user to create friendID groups
 * This activity also creates the initial application data files
 **/
public class SetupGroupsActivity extends BaseActivity implements OnClickListener
   {
      // user interface variables
      Button addButton, nextButton;
      EditText groupNameText;
      ListView lv;

      // user object to store the information about the new user
      public User newUser;

      // password object to pass to next activity
      public String password;

      // arraylists to hold the new user defined friendID groups
      ArrayList<UserGroup> userGroupNames = new ArrayList<>();
      ArrayList<String> selectedGroups = new ArrayList<>();

      // adapter for the listview
      SetupInitialGroupsArrayAdapter adapter;


      /**
       * This method is called when the activity needs to be created and handles setting up the user interface objects and sets listeners for touch events.
       **/
      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            setContentView(R.layout.activity_setup_groups);

            // get the user and password from the previous activity
            if (getIntent().hasExtra("user"))
               {
                  newUser = (User) getIntent().getExtras().get("user");
                  password = getIntent().getExtras().getString("password");
               }

            // get the listview from the layout
            lv = (ListView) findViewById(R.id.listView1);

            // get the EditText from the layout
            groupNameText = (EditText) findViewById(R.id.group_name_text);

            // get the buttons from the layout
            addButton = (Button) findViewById(R.id.add_button);
            nextButton = (Button) findViewById(R.id.next_button);

            // set onclick listener for each button
            addButton.setOnClickListener(this);
            nextButton.setOnClickListener(this);

            // create a custom adapter for each group item in the listview
            adapter = new SetupInitialGroupsArrayAdapter(this, userGroupNames, selectedGroups);

            // set up the listview
            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            lv.setItemsCanFocus(true);
            lv.setAdapter(adapter);

            // set onItemClick listener
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
               {

                  @Override
                  public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
                     {
                        CheckBox currentCheckBox = (CheckBox) view.findViewById(R.id.checkBox1);
                        currentCheckBox.toggle();

                        UserGroup userGroup = (UserGroup) parent.getItemAtPosition(position);

                        // get the contact that was click and toggle the check box
                        adapter.updateSelectedGroupList(userGroup);
                     }

               });

            // get the action bar and set the title
            ActionBar actionBar = getActionBar();
            if (actionBar != null)
               {
                  actionBar.setTitle("Create Friend Groups");
               }
         }


      /**
       * This method is called when the user touches a UI element and gives the element its functionality
       **/
      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.add_button:

                     // check that there is a group name in the editText
                     if (!UserInterfaceManager.isEditTextEmpty(groupNameText))
                        {
                           // create a new user group
                           UserGroup newUserGroup = new UserGroup();

                           // assign the group name and mark it as selected
                           newUserGroup.name = groupNameText.getText().toString();
                           newUserGroup.selected = true;

                           // add the group to the arraylist and adapter
                           selectedGroups.add(newUserGroup.name);
                           adapter.add(newUserGroup);

                           // notify the adapter of the change so the listview can be updated
                           adapter.notifyDataSetChanged();

                           // clear the editText
                           groupNameText.getText().clear();
                        }
                     else
                        {
                           // show error
                           Toast.makeText(this, "You must enter a group name", Toast.LENGTH_SHORT).show();
                        }
                     break;

                  case R.id.next_button:

                     // check to see if at least one group has been added/selected
                     if (selectedGroups.size() > 0)
                        {
                           // create a new intent to launch the login activity
                           Intent intent = new Intent(this, LoginActivity.class);
                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                           // create the initial application files from the new user data
                           new SetupFilesAsyncTask(this, selectedGroups, intent).execute();
                        }
                     else
                        {
                           Toast.makeText(this, "You selected at least one group", Toast.LENGTH_SHORT).show();
                        }
                     break;
               }
         }
   }
