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


public class SetupPersonalInfoActivity extends FragmentActivity implements OnClickListener, OnDateSetListener
   {

      EditText firstName, lastName, email, phone;
      TextView birthday;
      Button next;

      //POSNApplication app;

      User user = new User();

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

            // get the application to store data
            //app = (POSNApplication) this.getApplication();
         }


      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {

                  case R.id.next_button:

                     // check and store all the data
                     if (checkFields())
                        {
                           // store the first and last name
                           user.firstName = firstName.getText().toString().trim();
                           user.lastName = lastName.getText().toString().trim();

                           // store the email address
                           user.email = email.getText().toString().trim();

                           // store the phone number
                           user.phoneNumber = phone.getText().toString().trim();

                           // store the birthday
                           user.birthday = birthday.getText().toString().trim();

                           // store the gender
                           RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
                           String radiovalue = ((RadioButton) this.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                           user.gender = radiovalue.trim();

                           // create user ID
                           user.ID = IDGenerator.generate(user.email);

                           Intent intent = new Intent(this, SetupPasswordActivity.class);
                           intent.putExtra("user", user);
                           startActivity(intent);
                        }

                     break;

                  case R.id.textView1:

                     showDateDialog();
                     break;

               }

         }


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


      public void showDateDialog()
         {

            FragmentManager fm = getSupportFragmentManager();
            TimePickerFragment newFragment = new TimePickerFragment(this);
            newFragment.show(fm, "date_picker");

         }


      public boolean checkFields()
         {
            // check the first name
            if (isEmpty(firstName))
               {
                  Toast.makeText(this, "Please enter your first name.", Toast.LENGTH_SHORT).show();
                  return false;
               }

            // check the last name
            if (isEmpty(lastName))
               {
                  Toast.makeText(this, "Please enter your last name.", Toast.LENGTH_SHORT).show();
                  return false;
               }

            // check the email
            if (isEmpty(email))
               {
                  Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show();
                  return false;
               }

            // check the phone number
            if (isEmpty(phone))
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


      private boolean isEmpty(EditText etText)
         {
            if (etText.getText().toString().trim().length() > 0)
               {
                  return false;
               }
            else
               {
                  return true;
               }
         }
   }
