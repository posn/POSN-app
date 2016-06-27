package com.posn.main.wall;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.posn.R;
import com.posn.main.groups.SelectGroupArrayAdapter;
import com.posn.datatypes.UserGroup;

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
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_new_status_post);

            userGroupList = getIntent().getExtras().getParcelableArrayList("groups");


            status = (EditText) findViewById(R.id.postStatus);
            Button createStatusButton = (Button) findViewById(R.id.create_status_button);
            createStatusButton.setOnClickListener(this);

            // get the listview from the layout
            ListView lv = (ListView) findViewById(R.id.listView1);

            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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


            ActionBar actionBar = getActionBar();
            if (actionBar != null)
               {
                  actionBar.setDisplayHomeAsUpEnabled(true);
                  actionBar.setTitle("Create Status Post");
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
                  case R.id.create_status_button:
                     InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                     imm.hideSoftInputFromWindow(status.getWindowToken(), 0);

                     String statusMsg = status.getText().toString();

                     Intent resultIntent = new Intent();
                     resultIntent.putExtra("status", statusMsg);
                     resultIntent.putStringArrayListExtra("groups", selectedGroups);
                     setResult(Activity.RESULT_OK, resultIntent);
                     finish();
                     break;
               }
         }

   }
