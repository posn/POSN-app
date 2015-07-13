package com.posn.main.wall.comments;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ListView;

import com.posn.R;
import com.posn.datatypes.Friend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;


public class CommentActivity extends Activity
	{

		// declare variables
		ArrayList<CommentItem> commentList = new ArrayList<CommentItem>();


		@Override
		protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);

				// load the xml file for the logs

				requestWindowFeature(Window.FEATURE_NO_TITLE);
				this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);

				LayoutParams params = this.getWindow().getAttributes();
				params.alpha = 1.0f;
				params.dimAmount = 0.5f;
				this.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				int width = size.x;
				int height = size.y;

				int statusBarHeight = getStatusBarHeight();

				// This sets the window size, while working around the IllegalStateException thrown by ActionBarView
				getWindow().setLayout(width, height - statusBarHeight);
				getWindow().setGravity(Gravity.BOTTOM);

				setContentView(R.layout.activity_post_comment);

				ListView lv = (ListView) findViewById(R.id.commentsListView);

				getComments();
				

				final CommentArrayAdapter adapter = new CommentArrayAdapter(this, commentList);
				lv.setAdapter(adapter);
			}


		public void getComments()
			{
				commentList.clear();
				int count = 0;
				HashSet<String> emlRecsHS = new HashSet<String>();
String comment;
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

										Random rand = new Random();
										int random = rand.nextInt((3 - 1) + 1) + 1;
										comment = "Test Comment!";
										if (random == 1)
											{
												comment = "Happy Birthday!";
											}
										else if (random == 2)
											{
												comment = "Here is a really long sentence that will take multiple lines. Often times, comments will take multiple lines to display.!";
											}
										
										commentList.add(new CommentItem(phoneContact.name, "Jan 19, 2015 at 1:56 pm", comment));
										count++;
									}
							}
						while (cur.moveToNext() && count < 8);
					}

				cur.close();
			}


		public int getStatusBarHeight()
			{
				int result = 0;
				int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
				if (resourceId > 0)
					{
						result = getResources().getDimensionPixelSize(resourceId);
					}
				return result;
			}

	}
