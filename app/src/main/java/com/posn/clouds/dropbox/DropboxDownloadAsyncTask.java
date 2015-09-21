package com.posn.clouds.dropbox;

import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.android.AndroidAuthSession;

import java.io.FileOutputStream;


public class DropboxDownloadAsyncTask extends AsyncTask<Boolean, Void, Void>
	{

		// declare variables
		String dropboxPath, outputPath;
		DropboxAPI<AndroidAuthSession> dropboxSession;


		public DropboxDownloadAsyncTask(DropboxAPI<AndroidAuthSession> dropboxSession, String dropboxPath, String outputPath)
			{
				this.dropboxPath = dropboxPath;
				this.outputPath = outputPath;
				this.dropboxSession = dropboxSession;
			}


		@Override
		protected Void doInBackground(Boolean... arg0)
			{
				Log.i("Dropbox Download", "Starting Download");

				// declare variables
				int tries = 3;
				FileOutputStream outputStream;

				// get the location of the file to be downloaded
				java.io.File f = new java.io.File(outputPath);

				// try up to three times to download the file
				while (tries > 0)
					{
						try
							{
								// create a stream to put the file onto the device
								outputStream = new FileOutputStream(f);

								// download the file from dropbox
								DropboxFileInfo info = dropboxSession.getFile(dropboxPath, null, outputStream, null);

								// check if the download was successful
								if (info != null)
									{
										tries = 0;
										Log.i("Dropbox Download", "Download Complete");
									}
								else
									{
										tries--;
										Log.i("Dropbox Download", "Download Failed");
									}

								// close the stream
								outputStream.close();
							}
						catch (Exception e)
							{
								e.printStackTrace();
								Log.i("Dropbox Download", "Exception");
								tries--;
							}
					}
				return null;
			}
	}
