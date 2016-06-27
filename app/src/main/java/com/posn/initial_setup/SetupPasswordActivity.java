package com.posn.initial_setup;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.posn.R;
import com.posn.datatypes.User;
import com.posn.utility.UserInterfaceManager;


/**
 * This activity class implements the functionality for a new user to create a password
 **/
public class SetupPasswordActivity extends FragmentActivity implements OnClickListener
   {
      // user interface variables
      Button next;
      EditText passwordText, confirmPasswordText;

      // user object to store the information about the new user
      User newUser;


      /**
       * This method is called when the activity needs to be created and handles setting up the user interface objects and sets listeners for touch events.
       **/
      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_setup_password);

            // get the new user object from the setup personal info activity
            if (getIntent().hasExtra("user"))
               {
                  newUser = (User) getIntent().getExtras().get("user");
               }

            // get the EditText from the layout
            passwordText = (EditText) findViewById(R.id.password_text);
            confirmPasswordText = (EditText) findViewById(R.id.confirm_password_text);

            // get the buttons from the layout
            next = (Button) findViewById(R.id.next_button);

            // set an onclick listener for each button
            next.setOnClickListener(this);

            // set a lister for when the user touches the done button in the keyboard
            confirmPasswordText.setOnEditorActionListener(new OnEditorActionListener()
               {

                  @Override
                  public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
                     {
                        if (actionId == EditorInfo.IME_ACTION_DONE)
                           {
                              UserInterfaceManager.hideKeyboard(SetupPasswordActivity.this);
                           }
                        return false;
                     }
               });

            // get the action bar and set the page title
            ActionBar actionBar = getActionBar();
            if (actionBar != null)
               {
                  actionBar.setTitle("Create Password");
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
                  case R.id.next_button:

                     // check if the passwords are empty
                     if (!UserInterfaceManager.isEditTextEmpty(passwordText) && !UserInterfaceManager.isEditTextEmpty(confirmPasswordText))
                        {
                           // get the passwords from the edittexts
                           String password = passwordText.getText().toString();
                           String confirmPassword = confirmPasswordText.getText().toString();

                           // check if the two passwords match
                           if (password.equals(confirmPassword))
                              {
                                 // start the setup cloud provider activity
                                 Intent intent = new Intent(this, SetupCloudProvidersActivity.class);
                                 intent.putExtra("user", newUser);
                                 intent.putExtra("password", password);
                                 startActivity(intent);
                              }
                           else
                              {
                                 // clear the password edit texts
                                 passwordText.getText().clear();
                                 confirmPasswordText.getText().clear();

                                 // show error to the user
                                 Toast.makeText(this, "Password do not match. Please enter passwords.", Toast.LENGTH_SHORT).show();
                              }
                        }
                     else
                        {
                           Toast.makeText(this, "One or more passwords are empty. Please enter passwords", Toast.LENGTH_SHORT).show();
                        }
                     break;
               }
         }
   }
