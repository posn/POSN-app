package com.posn.application;

import android.app.Application;
import android.os.Environment;

import com.posn.clouds.CloudProvider;


public class POSNApplication extends Application
   {
      public CloudProvider cloud = null;

      // storage directory paths
      public String multimediaFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/multimedia";
      public String wallFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/wall";
      public String messagesFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/messages";

      // profile information data
      @Override
      public void onCreate()
         {
            super.onCreate();
         }
   }
