package com.posn.utility;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class provides methods to manage and assist with interactions with the user interface
 **/
public class UserInterfaceHelper
   {
      /**
       * Checks if the given edittext contains any data
       *
       * @param editText the editext to be checked
       * @return true if the edittext does not have data, otherwise false
       **/
      public static boolean isEditTextEmpty(EditText editText)
         {
            // check if the length of the text is greater than 0
            return (editText.getText().toString().trim().length() <= 0);
         }

      /**
       * Closes the soft keyboard and clear the focus of the view
       **/
      public static void hideKeyboard(Activity activity)
         {
            // Check if no view has focus:
            View view = activity.getCurrentFocus();
            if (view != null)
               {
                  InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                  inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
               }
         }

      /**
       * Shows a toast message in the activity
       **/
      public static void showToast(Context context, String message)
         {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
         }
   }
