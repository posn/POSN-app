package com.posn.main.initial_setup.unused;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.posn.R;


public class SetupProfilePictureActivity extends FragmentActivity implements OnClickListener
	{

		ImageView profilePicture;
		Button choosePhoto, takePhoto, next;
		Uri uri;

		int MY_CALLBACK_ID = 0;
		public static int TAKE_IMAGE = 111;
		public static int PICK_IMAGE = 222;
		Uri mCapturedImageURI;


		@Override
		protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_setup_profile_picture);

				choosePhoto = (Button) findViewById(R.id.choose_photo_button);
				takePhoto = (Button) findViewById(R.id.take_photo_button);

				next = (Button) findViewById(R.id.next_button);
				next.setOnClickListener(this);
				takePhoto.setOnClickListener(this);
				choosePhoto.setOnClickListener(this);

				profilePicture = (ImageView) findViewById(R.id.profile_photo);

				ActionBar actionBar = getActionBar();
				actionBar.setTitle("Set Profile Picture");
			}





		@Override
		public void onClick(View v)
			{
				switch(v.getId())
				{

					case R.id.next_button:

						Intent intent = new Intent(this, SetupFriendsActivity.class);
						startActivity(intent);
						break;

					case R.id.choose_photo_button:

						intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(Intent.createChooser(intent, ""), PICK_IMAGE);

						break;

					case R.id.take_photo_button:
						try
							{
								ContentValues values = new ContentValues();
								mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

								intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

								startActivityForResult(intent, TAKE_IMAGE);
							}
						catch (Exception e)
							{
								Log.e("", "", e);
							}

						break;

				}

			}


		protected void onActivityResult(int requestCode, int resultCode, Intent intent)
			{
				if ((requestCode == TAKE_IMAGE) && (resultCode == RESULT_OK))
					{
						String[] projection = { MediaStore.Images.Media.DATA };
						Cursor cursor = getContentResolver().query(mCapturedImageURI, projection, null, null, null);
						int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();

						String capturedImageFilePath = cursor.getString(column_index_data);
						Bitmap bitmap = BitmapFactory.decodeFile(capturedImageFilePath);

						Matrix matrix = new Matrix();

						ExifInterface exifReader;
						try
							{
								exifReader = new ExifInterface(capturedImageFilePath);
								int orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

								if (orientation == ExifInterface.ORIENTATION_NORMAL)
									{

										// Do nothing. The original image is fine.
									}
								else if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
									{

										matrix.postRotate(90);

									}
								else if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
									{

										matrix.postRotate(180);

									}
								else if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
									{

										matrix.postRotate(270);

									}

								Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);
								Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

								profilePicture.setImageBitmap(rotatedBitmap);

								saveToSD(bitmap);
							}
						catch (IOException e)
							{
								e.printStackTrace();
							}
					}

				if (resultCode != RESULT_CANCELED)
					{
						if (requestCode == PICK_IMAGE)
							{
								Uri selectedImageUri = intent.getData();
								String[] projection = { MediaStore.Images.Media.DATA };
								Cursor cursor = getContentResolver().query(selectedImageUri, projection, null, null, null);
								int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
								cursor.moveToFirst();

								String selectedImageFilePath = cursor.getString(column_index_data);
								Bitmap bitmap = BitmapFactory.decodeFile(selectedImageFilePath);

								Matrix matrix = new Matrix();

								ExifInterface exifReader;
								try
									{
										exifReader = new ExifInterface(selectedImageFilePath);
										int orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

										if (orientation == ExifInterface.ORIENTATION_NORMAL)
											{
												// Do nothing. The original image is fine.
												matrix.postRotate(0);
											}
										else if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
											{

												matrix.postRotate(90);

											}
										else if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
											{

												matrix.postRotate(180);

											}
										else if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
											{

												matrix.postRotate(270);

											}

										Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);
										Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

										profilePicture.setImageBitmap(rotatedBitmap);

									}
								catch (IOException e)
									{
										e.printStackTrace();
									}

							}
					}
			}


		public File saveToSD(Bitmap outputImage)
			{

				File storagePath = new File(Environment.getExternalStorageDirectory() + "/POSN/Photos");

				if (!storagePath.exists())
					storagePath.mkdirs();

				long time = System.currentTimeMillis();

				File myImage = new File(storagePath, Long.toString(time) + ".jpg");

				try
					{
						FileOutputStream out = new FileOutputStream(myImage);
						outputImage.compress(Bitmap.CompressFormat.JPEG, 50, out);
						out.flush();
						out.close();
					}
				catch (Exception e)
					{
						e.printStackTrace();
					}

				ContentValues values = new ContentValues();
				values.put(Images.Media.TITLE, "posn image");
				values.put(Images.Media.DESCRIPTION, " ");
				values.put(Images.Media.DATE_TAKEN, time);
				values.put(Images.ImageColumns.BUCKET_ID, myImage.toString().toLowerCase(Locale.US).hashCode());
				values.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, myImage.getName().toLowerCase(Locale.US));
				values.put("_data", myImage.getAbsolutePath());

				ContentResolver cr = getContentResolver();
				cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

				return myImage;
			}

	}
