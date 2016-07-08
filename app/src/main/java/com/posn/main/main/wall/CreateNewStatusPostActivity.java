package com.posn.main.main.wall;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.posn.R;
import com.posn.datatypes.UserGroup;
import com.posn.main.main.groups.SelectGroupArrayAdapter;
import com.posn.utility.UserInterfaceHelper;

import java.util.ArrayList;


/**
 * This activity class implements the functionality for a user to create a new status post
 * <ul><li>The user can enter a text status of any length
 * <li>The user must selected which group(s) to share the photo with
 * <li>The new post object is returned the main activity through an activity result</ul>
 **/
public class CreateNewStatusPostActivity extends Activity implements View.OnClickListener
   {
      // declare variables
      private EditText status;

      ArrayList<UserGroup> userGroupList;
      ArrayList<String> selectedGroups = new ArrayList<>();
      SelectGroupArrayAdapter adapter;


      /**
       * This method is called when the activity needs to be created and handles the interface elements.
       * <ul><li>Sets up the list view for groups and add listeners for buttons</ul>
       **/
      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_new_status_post);

            userGroupList = getIntent().getExtras().getParcelableArrayList("groups");


            status = (EditText) findViewById(R.id.postStatus);
            status.setOnFocusChangeListener(new View.OnFocusChangeListener()
               {
                  @Override public void onFocusChange(View v, boolean hasFocus)
                     {
                        UserInterfaceHelper.hideKeyboard(CreateNewStatusPostActivity.this);
                     }
               });

            Button createStatusButton = (Button) findViewById(R.id.create_status_button);
            createStatusButton.setOnClickListener(this);

            // get the listview from the layout
            ListView lv = (ListView) findViewById(R.id.listView1);

            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lv.setItemsCanFocus(true);

            // create a custom adapter for each contact item in the listview
            adapter = new SelectGroupArrayAdapter(this, userGroupList, selectedGroups);

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

            // set up action bar
            ActionBar actionBar = getActionBar();
            if (actionBar != null)
               {
                  actionBar.setDisplayHomeAsUpEnabled(true);
                  actionBar.setTitle("Create Status Post");
                  actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white);
               }

         }


      /**
       * This method is called when the user touches the back button on their device
       **/
      @Override
      public void onBackPressed()
         {
            // check if the status edittext contains text
            if (!UserInterfaceHelper.isEditTextEmpty(status))
               {
                  // warn the user about exiting
                  new AlertDialog.Builder(this)
                      .setTitle("Discard New Post?")
                      .setMessage("Are you sure you want to discard your status?")
                      .setNegativeButton(android.R.string.no, null)
                      .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                         {

                            public void onClick(DialogInterface arg0, int arg1)
                               {
                                  finish();
                               }
                         }).create().show();
               }
            else
               {
                  // end the activity
                  finish();
               }
         }

      /**
       * This method is called when the user clicks the buttons on the action bar
       **/
      @Override
      public boolean onMenuItemSelected(int featureId, MenuItem item)
         {
            int itemId = item.getItemId();
            switch (itemId)
               {
                  case android.R.id.home:
                     onBackPressed();
                     break;

               }

            return true;
         }


      /**
       * This method is called when the user clicks the different user interface elements and implements each element's functionality
       **/
      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.create_status_button:

                     // check if at least one group was selected
                     if (selectedGroups.size() > 0)
                        {
                           // check if a status message has been entered
                           if (!UserInterfaceHelper.isEditTextEmpty(status))
                              {

                                 // get the text status from the edit text
                                 String statusMsg = status.getText().toString();

                                 // return the data back to the main activity
                                 Intent resultIntent = new Intent();
                                 resultIntent.putExtra("status", statusMsg);
                                 resultIntent.putStringArrayListExtra("groups", selectedGroups);
                                 setResult(Activity.RESULT_OK, resultIntent);
                                 finish();
                              }
                           else
                              {
                                 // show toast with error message
                                 UserInterfaceHelper.showToast(this, "Please enter a status message");
                              }
                        }
                     else
                        {
                           // show toast with error message
                           UserInterfaceHelper.showToast(this, "Please select at least one group");
                        }
                     break;
               }
         }

   }
