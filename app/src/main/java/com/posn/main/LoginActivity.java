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
import com.posn.application.POSNApplication;
import com.posn.datatypes.Friend;
import com.posn.encryption.SymmetricKeyManager;
import com.posn.initial_setup.SetupPersonalInfoActivity;
import com.posn.utility.DeviceFileManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


public class LoginActivity extends BaseActivity implements OnClickListener
   {

      // variable declarations
      Button signupButton, loginButton;
      EditText passwordText, emailText;
      POSNApplication app;
      private ProgressDialog pDialog;


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
            actionBar.setTitle("Welcome - Login");

            // get the application
            app = (POSNApplication) this.getApplication();

            // create the storage directories
            createDefaultStorageDirectories();


            processURI(getIntent().getData());


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

                                 passwordText.setCursorVisible(false);
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


      // Background ASYNC Task to login by making HTTP Request
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


            // Before starting background thread Show Progress Dialog
            @Override
            protected void onPreExecute()
               {
                  super.onPreExecute();
                  pDialog = new ProgressDialog(LoginActivity.this);
                  pDialog.setMessage("Authenticating...");
                  pDialog.setIndeterminate(false);
                  pDialog.setCancelable(false);
                  pDialog.show();
               }


            // Checking login in background
            protected String doInBackground(String... params)
               {
                  // verify the password
                  if (verifyPassword(password))
                     {
                        if (verifyEmail(email))
                           {
    /*
                              app.setDropbox(new DropboxClientUsage(context));
                              app.setCloudProvider("Dropbox");
                              app.getDropbox().initializeDropbox();
                              app.getDropbox().authenticateDropboxLogin();

                              // check dropbox folders
                             // app.getDropbox().createDropboxStorageDirectories();

                              app.getDropbox().uploadFile("/multimedia/Test.jpg", app.multimediaFilePath + "/test.jpg");

*/

                              // load in profile information
                              loginVerify = true;


                           }
                     }


                  return null;
               }


            // After completing background task Dismiss the progress dialog
            protected void onPostExecute(String file_url)
               {
                  // dismiss the dialog once done
                  pDialog.dismiss();

                  if (loginVerify)
                     {
                        // Launch Employer homePage Screen
                        Intent homepage = new Intent(getApplicationContext(), MainActivity.class);

                        // Close all views before launching Employer
                        // homePage
                        homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homepage);

                        // Close Login Screen
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


      private boolean verifyPassword(String password)
         {
            // save the password to the application
            app.setPassword(password);

            // create AES key from password
            String passwordKey = SymmetricKeyManager.createKeyFromString(password);

            // check if the keys were read in from the file
            if (readKeysFromFile(app.encryptionKeyFilePath, passwordKey))
               {
                  // check if the password is valid
                  if (readPasswordVerificationFile(app.encryptionKeyFilePath, password, passwordKey))
                     {
                        return true;
                     }
               }
            return false;
         }


      private boolean verifyEmail(String email)
         {
            // try to load the personal data
            if (app.loadPersonalInformation())
               {
                  // try to verify the password
                  if (app.getEmailAddress().equals(email))
                     {
                        return true;
                     }
               }
            return false;
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
         }


      public boolean readKeysFromFile(String path, String key)
         {
            // declare variables
            String publicKey = "", privateKey = "";

            // read in the public and private keys from the file
            try
               {
                  // load the file from the path
                  File file = new File(path + "/keys.key");

                  // open the file
                  BufferedReader br = new BufferedReader(new FileReader(file));

                  // read in the header lines
                  br.readLine();
                  br.readLine();

                  // read in the number of lines from the file (public key)
                  br.readLine();

                  // parse to the string to get the number of lines (public key)
                  String line = br.readLine();
                  String[] split = line.split("\\s+");
                  int numLines = Integer.parseInt(split[1]);

                  // loop to get the public key string
                  for (int i = 0; i < numLines; i++)
                     {
                        publicKey += br.readLine();
                     }

                  // create a key from the public key string
                  app.publicKey = publicKey;

                  // read in the number of lines from the file (private key)
                  br.readLine();
                  line = br.readLine();

                  // parse to the string to get the number of lines (private key)
                  split = line.split("\\s+");
                  numLines = Integer.parseInt(split[1]);

                  // loop to get the encrypted private key (encrypted with user's password)
                  for (int i = 0; i < numLines; i++)
                     {
                        privateKey += br.readLine();
                     }

                  // close the file
                  br.close();

                  // decrypt the private key
                  app.privateKey = SymmetricKeyManager.decrypt(key, privateKey);

                  return true;
               }
            catch (IOException e)
               {
                  e.printStackTrace();
               }

            return false;
         }


      public boolean readPasswordVerificationFile(String path, String password, String key)
         {
            String encryptedText = "";

            File file = new File(path + "/verify.pass");

            BufferedReader br;
            try
               {
                  br = new BufferedReader(new FileReader(file));

                  br.readLine();

                  String line = br.readLine();
                  String[] split = line.split("\\s+");
                  int numLines = Integer.parseInt(split[3]);

                  for (int i = 0; i < numLines; i++)
                     {
                        encryptedText += br.readLine();
                     }

                  br.close();

                  String decryptedString = SymmetricKeyManager.decrypt(key, encryptedText);
                  System.out.println("STRING: " + decryptedString);

                  if (decryptedString != null)
                     {
                        if (decryptedString.equals("POSN - SUCCESS"))
                           {
                              return true;
                           }
                     }
               }
            catch (IOException e)
               {
                  e.printStackTrace();
               }


            return false;
         }

      private void processURI(Uri uriData)
         {
            if (uriData != null)
               {
                  List<String> params = uriData.getPathSegments();

                  // check the type of URI
                  String uriType = params.get(0);
                  if (uriType.equals("request"))
                     {
                        app.newFriendRequest = new Friend(params.get(1) + " " + params.get(2), params.get(3), 2);
                     }
                  else if (uriType.equals("confirm"))
                     {
                        app.newAcceptedFriend = new Friend(params.get(1) + " " + params.get(2), params.get(3), 3);
                     }
               }
         }
   }
