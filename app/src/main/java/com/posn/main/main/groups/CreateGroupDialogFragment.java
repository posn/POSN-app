package com.posn.main.main.groups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.posn.R;
import com.posn.main.main.groups.asynctasks.NewGroupAsyncTask;
import com.posn.main.main.MainActivity;


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
                     if (!isEmpty(groupText))
                        {
                           String value = groupText.getText().toString();
                           MainActivity activity = (MainActivity) getActivity();


                           new NewGroupAsyncTask(activity, value).execute();

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

      private boolean isEmpty(EditText etText)
         {
            // check if the length of the text is greater than 0
            return (etText.getText().toString().trim().length() <= 0);
         }
   }
