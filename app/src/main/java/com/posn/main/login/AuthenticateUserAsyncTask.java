package com.posn.main.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.posn.constants.Constants;
import com.posn.exceptions.POSNCryptoException;
import com.posn.main.login.LoginActivity;
import com.posn.main.main.MainActivity;
import com.posn.managers.DeviceFileManager;
import com.posn.managers.AppDataManager;
import com.posn.utility.SymmetricKeyHelper;
import com.posn.utility.UserInterfaceHelper;

import org.json.JSONException;

import java.io.IOException;


/**
 * This AsyncTask class implements the functionality to authenticate the user's credentials:
 * <ul><li>The user's password is used to create an asymmetric key that is used to decrypt a verification file.
 *         If the message is correct, then it was successful
 * <li>Loads in the user data application file from the device and creates a new data manager object
 * <li>The email is then checked and if correct, then the main activity is launched</ul>
 **/
public class AuthenticateUserAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;
      private String email, password;
      private LoginActivity activity;

      private boolean loginVerify = false;


      AppDataManager dataManager;


      public AuthenticateUserAsyncTask(LoginActivity activity, String email, String password)
         {
            super();
            this.email = email;
            this.password = password;
            this.activity = activity;
         }

      @Override
      protected void onPreExecute()
         {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Authenticating...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }

      protected String doInBackground(String... params)
         {
            // declare variables
            try
               {
                  // check if password is valid
                  String deviceFileKey = SymmetricKeyHelper.createKeyFromString(password);

                  String verificationString = DeviceFileManager.loadStringFromFile(Constants.encryptionKeyFilePath, "verify.pass");

                  verificationString = SymmetricKeyHelper.decrypt(deviceFileKey, verificationString);

                  // verify the password
                  if (verificationString != null)
                     {
                        if (verificationString.equals("POSN - SUCCESS"))
                           {
                              // create a new data manager object
                              dataManager = new AppDataManager(deviceFileKey);

                              // load the user application file from the device
                              dataManager.loadUserAppFile();

                              // verify email address
                              if (email.equals(dataManager.userManager.email))
                                 {
                                    loginVerify = true;
                                 }
                           }
                     }
               }
            catch (POSNCryptoException | JSONException | IOException error)
               {
                  error.printStackTrace();
               }

            return null;
         }

      protected void onPostExecute(String file_url)
         {
            // dismiss the dialog once done
            pDialog.dismiss();

            if (loginVerify)
               {
                  Intent intent = new Intent(activity, MainActivity.class);
                  intent.putExtra("dataManager", dataManager);

                  if (activity.uri != null)
                     {
                        intent.putExtra("uri", activity.uri.toString());
                     }

                  // Close all views before launching Employer
                  activity.startActivity(intent);
                  activity.finish();

               }
            else
               {
                  // clear the email and password edit text
                  activity.emailText.getText().clear();
                  activity.passwordText.getText().clear();

                  // display an error
                  UserInterfaceHelper.showToast(activity, "Invalid email address or password. Please log in again.");
               }
         }

   }
