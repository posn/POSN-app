package com.posn.initial_setup.unused;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.posn.R;
import com.posn.datatypes.Friend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;


public class SetupFriendsActivity extends FragmentActivity implements OnClickListener
	{

		Button selectAll, clearSelected, next;
		ListView lv;

		ArrayList<Friend> contactList = new ArrayList<Friend>();


		@Override
		protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_setup_friends);

				lv = (ListView) findViewById(R.id.listView1);

				selectAll = (Button) findViewById(R.id.select_all_button);
				clearSelected = (Button) findViewById(R.id.clear_selected_button);
				next = (Button) findViewById(R.id.next_button);

				next.setOnClickListener(this);
				selectAll.setOnClickListener(this);
				clearSelected.setOnClickListener(this);

				getNameEmailDetails();

				lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				lv.setItemsCanFocus(true);

				final ContactArrayAdapter adapter = new ContactArrayAdapter(this, contactList);
				lv.setAdapter(adapter);

				lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
					{

						@Override
						public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
							{
								final Friend item = (Friend) parent.getItemAtPosition(position);
								adapter.updateContactList(item);

								adapter.notifyDataSetChanged();
							}

					});
				
				ActionBar actionBar = getActionBar();
				actionBar.setTitle("Add Friends from Contacts");

			}


		@Override
		public void onClick(View v)
			{
				switch (v.getId())
				{

				case R.id.next_button:

					Intent intent = new Intent(this, SetupAdditionalFriendsActivity.class);
					startActivity(intent);
					break;

				case R.id.select_all_button:
					ContactArrayAdapter adapter = (ContactArrayAdapter) lv.getAdapter();
					adapter.selectAllContacts();
					adapter.notifyDataSetChanged();
					break;

				case R.id.add_button:
					adapter = (ContactArrayAdapter) lv.getAdapter();
					adapter.clearSelectedContacts();
					adapter.notifyDataSetChanged();
					break;

				}

			}


		public void getNameEmailDetails()
			{
				HashSet<String> emlRecsHS = new HashSet<String>();

				ContentResolver cr = this.getContentResolver();
				String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_ID, ContactsContract.CommonDataKinds.Email.DATA, ContactsContract.CommonDataKinds.Photo.CONTACT_ID };
				String order = "CASE WHEN " + ContactsContract.Contacts.DISPLAY_NAME + " NOT LIKE '%@%' THEN 1 ELSE 2 END, " + ContactsContract.Contacts.DISPLAY_NAME + ", " + ContactsContract.CommonDataKinds.Email.DATA + " COLLATE NOCASE";
				String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
				Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
				if (cur.moveToFirst())
					{
						do
							{
								Friend phoneContact = new Friend();
								// names comes in hand sometimes
								phoneContact.name = cur.getString(1);
								phoneContact.phone = cur.getString(2);
								
								phoneContact.email = cur.getString(3);

								// keep unique only
								if (emlRecsHS.add(phoneContact.email.toLowerCase(Locale.ENGLISH)))
									{
										phoneContact.email = "sample_email@posn.com";

										contactList.add(phoneContact);
									}
							}
						while (cur.moveToNext());
					}

				cur.close();
			}

	}
