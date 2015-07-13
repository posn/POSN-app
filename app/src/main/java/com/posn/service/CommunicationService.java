package com.posn.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;


public class CommunicationService extends Service
	{


		Thread thread;


		@Override
		public void onCreate()
			{


			}


		@Override
		public int onStartCommand(Intent intent, int flags, int startId)
			{
				// load in the personal data
				thread = new Thread(new Runnable() {
					public void run() {
						
					}
				});
				
				thread.start();


				// If we get killed, after returning from here, restart
				return START_NOT_STICKY;
			}


		@Override
		public IBinder onBind(Intent intent)
			{
				// We don't provide binding, so return null
				return null;
			}


		@Override
		public void onDestroy()
			{
				Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
			}
	}
