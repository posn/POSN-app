package com.posn.initial_setup;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.posn.Constants;
import com.posn.R;
import com.posn.datatypes.User;
import com.posn.exceptions.POSNCryptoException;
import com.posn.utility.AsymmetricKeyManager;
import com.posn.utility.DeviceFileManager;
import com.posn.utility.SymmetricKeyManager;

import java.io.IOException;


public class SetupEncryptionKeysActivity extends FragmentActivity implements OnClickListener
   {
      // user interface variables
      private DrawingView dv;
      private ProgressBar progressBar;

      private User user;
      private String password;

      private int timeCount;
      private final float MAXTIME = 80.0f;
      private boolean keyGenerated = false;
      private int numEncryptBits;


      // runs without a timer by reposting this handler at the end of the runnable
      Handler timerHandler = new Handler();
      Runnable timerRunnable = new Runnable()
         {

            @Override
            public void run()
               {
                  // check if the drawing screen is touched
                  if (dv.isTouched())
                     {
                        timeCount--;
                        progressBar.setProgress((int) (((MAXTIME - timeCount) / MAXTIME) * 100));
                     }
                  if (timeCount > 0)
                     {
                        timerHandler.postDelayed(this, 100);
                     }
                  else
                     {
                        progressBar.setProgress(100);

                        // create the encryption keys when the timer is done
                        createEncryptionKey();
                     }
               }
         };


      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_setup_encryption_keys);

            // get the user data from the previous activity
            if (getIntent().hasExtra("user"))
               {
                  user = (User) getIntent().getExtras().get("user");
                  password = getIntent().getExtras().getString("password");
               }

            // get the drawing view from the layout
            dv = (DrawingView) findViewById(R.id.draw_view);

            // get the progress bar from the layout
            progressBar = (ProgressBar) findViewById(R.id.progressBar);

            // get the radio buttons from the layout
            RadioButton lowEncryption = (RadioButton) findViewById(R.id.radio0);
            RadioButton highEncryption = (RadioButton) findViewById(R.id.radio1);

            // get the buttons from the layout
            Button next = (Button) findViewById(R.id.next_button);
            Button generate = (Button) findViewById(R.id.generate_button);

            // set the onclick listener for the buttons
            next.setOnClickListener(this);
            generate.setOnClickListener(this);
            lowEncryption.setOnClickListener(this);
            highEncryption.setOnClickListener(this);

            // get the action bar and set the page title
            ActionBar actionBar = getActionBar();
            if (actionBar != null)
               {
                  actionBar.setTitle("Generate Encryption Keys");
               }

            // default the number of bits for the encyrption key to 2048
            numEncryptBits = 2048;
         }


      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {

                  case R.id.next_button:

                     // check if the key has been generated
                     if (keyGenerated)
                        {
                           // start the next activity
                           Intent intent = new Intent(this, SetupGroupsActivity.class);
                           intent.putExtra("user", user);
                           intent.putExtra("password", password);
                           startActivity(intent);
                        }
                     // display toast with error message
                     else
                        {
                           Toast.makeText(this, "You must generate an encryption key", Toast.LENGTH_SHORT).show();
                        }

                     break;

                  case R.id.generate_button:

                     // set up drawing view
                     dv.resetDrawView();
                     dv.allowDrawing();

                     // set up progress bar
                     progressBar.setProgress(0);

                     // set the timer limt and start the timer
                     timeCount = 80;
                     timerHandler.postDelayed(timerRunnable, 0);
                     keyGenerated = false;
                     break;

                  case R.id.radio0:
                     numEncryptBits = 2048;
                     break;

                  case R.id.radio1:
                     numEncryptBits = 2048;
                     break;
               }

         }


      private void createEncryptionKey()
         {
            // disable drawing in the view
            dv.disableDrawing();

            // generate encryption keys
            keyGenerated = generateEncryptionKeys(dv.getRandomValues());
         }


      public boolean generateEncryptionKeys(int seed)
         {
            try
               {
                  // create a new Symmetric Key from the user's password
                  String key = SymmetricKeyManager.createKeyFromString(password);

                  // create password verification file contents by encrypting a the string
                  String verificationString = SymmetricKeyManager.encrypt(key, "POSN - SUCCESS");

                  // write the encrypted file contents to a file
                  DeviceFileManager.writeStringToFile(verificationString, Constants.encryptionKeyFilePath, "verify.pass");

                  // create a new RSA Public and Private key based on the random input
                  Pair<String, String> keyPair = AsymmetricKeyManager.generateKeys(numEncryptBits, seed);

                  // set the user's public and private key
                  user.publicKey = keyPair.first;
                  user.privateKey = keyPair.second;

                  return true;
               }
            catch (POSNCryptoException | IOException e)
               {
                  e.printStackTrace();
               }
            return false;
         }
   }
