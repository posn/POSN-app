package com.posn.initial_setup;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.posn.R;
import com.posn.datatypes.User;


public class SetupPasswordActivity extends FragmentActivity implements OnClickListener
   {

      Button next;
      EditText passwordText, confirmPasswordText;
      User user;


      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_setup_password);

            if (getIntent().hasExtra("user"))
               {
                  user = (User) getIntent().getExtras().get("user");
               }

            // get the EditText from the layout
            passwordText = (EditText) findViewById(R.id.password_text);
            confirmPasswordText = (EditText) findViewById(R.id.confirm_password_text);

            // get the buttons from the layout
            next = (Button) findViewById(R.id.next_button);

            // set an onclick listener for each button
            next.setOnClickListener(this);

            confirmPasswordText.setOnEditorActionListener(new OnEditorActionListener()
               {

                  @Override
                  public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
                     {
                        if (actionId == EditorInfo.IME_ACTION_DONE)
                           {
                              // Clear focus here from edittext
                              confirmPasswordText.setCursorVisible(false);
                              InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                              imm.hideSoftInputFromWindow(confirmPasswordText.getWindowToken(), 0);
                              confirmPasswordText.clearFocus();
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


      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {

                  case R.id.next_button:

                     // check if the passwords are empty
                     if (!isEmpty(passwordText) && !isEmpty(confirmPasswordText))
                        {

                           // get the passwords from the edittexts
                           String password = passwordText.getText().toString();
                           String confirmPassword = confirmPasswordText.getText().toString();

                           // check if the two passwords match
                           if (password.equals(confirmPassword))
                              {

                                 // start the encryption activity
                                 Intent intent = new Intent(this, SetupCloudProvidersActivity.class);
                                 intent.putExtra("user", user);
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
