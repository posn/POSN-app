package com.posn.main.main.groups;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.posn.R;
import com.posn.utility.UserInterfaceHelper;


public class CreateGroupDialogFragment extends DialogFragment
   {
      Button positiveButton, negativeButton;
      private EditText groupText;

      @Override
      public Dialog onCreateDialog(Bundle savedInstanceState)
         {
            AlertDialog.Builder createProjectAlert = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_new_group_fragment, null);

            groupText = (EditText) view.findViewById(R.id.category);

            positiveButton = (Button) view.findViewById(R.id.positive_button);
            negativeButton = (Button) view.findViewById(R.id.negative_button);

            createProjectAlert.setView(view);

            positiveButton.setOnClickListener(new View.OnClickListener()
            {
               @Override
               public void onClick(View v)
                  {
                     if (!UserInterfaceHelper.isEditTextEmpty(groupText))
                        {
                           String newGroupName = groupText.getText().toString();

                           Intent resultIntent = new Intent();
                           resultIntent.putExtra("newGroupName", newGroupName);

                           getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, resultIntent);

                           dismiss();
                        }
                     else
                        {
                           Toast.makeText(getActivity(), "Group name cannot be blank", Toast.LENGTH_SHORT).show();
                        }
                  }
            });

            negativeButton.setOnClickListener(new View.OnClickListener()
            {
               @Override
               public void onClick(View v)
                  {
                     dismiss();
                  }
            });


            AlertDialog dialog = createProjectAlert.create();

            dialog.show();
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextSize(17);
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(17);


            return dialog;

         }
   }
