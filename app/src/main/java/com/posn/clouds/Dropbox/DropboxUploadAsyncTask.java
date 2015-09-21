package com.posn.clouds.Dropbox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;


public class DropboxUploadAsyncTask extends AsyncTask<Boolean, Void, Void>
	{
		// declare variables
		String dropboxPath, inputPath;
		DropboxAPI<AndroidAuthSession> dropboxSession;


		public DropboxUploadAsyncTask(DropboxAPI<AndroidAuthSession> dropboxSession, String dropboxPath, String inputPath)
			{
				this.dropboxPath = dropboxPath;
				this.inputPath = inputPath;
				this.dropboxSession = dropboxSession;
			}


		@Override
		protected Void doInBackground(Boolean... arg0)
			{
				Log.i("Dropbox Upload", "Starting Upload");

				// declare variables
				Entry response = null;
				int tries = 3;
				FileInputStream inputStream;

				// get the location of the file to be uploaded
				java.io.File file = new java.io.File(inputPath);

				// try up to three times to upload the file
				while (tries > 0)
					{
						try
							{
								// get the file into memory
								inputStream = new FileInputStream(file);

								// upload the file to dropbox
								response = dropboxSession.putFile(dropboxPath, inputStream, file.length(), null, null);

								// check if the upload was successful
								if(response != null)
									{
										Log.i("Dropbox Upload", "Upload Complete");
										tries = 0;
									}
								else
									{
										Log.i("Dropbox Upload", "Upload Failed");
										tries --;								
									}
								
								// close the stream
								inputStream.close();
							}
						catch (FileNotFoundException e)
							{
								e.printStackTrace();
								Log.i("Dropbox Upload", "File Not Found");
								tries--;
							}

						catch (DropboxException e)
							{
								e.printStackTrace();
								Log.i("Dropbox Upload", "Dropbox Error");
								tries--;
							}
						catch (IOException e)
							{
								e.printStackTrace();
								Log.i("Dropbox Upload", "IO Exception");
								tries--;
							}
					}
				return null;
			}
	}
