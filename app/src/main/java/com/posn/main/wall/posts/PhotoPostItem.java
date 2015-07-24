package com.posn.main.wall.posts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Post;
import com.posn.main.wall.PhotoViewerActivity;
import com.posn.main.wall.WallArrayAdapter.PostType;
import com.posn.main.wall.comments.CommentActivity;
import com.posn.main.wall.views.SquareImageView;


public class PhotoPostItem implements ListViewPostItem, OnClickListener
   {
      static class ViewHolderItem
         {
            TextView nameText;
            TextView dateText;
            SquareImageView photoImage;

            RelativeLayout commentButton;
            RelativeLayout shareButton;
         }

      private Context context;
      private String friendName;
      private Post postData;
      private String directory;
      ViewHolderItem viewHolder;

     // SquareImageView photoImage;
      int finalHeight, finalWidth;


      public PhotoPostItem(Context context, String friendName, Post postData, String directory)
         {
            this.context = context;
            this.postData = postData;
            this.directory = directory + "/";
            this.friendName = friendName;
         }


      @Override
      public int getViewType()
         {
            return PostType.PHOTO_POST_ITEM.ordinal();
         }


      @Override
      public View getView(LayoutInflater inflater, View convertView)
         {
            //  View view = convertView;

            if (convertView == null)
               {
                  // inflate the layout
                  convertView = inflater.inflate(R.layout.listview_wall_photo_item, null);

                  // well set up the ViewHolder
                  viewHolder = new ViewHolderItem();
                  viewHolder.nameText = (TextView) convertView.findViewById(R.id.name);
                  viewHolder.dateText = (TextView) convertView.findViewById(R.id.date);
                  viewHolder.photoImage = (SquareImageView) convertView.findViewById(R.id.photo);

                  viewHolder.commentButton = (RelativeLayout) convertView.findViewById(R.id.comment_button);
                  viewHolder.shareButton = (RelativeLayout) convertView.findViewById(R.id.share_button);

                  viewHolder.commentButton.setOnClickListener(this);
                  viewHolder.shareButton.setOnClickListener(this);
                  viewHolder.photoImage.setOnClickListener(this);

                  // store the holder with the view.
                  convertView.setTag(viewHolder);
               }
            else
               {
                  viewHolder = (ViewHolderItem) convertView.getTag();
               }

            // set the data into the views
            viewHolder.nameText.setText(friendName);
            viewHolder.dateText.setText(postData.date);
/*
            ViewTreeObserver vto = viewHolder.photoImage.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
            {

               public boolean onPreDraw()
                  {
                     // Remove after the first run so it doesn't fire forever
                     viewHolder.photoImage.getViewTreeObserver().removeOnPreDrawListener(this);
                     finalHeight = viewHolder.photoImage.getMeasuredHeight();
                     finalWidth = viewHolder.photoImage.getMeasuredWidth();

                     File imgFile = new File(directory + postData.content);

                     Bitmap photo = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                     int w = photo.getWidth();
                     int h = photo.getHeight();

                     while (w > finalWidth || h > finalHeight)
                        {
                           w = w / 2;
                           h = h / 2;
                        }
                     System.out.println("ASD " + w + "asdadd: " + h);

                     viewHolder.photoImage.setImageBitmap(Bitmap.createScaledBitmap(photo, w, h, false));

                     return true;
                  }
            });
*/
            return convertView;
         }


      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.comment_button:

                     // launch comment activity
                     Intent intent = new Intent(context, CommentActivity.class);
                     context.startActivity(intent);

                     break;

                  case R.id.share_button:
                     break;

                  case R.id.photo:

                     intent = new Intent(context, PhotoViewerActivity.class);
                     intent.putExtra("photoPath", directory + postData.content);

                     context.startActivity(intent);
                     break;
               }
         }
   }
