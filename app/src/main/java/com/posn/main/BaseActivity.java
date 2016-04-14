package com.posn.main;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.posn.application.POSNApplication;
import com.posn.clouds.CloudProvider;


public abstract class BaseActivity extends FragmentActivity
   {
      public CloudProvider cloud = null;

      public POSNApplication app;


      @Override
      protected void onResume()
         {
            super.onResume();

            app = (POSNApplication) getApplication();

            cloud = app.cloud;

            if(cloud != null)
               {
                  cloud.onResume();
               }
         }


      @Override
      protected void onActivityResult(int requestCode, int resultCode, Intent data)
         {
            super.onActivityResult(requestCode, resultCode, data);

            cloud.activityResult(requestCode, resultCode, data);
         }


   }
