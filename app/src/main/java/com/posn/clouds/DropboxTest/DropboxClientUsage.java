package com.posn.clouds.DropboxTest;

import android.content.Context;
import android.content.SharedPreferences;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;


public class DropboxClientUsage
	{
		// variable declarations
		public int fileSize;
		public int numberOfFiles;

		public DropboxAPI<AndroidAuthSession> dropboxSession;
		private AppKeyPair appKeyToken;
		//private AccessTokenPair sessionToken;
		String sessionToken;
		private Context context;


		public DropboxClientUsage(Context context)
			{
				// set the activity context
				this.context = context;
			}

		public void initializeDropbox()
			{
			// create the application key token
				appKeyToken = new AppKeyPair("bcwjrrwwyw309ol", "xgu5rpt67mv4k25");
				
				// try to get the session token from the shared preferences
				sessionToken = getDropboxToken();

				// check if the session token was retrieved
				if (sessionToken == null)
					{
						// if no token, then have the user sign in
						dropboxSession = new DropboxAPI<>(new AndroidAuthSession(appKeyToken));
						dropboxSession.getSession().startOAuth2Authentication(context);
					//	dropboxSession.getSession().startAuthentication(context);
					}
				else
					{
						// session token already available
						dropboxSession = new DropboxAPI<AndroidAuthSession>(new AndroidAuthSession(appKeyToken, sessionToken));
					}
			}
		
		public void authenticateDropboxLogin()
			{
				// check if the dropbox session has been created
				if (dropboxSession != null)
					{
						// check if the authentication was successful
						if (dropboxSession.getSession().authenticationSuccessful())
							{
								// finish the authentication
								dropboxSession.getSession().finishAuthentication();
								
								// store the session token in the shared preferences
								           // String accessToken = mDBApi.getSession().getOAuth2AccessToken();

								//saveDropboxToken(dropboxSession.getSession().getAccessTokenPair());
								System.out.println("TOKEN SAVED!!!!");
								saveDropboxToken(dropboxSession.getSession().getOAuth2AccessToken());
								

							}
					}						
			}

		public void downloadFile(String dropboxPath, String outputPath)
			{
				// create a new async task to download the desired file
				new DropboxDownloadAsyncTask(dropboxSession, dropboxPath, outputPath).execute();
			}


		public void uploadFile(String dropboxPath, String inputPath)
			{
				// create a new async task to upload the desired file
				new DropboxUploadAsyncTask(dropboxSession, dropboxPath, inputPath).execute();
			}
		

		public void createDropboxStorageDirectories()
			{
				// create a new async task to check and create directories as needed
				new DropboxCreateDirectoriesAsyncTask(dropboxSession).execute();
			}


		// check to see if the token has already been obtained
		//private AccessTokenPair getDropboxToken()
		private String getDropboxToken()
			{
				// get the shared preferences area for the token
				SharedPreferences sessionTokenRecord = context.getSharedPreferences("token", Context.MODE_PRIVATE);
				
				// get the token key
					String sessionToken = sessionTokenRecord.getString("accessToken", null);

			//	String sessionKey = sessionTokenRecord.getString("sessionKey", null);
				
				// get the token secret
			//String sessionSecret = sessionTokenRecord.getString("sessionSecret", null);
				
				// check if the token key and secret were retrieved successfully
				//if (!(sessionKey == null || sessionSecret == null))
				if (!(sessionToken == null))
					{
					System.out.println("TOKEN FOUND!");
						// return the access token pair
						//return new AccessTokenPair(sessionKey, sessionSecret);
					return sessionToken;
					}
				else
					{
					System.out.println("TOKEN FAILED!");

						// return null
						return null;
					}

			}

		
		//private void saveDropboxToken(AccessTokenPair accessToken)
		private void saveDropboxToken(String accessToken)
			{
				// create a new shared preferences area for the token
				SharedPreferences.Editor tokenRecordEditor = context.getSharedPreferences("token", Context.MODE_PRIVATE).edit();
				
				tokenRecordEditor.putString("accessToken", accessToken);

				
				// put the token key
				//tokenRecordEditor.putString("sessionKey", accessToken.key);
				
				// put the token secret
				//tokenRecordEditor.putString("sessionSecret", accessToken.secret);
				
				// write the preferences to the device
				tokenRecordEditor.commit();
			}
	}
