package com.posn.main;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.posn.Constants;
import com.posn.R;
import com.posn.datatypes.User;
import com.posn.encryption.SymmetricKeyManager;
import com.posn.initial_setup.SetupPersonalInfoActivity;
import com.posn.utility.DeviceFileManager;


public class LoginActivity extends BaseActivity implements OnClickListener
   {
      // variable declarations
      private Button signupButton, loginButton;
      private EditText passwordText, emailText;
      private ProgressDialog pDialog;

      private User user = new User();

      Uri uri = null;

      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            // get the EditText from the layout
            emailText = (EditText) findViewById(R.id.email_text);
            passwordText = (EditText) findViewById(R.id.password_text);

            // get the buttons from the layout
            loginButton = (Button) findViewById(R.id.login_button);
            signupButton = (Button) findViewById(R.id.signup_button);

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

            // get the action bar and set the page title
            ActionBar actionBar = getActionBar();
            if (actionBar != null)
               {
                  actionBar.setTitle("Welcome - Login");
               }

            // create the storage directories
            createDefaultStorageDirectories();

            uri = getIntent().getData();
         }


      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.login_button:

                     // check if the email field is empty
                     if (!isEmpty(emailText))
                        {
                           // check if the password field is empty
                           if (!isEmpty(passwordText))
                              {
                                 // get the passwords from the edit texts
                                 String email = emailText.getText().toString();
                                 String password = passwordText.getText().toString();

                                 // passwordText.setCursorVisible(false);
                                 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                 imm.hideSoftInputFromWindow(passwordText.getWindowToken(), 0);
                                 passwordText.clearFocus();

                                 new AuthenticateUser(this, email, password).execute();


                              }
                           // show toast for empty password field
                           else
                              {
                                 Toast.makeText(this, "Please Enter the Password for your POSN Account.", Toast.LENGTH_SHORT).show();
                              }
                        }
                     // show toast for empty email field
                     else
                        {
                           Toast.makeText(this, "Please Enter the Email for your POSN Account.", Toast.LENGTH_SHORT).show();
                        }
                     break;

                  case R.id.signup_button:

                     // start a new activity to create a new account
                     Intent intent = new Intent(this, SetupPersonalInfoActivity.class);
                     startActivity(intent);
                     break;
               }

         }


      class AuthenticateUser extends AsyncTask<String, String, String>
         {
            String email, password;
            boolean loginVerify;
            Context context;

            public AuthenticateUser(Context context, String email, String password)
               {
                  super();
                  this.email = email;
                  this.password = password;
                  this.context = context;
                  loginVerify = false;
               }

            @Override
            protected void onPreExecute()
               {
                  super.onPreExecute();
                  pDialog = new ProgressDialog(context);
                  pDialog.setMessage("Authenticating...");
                  pDialog.setIndeterminate(false);
                  pDialog.setCancelable(false);
                  pDialog.show();
               }

            protected String doInBackground(String... params)
               {
                  // check if password is valid
                  String key = SymmetricKeyManager.createKeyFromString(password);

                  String verificationString = DeviceFileManager.loadStringFromFile(Constants.encryptionKeyFilePath + "/verify.pass");
                  verificationString = SymmetricKeyManager.decrypt(key, verificationString);

                  // verify the password
                  if (verificationString != null)
                     {
                        if (verificationString.equals("POSN - SUCCESS"))
                           {
                              // load user data from file
                              user.loadUserFromFile(password, Constants.profileFilePath + "/user.txt");

                              // verify email address
                              if (email.equals(user.email))
                                 {
                                    loginVerify = true;
                                 }
                           }
                     }

                  return null;
               }

            protected void onPostExecute(String file_url)
               {
                  // dismiss the dialog once done
                  pDialog.dismiss();

                  if (loginVerify)
                     {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("user", user);

                        if (uri != null)
                           {
                              intent.putExtra("uri", uri.toString());
                           }

                        // Close all views before launching Employer
                        startActivity(intent);
                        finish();

                     }
                  else
                     {
                        // clear the email and password edit text
                        emailText.getText().clear();
                        passwordText.getText().clear();

                        // display an error
                        Toast.makeText(context, "Invalid email address or password. Please log in again.", Toast.LENGTH_SHORT).show();
                     }
               }
         }


      private boolean isEmpty(EditText etText)
         {
            // check if the length of the text is greater than 0
            return (etText.getText().toString().trim().length() <= 0);
         }


      void createDefaultStorageDirectories()
         {
            DeviceFileManager.createDirectory(Constants.archiveFilePath);
            DeviceFileManager.createDirectory(Constants.encryptionKeyFilePath);
            DeviceFileManager.createDirectory(Constants.multimediaFilePath);
            DeviceFileManager.createDirectory(Constants.profileFilePath);
            DeviceFileManager.createDirectory(Constants.wallFilePath);
            DeviceFileManager.createDirectory(Constants.messagesFilePath);
            DeviceFileManager.createDirectory(Constants.applicationDataFilePath);
            DeviceFileManager.createDirectory(Constants.friendsFilePath);
         }


   }
