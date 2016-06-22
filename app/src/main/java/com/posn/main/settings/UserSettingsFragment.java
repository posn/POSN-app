package com.posn.main.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.posn.R;


public class UserSettingsFragment extends Fragment implements OnClickListener
   {

      // declare variables
      Context context;
      private int fragNum;

      public void setFragNum(int postition)
         {
            fragNum = postition;
         }


      @Override
      public void onResume()
         {
            super.onResume();
         }


      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            // load the settings tab layout
            View view = inflater.inflate(R.layout.fragment_user_settings, container, false);
            context = getActivity();

            return view;
         }


      @Override
      public void onActivityCreated(Bundle savedInstanceState)
         {
            super.onActivityCreated(savedInstanceState);
            onResume();
         }


      @Override
      public void onAttach(Activity activity)
         {
            super.onAttach(activity);
            context = getActivity();
         }


      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {

               }
         }
   }
