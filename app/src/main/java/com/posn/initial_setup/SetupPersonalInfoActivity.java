package com.posn.initial_setup;

import android.app.ActionBar;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.posn.R;
import com.posn.datatypes.User;
import com.posn.utility.IDGenerator;
import com.posn.utility.UserInterfaceManager;

/**
 * This activity class implements the functionality to get the personal information from a new user
 * Personal Info: first name, last name, email address, phone number, birthday, gender
 **/
public class SetupPersonalInfoActivity extends FragmentActivity implements OnClickListener, OnDateSetListener
   {
      // user interface variables
      EditText firstName, lastName, email, phone;
      TextView birthday;
      Button next;

      // user object to store the information about the new user
      User newUser;

      /**
       * This method is called when the activity needs to be created and handles setting up the user interface objects and sets listeners for touch events.
       **/
      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_setup_personal_info);

            // get the edit texts from the layout
            firstName = (EditText) findViewById(R.id.firstname_text);
            lastName = (EditText) findViewById(R.id.lastname_text);
            email = (EditText) findViewById(R.id.email_text);
            phone = (EditText) findViewById(R.id.phone_text);

            // get the textview from the layout
            birthday = (TextView) findViewById(R.id.textView1);

            // set listeners for the birthday textview
            birthday.setOnClickListener(this);
            birthday.setOnFocusChangeListener(new OnFocusChangeListener()
               {
                  @Override
                  public void onFocusChange(View v, boolean hasFocus)
                     {
                        if (hasFocus)
                           {
                              showDateDialog();
                           }
                     }
               });

            // get the button from the layout
            next = (Button) findViewById(R.id.next_button);
            next.setOnClickListener(this);

            // set the action bar title
            ActionBar actionBar = getActionBar();
            if (actionBar != null)
               {
                  actionBar.setTitle("Setup Account Information");
               }
         }


      /**
       * This method is called when the user touches a UI element and gives the element its functionality
       **/
      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.next_button:

                     // check and store all the data
                     if (checkFields())
                        {
                           // create a new user
                           newUser = new User();

                           // create user ID
                           newUser.ID = IDGenerator.generate(newUser.email);

                           // store the first and last name
                           newUser.firstName = firstName.getText().toString().trim();
                           newUser.lastName = lastName.getText().toString().trim();

                           // store the email address
                           newUser.email = email.getText().toString().trim();

                           // store the phone number
                           newUser.phoneNumber = phone.getText().toString().trim();

                           // store the birthday
                           newUser.birthday = birthday.getText().toString().trim();

                           // store the gender
                           RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
                           String radioValue = ((RadioButton) this.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                           newUser.gender = radioValue.trim();

                           // launch a new activity to create the password
                           Intent intent = new Intent(this, SetupPasswordActivity.class);
                           intent.putExtra("user", newUser);
                           startActivity(intent);
                        }
                     break;

                  case R.id.textView1:

                     showDateDialog();
                     break;

               }

         }


      /**
       * This method is called when the user selects his/her birthday and formats the date
       **/
      @Override
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
         {
            String year1 = String.valueOf(year);
            String month1 = String.valueOf(monthOfYear + 1);
            if (monthOfYear < 10)
               month1 = "0" + month1;
            String day1 = String.valueOf(dayOfMonth);
            if (dayOfMonth < 10)
               day1 = "0" + day1;
            String birthdayText = month1 + "/" + day1 + "/" + year1;
            birthday.setText(birthdayText);
            birthday.clearFocus();
         }


      /**
       * This method shows the date picker dialog window
       **/
      public void showDateDialog()
         {
            FragmentManager fm = getSupportFragmentManager();
            TimePickerFragment newFragment = new TimePickerFragment(this);
            newFragment.show(fm, "date_picker");
         }


      /**
       * This method checks to see if the required fields have been filled out. An error message will be displayed if one is missing
       **/
      public boolean checkFields()
         {
            // check the first name
            if (UserInterfaceManager.isEditTextEmpty(firstName))
               {
                  Toast.makeText(this, "Please enter your first name.", Toast.LENGTH_SHORT).show();
                  return false;
               }

            // check the last name
            if (UserInterfaceManager.isEditTextEmpty(lastName))
               {
                  Toast.makeText(this, "Please enter your last name.", Toast.LENGTH_SHORT).show();
                  return false;
               }

            // check the email
            if (UserInterfaceManager.isEditTextEmpty(email))
               {
                  Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show();
                  return false;
               }

            // check the phone number
            if (UserInterfaceManager.isEditTextEmpty(phone))
               {
                  Toast.makeText(this, "Please enter your phone number.", Toast.LENGTH_SHORT).show();
                  return false;
               }

            // check the birthday
            if (birthday.getText().toString().trim().length() <= 0)
               {
                  Toast.makeText(this, "Please enter your birthday.", Toast.LENGTH_SHORT).show();
                  return false;
               }

            return true;
         }
   }
