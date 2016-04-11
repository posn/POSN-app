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

import com.posn.R;
import com.posn.application.POSNApplication;
import com.posn.encryption.AsymmetricKeyManager;
import com.posn.encryption.SymmetricKeyManager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class SetupEncryptionKeysActivity extends FragmentActivity implements OnClickListener
	{

		Button next, generate;
		DrawingView dv;
		ProgressBar progressBar;
		RadioButton lowEncryption, highEncryption;

		int timeCount;
		float MAXTIME = 80.0f;
		boolean keyGenerated = false;
		int numEncryptBits;

		POSNApplication app;

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
								createEncryptionKey();
							}
					}
			};


		@Override
		protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_setup_encryption_keys);

				// get the application to store data
				app = (POSNApplication) this.getApplication();

				// get the drawing view from the layout
				dv = (DrawingView) findViewById(R.id.draw_view);

				// get the progress bar from the layout
				progressBar = (ProgressBar) findViewById(R.id.progressBar);

				// get the radio buttons from the layout
				lowEncryption = (RadioButton) findViewById(R.id.radio0);
				highEncryption = (RadioButton) findViewById(R.id.radio1);

				// get the buttons from the layout
				next = (Button) findViewById(R.id.next_button);
				generate = (Button) findViewById(R.id.generate_button);

				// set the onclick listener for the buttons
				next.setOnClickListener(this);
				generate.setOnClickListener(this);
				lowEncryption.setOnClickListener(this);
				highEncryption.setOnClickListener(this);

				// get the action bar and set the page title
				ActionBar actionBar = getActionBar();
				actionBar.setTitle("Generate Encryption Keys");

				numEncryptBits = 2048;

			}


		@Override
		public void onClick(View v)
			{
				switch(v.getId())
				{

					case R.id.next_button:

						if (keyGenerated)
							{
								Intent intent = new Intent(this, SetupProfilePictureActivity.class);
								startActivity(intent);
							}
						else
							{
								Toast.makeText(this, "You Must Generate an Encryption Key", Toast.LENGTH_SHORT).show();
							}

						break;

					case R.id.generate_button:
						dv.resetDrawView();
						dv.allowDrawing();
						progressBar.setProgress(0);
						timeCount = 80;
						timerHandler.postDelayed(timerRunnable, 0);
						keyGenerated = false;
					//	createEncryptionKey();
						break;

					case R.id.radio0:
						numEncryptBits = 1024;
						break;

					case R.id.radio1:
						numEncryptBits = 2048;
						break;

				}

			}


		void createEncryptionKey()
			{
				dv.disableDrawing();
				createEncryptionKeys(dv.getRandomValues());
				keyGenerated = true;
				app.savePersonalInformation();
			}


		public boolean createEncryptionKeys(int seed)
			{
				String password = app.getPassword();
				boolean status;
				
				System.out.println("PASS: " + password);

				// create a new Symmetric Key from the user's password
				String key = SymmetricKeyManager.createKeyFromString(password);
				if (key == null)
					{
						Toast.makeText(this, "Failed to create AES Key.", Toast.LENGTH_SHORT).show();
						return false;
					}

				// create a password verification file
				status = createPasswordVerificationFile(app.encryptionKeyFilePath, key, password);
				if (!status)
					{
						Toast.makeText(this, "Failed to create password verification file.", Toast.LENGTH_SHORT).show();
						return false;
					}
				
				// create a new RSA Public and Private key based on the random input
				Pair<String, String> keyPair = AsymmetricKeyManager.generateKeys(numEncryptBits, seed);
				if (keyPair == null)
					{
						Toast.makeText(this, "Failed to create RSA Key.", Toast.LENGTH_SHORT).show();
						return false;
					}

				// save the public and private key to a file on the device
				status = saveKeysToFile(app.encryptionKeyFilePath, keyPair, key);
				if (!status)
					{
						Toast.makeText(this, "Failed to save keys to file.", Toast.LENGTH_SHORT).show();
						return false;
					}

				return true;
			}


		public boolean saveKeysToFile(String path, Pair<String, String> keyPair, String passwordKey)
			{
				try
					{
						// Store Public Key.
						File file = new File(path + "/keys.key");

						// if file doesnt exists, then create it
						if (!file.exists())
							{
								file.createNewFile();
							}

						PrintWriter printWriter = new PrintWriter(file);

						String publicKey = keyPair.first;

						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
						String currentDateandTime = sdf.format(new Date());

						// create header in the file
						printWriter.println("POSN: RSA - " + numEncryptBits);
						printWriter.println("Encryption: AES");
						printWriter.println("Comment: rsa-key-" + currentDateandTime);

						// get the number of lines in the public key
						int numLines = countLines(publicKey);
						printWriter.println("Public-Lines: " + numLines);

						// put the public key in the file
						printWriter.println(publicKey);

						String privateKey = keyPair.second;

						String encryptedPrivateKey = SymmetricKeyManager.encrypt(passwordKey, privateKey);

						// get the number of lines in the private key
						numLines = countLines(encryptedPrivateKey);
						printWriter.println("Private-Lines: " + numLines);
						printWriter.println(encryptedPrivateKey);

						printWriter.flush();
						printWriter.close();

						// if file doesnt exists, then create it
						if (!file.exists())
							{
								file.createNewFile();
							}
						return true;
					}
				catch (IOException e)
					{
						e.printStackTrace();
					}

				return false;
			}


		public boolean createPasswordVerificationFile(String path, String key, String password)
			{
				// create a new file for the password verification.
				File file = new File(path + "/verify.pass");

				try
					{
						// check if the verification file exists
						if (!file.exists())
							{
								// if not then create it
								file.createNewFile();
							}

						// create a print writer to output file contents
						PrintWriter printWriter = new PrintWriter(file);

						// get the current date and put in appropriate format
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
						String currentDate = sdf.format(new Date());

						// create header in the file
						printWriter.println("Comment: password-verification-" + currentDate);

						// encrypt verification string
						String encryptedText = SymmetricKeyManager.encrypt(key, "POSN - SUCCESS");

						// get the number of lines in the string
						int numLines = countLines(encryptedText);
						printWriter.println("Number of Lines: " + numLines);

						// put the string in the file
						printWriter.println(encryptedText);

						// close the print writer
						printWriter.close();

						return true;
					}
				catch (IOException e)
					{
						e.printStackTrace();
					}
				return false;
			}


		private static int countLines(String str)
			{
				String[] lines = str.split("\r\n|\r|\n");
				return lines.length;
			}
	}
