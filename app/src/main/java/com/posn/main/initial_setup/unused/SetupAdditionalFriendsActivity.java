package com.posn.main.initial_setup.unused;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.posn.R;
import com.posn.datatypes.Friend;
import com.posn.main.main.MainActivity;

import java.util.ArrayList;


public class SetupAdditionalFriendsActivity extends FragmentActivity implements OnClickListener
	{

		// declare variables
		Button add, next, skip;
		EditText name, email;
		ListView lv;
		ArrayList<Friend> contactList = new ArrayList<Friend>();


		@Override
		protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

				// get the XML layout
				setContentView(R.layout.activity_setup_additional_friends);

				// get the listview from the layout
				lv = (ListView) findViewById(R.id.listView1);

				// get the EditText from the layout
				name = (EditText) findViewById(R.id.name_text);
				email = (EditText) findViewById(R.id.email_text);

				// get the buttons from the layout
				add = (Button) findViewById(R.id.add_button);
				next = (Button) findViewById(R.id.next_button);
				skip = (Button) findViewById(R.id.skip_button);

				// set onclick listener for each button
				next.setOnClickListener(this);
				add.setOnClickListener(this);
				skip.setOnClickListener(this);

				// get all the phone contacts.

				lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				lv.setItemsCanFocus(true);

				// create a custom adapter for each contact item in the listview
				final ContactArrayAdapter adapter = new ContactArrayAdapter(this, contactList);

				// set the adapter to the listview
				lv.setAdapter(adapter);

				// set onItemClick listener
				lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
					{

						@Override
						public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
							{
								// get the contact that was click and toggle the check box
								final Friend item = (Friend) parent.getItemAtPosition(position);
								adapter.updateContactList(item);

								// refresh the listview
								adapter.notifyDataSetChanged();
							}

					});

				// get the action bar and set the title
				ActionBar actionBar = getActionBar();
				actionBar.setTitle("Add Additional Friends");

			}


		@Override
		public void onClick(View v)
			{
				switch(v.getId())
				{

					case R.id.next_button:

						Intent intent = new Intent(this, MainActivity.class);
						startActivity(intent);
						break;

					case R.id.add_button:
						ContactArrayAdapter adapter = (ContactArrayAdapter) lv.getAdapter();

						Friend newContact = new Friend();

						if (!isEmpty(name) && !isEmpty(email))
							{
								newContact.name = name.getText().toString();
								newContact.email = email.getText().toString();
								newContact.selected = true;

								adapter.add(newContact);
								adapter.notifyDataSetChanged();

								name.getText().clear();
								email.getText().clear();
							}
						else
							{
								Toast.makeText(this, "You Must Enter the Name and Email", Toast.LENGTH_SHORT).show();
							}
						break;

					case R.id.skip_button:
						break;

				}

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
