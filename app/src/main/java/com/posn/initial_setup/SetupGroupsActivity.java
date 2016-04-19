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
import com.posn.adapters.SetupGroupArrayAdapter;
import com.posn.asynctasks.SetupFilesAsyncTask;
import com.posn.datatypes.User;
import com.posn.datatypes.UserGroup;
import com.posn.main.BaseActivity;
import com.posn.main.LoginActivity;

import java.util.ArrayList;


public class SetupGroupsActivity extends BaseActivity implements OnClickListener
   {

      // declare variables
      Button addButton, nextButton;
      EditText groupNameText;
      ListView lv;

      public User user;
      public String password;

      ArrayList<UserGroup> userGroupNames = new ArrayList<>();
      ArrayList<String> selectedGroups = new ArrayList<>();

      SetupGroupArrayAdapter adapter;


      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            setContentView(R.layout.activity_setup_groups);

            if (getIntent().hasExtra("user"))
               {
                  user = (User) getIntent().getExtras().get("user");
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


            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            lv.setItemsCanFocus(true);


            // create a custom adapter for each contact item in the listview
            adapter = new SetupGroupArrayAdapter(this, userGroupNames, selectedGroups);

            // set the adapter to the listview
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


      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.add_button:

                     UserGroup newUserGroup = new UserGroup();

                     if (!isEmpty(groupNameText))
                        {
                           newUserGroup.name = groupNameText.getText().toString();
                           newUserGroup.selected = true;

                           selectedGroups.add(newUserGroup.name);
                           adapter.add(newUserGroup);
                           adapter.notifyDataSetChanged();
                           groupNameText.getText().clear();
                        }
                     else
                        {
                           Toast.makeText(this, "You must enter a group name", Toast.LENGTH_SHORT).show();
                        }

                     break;

                  case R.id.next_button:
                     if (selectedGroups.size() > 0)
                        {
                           Intent intent = new Intent(this, LoginActivity.class);
                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                           new SetupFilesAsyncTask(this, selectedGroups, intent).execute();
                        }
                     else
                        {
                           Toast.makeText(this, "You selected at least one group", Toast.LENGTH_SHORT).show();
                        }
                     break;
               }
         }


      private boolean isEmpty(EditText etText)
         {
            return etText.getText().toString().trim().length() <= 0;
         }
   }
