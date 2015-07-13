package com.posn.main.notifications;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.posn.R;
import com.posn.application.POSNApplication;


public class UserNotificationsFragment extends Fragment implements OnClickListener
	{

		// declare variables
		Context context;
		ArrayList<Notification> notificationsList = new ArrayList<Notification>();
		ListView lv;
		POSNApplication app;


		@Override
		public void onResume()
			{
				super.onResume();
			}


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);

				// load the system tab layout
				View view = inflater.inflate(R.layout.fragment_user_notifications, container, false);
				context = getActivity();
				
				// get the application
				app = (POSNApplication) getActivity().getApplication();

				// get the listview from the layout
				lv = (ListView) view.findViewById(R.id.listView1);

				// fill with fake data
				createNotifications();
				loadNotifications();
				
				final NotificationArrayAdapter adapter = new NotificationArrayAdapter(getActivity(), notificationsList);
				lv.setAdapter(adapter);

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
		public void onClick(View arg0)
			{
			}


		
		public void createNotifications()
			{
					JSONArray notifications = new JSONArray();

					JSONObject notification = new JSONObject();
					try {
						notification.put("type", "comment");
						notification.put("friend", "Allen Rice");
						notification.put("date", "Jan 19, 2015 at 1:45 pm");
						notification.put("frienduri", "This is a test post from a file.");
						notifications.put(notification);
						
						notification = new JSONObject();
						notification.put("type", "accepted");
						notification.put("friend", "Cam Rowe");
						notification.put("date", "Jan 19, 2015 at 1:45 pm");
						notification.put("frienduri", "This is a test post from a file.");
						notifications.put(notification);
						
						notification = new JSONObject();
						notification.put("type", "request");
						notification.put("friend", "Daniel Chavez");
						notification.put("date", "Jan 19, 2015 at 1:45 pm");
						notification.put("frienduri", "This is a test post from a file.");
						notifications.put(notification);
						
						
						JSONObject studentsObj = new JSONObject();
						studentsObj.put("notifications", notifications);
						
						String jsonStr = studentsObj.toString();

						FileWriter fw = new FileWriter(app.wallFilePath + "/user_notifications.txt");
				    BufferedWriter bw = new BufferedWriter(fw);
				    bw.write(jsonStr);
				    bw.close();

					} catch (JSONException e) {
					    e.printStackTrace();
					}
					catch (IOException e)
						{
							e.printStackTrace();
						}
			
			}
		
		
		
		public void loadNotifications()
			{
				notificationsList.clear();
						System.out.println("GETTING NOTIFICATIONS!!!");
						
						File notificationFile = new File(app.wallFilePath + "/user_notifications.txt");

						String line, fileContents;

						// open the file
						try
							{
								BufferedReader br = new BufferedReader(new FileReader(notificationFile));

								StringBuilder sb = new StringBuilder();
								while ((line = br.readLine()) != null)
									sb.append(line);

								br.close();
								fileContents = sb.toString();
							
								JSONObject data = new JSONObject(fileContents);
								
			          JSONArray notifications = data.getJSONArray("notifications");
			          
			          data = new JSONObject();
			          
								for (int n = 0; n < notifications.length(); n++)
									{
										Notification notification = new Notification();

										data = notifications.getJSONObject(n);
										
										// get the post type
										notification.type = data.getString("type");

										// get the friend name
										notification.friendName = data.getString("friend");

										// get the post date
										notification.date =  data.getString("date");

										// get the post data
										notification.friendImageUri = data.getString("frienduri");
										
										notificationsList.add(notification);
									}
							}
						catch (FileNotFoundException e)
							{
								e.printStackTrace();
							}
						catch (IOException e)
							{
								e.printStackTrace();
							}
						catch (JSONException e)
							{
								e.printStackTrace();
							}
					
			}
		
		/*
		public void createData()
			{
				HashSet<String> emlRecsHS = new HashSet<String>();
				int count = 0;
				ContentResolver cr = getActivity().getContentResolver();
				String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_ID, ContactsContract.CommonDataKinds.Email.DATA, ContactsContract.CommonDataKinds.Photo.CONTACT_ID };
				String order = "CASE WHEN " + ContactsContract.Contacts.DISPLAY_NAME + " NOT LIKE '%@%' THEN 1 ELSE 2 END, " + ContactsContract.Contacts.DISPLAY_NAME + ", " + ContactsContract.CommonDataKinds.Email.DATA + " COLLATE NOCASE";
				String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
				Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
				if (cur.moveToFirst())
					{
						do
							{
								Notification notification = new Notification();

								// names comes in hand sometimes
								notification.friendName = cur.getString(1);

								if (emlRecsHS.add(notification.friendName))
									{
										notification.dateTime = new Date();

										Random rand = new Random();
										int random = rand.nextInt((3 - 1) + 1) + 1;

										if (random == 1)
											{
												notification.type = "FRIEND_REQUEST";
											}
										else if (random == 2)
											{
												notification.type = "ACCEPTED_FRIEND_REQUEST";
											}
										else if (random == 3)
											{
												notification.type = "COMMENT";
											}

										notificationsList.add(notification);
										count++;
									}

							}
						while (cur.moveToNext() && count < 10);
					}

				cur.close();
			}*/
	}
