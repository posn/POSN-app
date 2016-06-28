package com.posn.main.initial_setup;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;


/**
 * This fragment class implements the functionality to show the date and time picker dialog
 * Used in the SetupPersonalInfoActivity
 **/
public class TimePickerFragment extends DialogFragment
   {

      private OnDateSetListener listener;

      public TimePickerFragment()
         {
         }

      public TimePickerFragment(OnDateSetListener listener)
         {
            this.listener = listener;
         }


      @Override
      public Dialog onCreateDialog(Bundle savedInstanceState)
         {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // create a new date picker dialog
            DatePickerDialog datePicker = new DatePickerDialog(getActivity(), listener, year, month, day);

            // set the max date to current date
            datePicker.getDatePicker().setMaxDate(c.getTimeInMillis());

            // Create a new instance of TimePickerDialog and return it
            return datePicker;
         }

   }
