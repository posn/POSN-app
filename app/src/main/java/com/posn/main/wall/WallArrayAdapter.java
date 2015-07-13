package com.posn.main.wall;

import java.util.ArrayList;

import com.posn.main.wall.posts.ListViewPostItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


public class WallArrayAdapter extends ArrayAdapter<ListViewPostItem>
	{

		private LayoutInflater mInflater;

		private ArrayList<ListViewPostItem> values;
		ArrayList<ListViewPostItem> selectedContacts = new ArrayList<ListViewPostItem>();


		public enum PostType {
			LINK_POST_ITEM, PHOTO_POST_ITEM, STATUS_POST_ITEM, VIDEO_POST_ITEM
		}


		public WallArrayAdapter(Context context, ArrayList<ListViewPostItem> values)
			{
				super(context, 0, values);
				mInflater = LayoutInflater.from(context);

				this.values = values;
				System.out.println(values.size());
				System.out.println(this.values.size());

			}


		@Override
		public int getViewTypeCount()
			{
				return PostType.values().length;

			}


		@Override
		public int getItemViewType(int position)
			{
				return getItem(position).getViewType();
			}


		@Override
		public View getView(int position, View convertView, ViewGroup parent)
			{
				return getItem(position).getView(mInflater, convertView);
			}


		@Override
		public boolean isEnabled(int position)
			{
				return false;
			}
	}

/* class ViewHolder {
 * 
 * TextView nameText; TextView emailText; ImageView thumb_image; }
 * 
 * 
 * public class WallArrayAdapter extends ArrayAdapter<Contact> {
 * 
 * private final Context context; private ArrayList<Contact> values; ArrayList<Contact> selectedContacts = new ArrayList<Contact>(); ViewHolder mViewHolder;
 * 
 * 
 * public WallArrayAdapter(Context context, ArrayList<Contact> values) { super(context, R.layout.listview_wall_item, values); this.context = context; this.values = values; System.out.println(values.size()); System.out.println(this.values.size());
 * 
 * }
 * 
 * 
 * @Override public View getView(final int position, View convertView, ViewGroup parent) {
 * 
 * if (convertView == null) {
 * 
 * LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 * 
 * convertView = inflater.inflate(R.layout.listview_wall_item, parent, false);
 * 
 * mViewHolder = new ViewHolder();
 * 
 * mViewHolder.nameText = (TextView) convertView.findViewById(R.id.name); mViewHolder.emailText = (TextView) convertView.findViewById(R.id.email_address); mViewHolder.thumb_image = (ImageView) convertView.findViewById(R.id.image);
 * 
 * convertView.setTag(mViewHolder); } else { mViewHolder = (ViewHolder) convertView.getTag(); }
 * 
 * mViewHolder.nameText.setText(values.get(position).name); mViewHolder.emailText.setText("sample_email@posn.com");
 * 
 * return convertView; }
 * 
 * 
 * @Override public int getCount() { return values.size(); }
 * 
 * 
 * @Override public boolean isEnabled(int position) { return false; }
 * 
 * 
 * public void updateContactList(Contact item) { if (item.selected) { item.selected = false; System.out.println("NOT CHECKED"); selectedContacts.remove(item); } else { item.selected = true; System.out.println("CHECKED"); selectedContacts.add(item); } }
 * 
 * 
 * public void selectAllContacts() { for (int i = 0; i < values.size(); i++) { values.get(i).selected = true; if (!selectedContacts.contains(values.get(i))) selectedContacts.add(values.get(i)); } }
 * 
 * 
 * public void clearSelectedContacts() { for (int i = 0; i < values.size(); i++) { values.get(i).selected = false; } selectedContacts.clear(); } } */
