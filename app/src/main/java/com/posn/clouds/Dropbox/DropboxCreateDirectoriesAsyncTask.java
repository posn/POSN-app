package com.posn.clouds.Dropbox;

import android.os.AsyncTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;


public class DropboxCreateDirectoriesAsyncTask extends AsyncTask<Boolean, Void, Void>
	{

		// declare variables
		DropboxAPI<AndroidAuthSession> dropboxSession;


		public DropboxCreateDirectoriesAsyncTask(DropboxAPI<AndroidAuthSession> dropboxSession)
			{
				this.dropboxSession = dropboxSession;
			}


		@Override
		protected Void doInBackground(Boolean... arg0)
			{
				// check for archive directory
				try
					{
						// check if directory exists
						dropboxSession.metadata("/archive", 1, null, false, null);
					}
				catch (DropboxException e)
					{
						try
							{
								// create folder if it does not exist
								dropboxSession.createFolder("archive");
							}
						catch (DropboxException e1)
							{
								e1.printStackTrace();
							}
					}

				// check for encryption key directory
				try
					{
						// check if directory exists
						dropboxSession.metadata("/keys", 1, null, false, null);
					}
				catch (DropboxException e)
					{
						try
							{
								// create folder if it does not exist
								dropboxSession.createFolder("keys");
							}
						catch (DropboxException e1)
							{
								e1.printStackTrace();
							}
					}

				// check for multimedia directory
				try
					{
						// check if directory exists
						dropboxSession.metadata("/multimedia", 1, null, false, null);
					}
				catch (DropboxException e)
					{
						try
							{
								// create folder if it does not exist
								dropboxSession.createFolder("multimedia");
							}
						catch (DropboxException e1)
							{
								e1.printStackTrace();
							}
					}

				// check for profile directory
				try
					{
						// check if directory exists
						dropboxSession.metadata("/profile", 1, null, false, null);
					}
				catch (DropboxException e)
					{
						try
							{
								// create folder if it does not exist
								dropboxSession.createFolder("profile");
							}
						catch (DropboxException e1)
							{
								e1.printStackTrace();
							}
					}

				// check for wall directory
				try
					{
						// check if directory exists
						dropboxSession.metadata("/wall", 1, null, false, null);
					}
				catch (DropboxException e)
					{
						try
							{
								// create folder if it does not exist
								dropboxSession.createFolder("wall");
							}
						catch (DropboxException e1)
							{
								e1.printStackTrace();
							}
					}

				return null;
			}


		protected void onPostExecute(Void result)
			{

			}

	}
