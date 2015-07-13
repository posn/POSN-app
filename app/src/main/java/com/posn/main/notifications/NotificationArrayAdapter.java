package com.posn.main.notifications;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.posn.R;


class NotificationViewHolder
	{

		TextView notification;
		TextView time;
		ImageView friendImage;
	}


public class NotificationArrayAdapter extends ArrayAdapter<Notification>
	{

		private final Context context;
		private ArrayList<Notification> values;
		ArrayList<Notification> selectedContacts = new ArrayList<Notification>();
		NotificationViewHolder mViewHolder = null;


		public NotificationArrayAdapter(Context context, ArrayList<Notification> values)
			{
				super(context, R.layout.listview_notification_item, values);
				this.context = context;
				this.values = values;
				System.out.println(values.size());
				System.out.println(this.values.size());

			}


		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
			{

				if (convertView == null)
					{
						LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

						convertView = inflater.inflate(R.layout.listview_notification_item, parent, false);

						mViewHolder = new NotificationViewHolder();

						mViewHolder.notification = (TextView) convertView.findViewById(R.id.notification_text);
						mViewHolder.time = (TextView) convertView.findViewById(R.id.time_text);
						mViewHolder.friendImage = (ImageView) convertView.findViewById(R.id.image);
					}
				
				
				Notification notification = values.get(position);
				String notificationMessage, friendName;

				friendName = "<b>" + notification.friendName + "</b>";

				if (notification.type.equals("request"))
					{
						notificationMessage = " wants to be your friend.";
					}
				else if (notification.type.equals("accepted"))
					{
						notificationMessage = " accepted your friend request.";
					}
				else if (notification.type.equals("comment"))
					{
						notificationMessage = " commented on your post.";
					}
				else
					{
						notificationMessage = null;
					}
				mViewHolder.notification.setText(Html.fromHtml(friendName + notificationMessage));

				//SimpleDateFormat ft = new SimpleDateFormat("E MMM dd 'at' hh:mm:ss a", Locale.ENGLISH);

				mViewHolder.time.setText(notification.date);

				return convertView;
			}


		@Override
		public int getCount()
			{
				return values.size();
			}

	}
