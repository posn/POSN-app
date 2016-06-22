package com.posn.application;

import android.app.Application;

import com.posn.clouds.CloudProvider;


public class POSNApplication extends Application
   {
      public CloudProvider cloud = null;

      // profile information data
      @Override
      public void onCreate()
         {
            super.onCreate();
         }
   }
