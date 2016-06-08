package com.posn.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.posn.R;
import com.posn.asynctasks.AuthenticateUserAsyncTask;
import com.posn.initial_setup.SetupPersonalInfoActivity;
import com.posn.utility.DeviceFileManager;
import com.posn.utility.UserInterfaceManager;


public class LoginActivity extends BaseActivity implements OnClickListener
   {
      // user interface variables
      public EditText passwordText, emailText;

      public Uri uri = null;

      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            // get the EditText from the layout
            emailText = (EditText) findViewById(R.id.email_text);
            passwordText = (EditText) findViewById(R.id.password_text);

            // get the buttons from the layout
            Button loginButton = (Button) findViewById(R.id.login_button);
            Button signupButton = (Button) findViewById(R.id.signup_button);

            // set an onclick listener for each button
            loginButton.setOnClickListener(this);
            signupButton.setOnClickListener(this);

            passwordText.setOnEditorActionListener(new OnEditorActionListener()
               {

                  @Override
                  public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
                     {
                        if (actionId == EditorInfo.IME_ACTION_DONE)
                           {
                              // Clear focus here from edittext
                              passwordText.setCursorVisible(false);
                              InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                              imm.hideSoftInputFromWindow(passwordText.getWindowToken(), 0);
                              passwordText.clearFocus();
                           }
                        return false;
                     }
               });

            // create the storage directories
            DeviceFileManager.createDefaultStorageDirectories();

            // get the URI that opened the app (uri is null if it does not exist)
            // URI is used for friend requests
            uri = getIntent().getData();
         }


      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.login_button:

                     // check if the email field is empty
                     if (!UserInterfaceManager.isEditTextEmpty(emailText))
                        {
                           // check if the password field is empty
                           if (!UserInterfaceManager.isEditTextEmpty(passwordText))
                              {
                                 // get the email and password from the edit texts
                                 String email = emailText.getText().toString();
                                 String password = passwordText.getText().toString();

                                 // close the keyboard and clear the focus
                                 UserInterfaceManager.hideKeyboard(this);

                                 // start a new asynctask to authenticate the user
                                 new AuthenticateUserAsyncTask(this, email, password).execute();
                              }
                           // show toast for empty password field
                           else
                              {
                                 UserInterfaceManager.showToast(this, "Please Enter the Password for your POSN Account.");
                              }
                        }
                     // show toast for empty email field
                     else
                        {
                           UserInterfaceManager.showToast(this, "Please Enter the Email for your POSN Account.");
                        }
                     break;

                  case R.id.signup_button:

                     // start a new activity to create a new account
                     Intent intent = new Intent(this, SetupPersonalInfoActivity.class);
                     startActivity(intent);
                     break;
               }
         }


   }
