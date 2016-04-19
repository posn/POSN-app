package com.posn.main.wall;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import com.posn.Constants;
import com.posn.R;
import com.posn.adapters.SelectGroupArrayAdapter;
import com.posn.datatypes.UserGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CreateNewPhotoPostActivity extends Activity implements View.OnClickListener
   {
      // declare variables
      ArrayList<UserGroup> userGroupList;
      ArrayList<String> selectedGroups = new ArrayList<>();
      SelectGroupArrayAdapter adapter;
      ImageView imageView;

      private Uri outputFileUri;
      String photopath = null;
      Context context;


      @Override
      public void onSaveInstanceState(Bundle savedInstanceState)
         {
            // Save the user's current game state
            savedInstanceState.putParcelable("outputFileUri", outputFileUri);
            savedInstanceState.putString("photopath", photopath);
            savedInstanceState.putStringArrayList("selectedGroups", selectedGroups);
            savedInstanceState.putParcelableArrayList("userGroupList", userGroupList);

            // Always call the superclass so it can save the view hierarchy state
            super.onSaveInstanceState(savedInstanceState);
         }


      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_new_photo_post);

            userGroupList = getIntent().getExtras().getParcelableArrayList("groups");

            context = this;

            Button postPhotoButton = (Button) findViewById(R.id.post_photo_button);
            Button choosePhotoButton = (Button) findViewById(R.id.choose_photo_button);
            postPhotoButton.setOnClickListener(this);
            choosePhotoButton.setOnClickListener(this);

            imageView = (ImageView) findViewById(R.id.photo_preview);

            // get the listview from the layout
            ListView lv = (ListView) findViewById(R.id.listView1);

            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            lv.setItemsCanFocus(true);

            // get the saved data from when the activity was destroyed
            if (savedInstanceState != null)
               {
                  outputFileUri = savedInstanceState.getParcelable("outputFileUri");
                  photopath = savedInstanceState.getString("photopath");
                  selectedGroups = savedInstanceState.getStringArrayList("selectedGroups");
                  userGroupList = savedInstanceState.getParcelableArrayList("userGroupList");
                  if (photopath != null)
                     {
                        Bitmap photo = loadBitmap(photopath);
                        imageView.setImageBitmap(photo);
                        imageView.invalidate();
                     }
               }

            // create a custom adapter for each contact item in the listview
            adapter = new SelectGroupArrayAdapter(this, userGroupList, selectedGroups);

            // set the adapter to the listview
            lv.setAdapter(adapter);

            // set onItemClick listener
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
               {

                  @Override
                  public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
                     {
                        CheckBox currentCheckBox = (CheckBox) view.findViewById(R.id.checkBox1);
                        currentCheckBox.toggle();

                        UserGroup userGroup = (UserGroup) parent.getItemAtPosition(position);

                        // get the contact that was click and toggle the check box
                        adapter.updateSelectedGroupList(userGroup);
                     }

               });
         }


      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.post_photo_button:
                     Intent resultIntent = new Intent();
                     resultIntent.putExtra("photopath", photopath);
                     resultIntent.putStringArrayListExtra("groups", selectedGroups);
                     setResult(Activity.RESULT_OK, resultIntent);
                     finish();
                     break;

                  case R.id.choose_photo_button:
                     openImageIntent();
                     break;
               }
         }

      @Override
      public void onActivityResult(int requestCode, int resultCode, Intent data)
         {
            if (requestCode == Constants.RESULT_PHOTO && resultCode == Activity.RESULT_OK)
               {
                  final boolean isCamera;
                  if (data == null || data.getData() == null)
                     {
                        isCamera = true;
                     }
                  else
                     {
                        final String action = data.getAction();
                        isCamera = action != null && action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                     }

                  if (!isCamera)
                     {
                        outputFileUri = data.getData();
                     }

                  // get the path from the URI
                  String[] projection = {MediaStore.Images.Media.DATA};
                  Cursor cursor = getContentResolver().query(outputFileUri, projection, null, null, null);
                  int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                  cursor.moveToFirst();


                  photopath = cursor.getString(column_index_data);
                  System.out.println("PATH: " + photopath);

                  // check if the photo needs to be rotated first
                  Bitmap photo = BitmapFactory.decodeFile(photopath);
                  Matrix matrix = new Matrix();

                  ExifInterface exifReader;
                  try
                     {
                        exifReader = new ExifInterface(photopath);
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
                        //photo = Bitmap.createScaledBitmap(photo, photo.getWidth() / 2, photo.getHeight() / 2, true);
                        photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);

                        FileOutputStream fOut;

                        fOut = new FileOutputStream(photopath);
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                     }
                  catch (IOException e)
                     {
                        e.printStackTrace();
                     }


                  // load the bitmap and resize it
                  photo = loadBitmap(photopath);

                  imageView.setImageBitmap(photo);
                  //photo.recycle();


               }

         }

      private void openImageIntent()
         {
            // set the folder path
            final File root = new File(Constants.multimediaFilePath);
            final String filename = "posn_" + Integer.toString((int) System.currentTimeMillis() / 1000) + ".jpg";
            final File sdImageMainDirectory = new File(root, filename);

            // outputFileUri = Uri.fromFile(sdImageMainDirectory);
            ContentValues values = new ContentValues();
            outputFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // Camera.
            final List<Intent> cameraIntents = new ArrayList<>();
            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            final PackageManager packageManager = getPackageManager();
            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCam)
               {
                  final String packageName = res.activityInfo.packageName;
                  final Intent intent = new Intent(captureIntent);
                  intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                  intent.setPackage(packageName);
                  intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                  cameraIntents.add(intent);
               }

            // Filesystem.
            final Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_PICK);

            // Chooser of filesystem options.
            final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

            // Add the camera options.
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

            startActivityForResult(chooserIntent, Constants.RESULT_PHOTO);
         }


      public Bitmap loadBitmap(String filename)
         {
            //Bitmap bitmap = BitmapFactory.decodeFile(filename);
            Bitmap photo;
            File file = new File(filename);
            int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
            if (file_size > 2048)
               {
                  BitmapFactory.Options options = new BitmapFactory.Options();
                  options.inSampleSize = 4;
                  photo = BitmapFactory.decodeFile(filename, options);
               }
            else if (file_size > 1024)
               {
                  BitmapFactory.Options options = new BitmapFactory.Options();
                  options.inSampleSize = 2;
                  photo = BitmapFactory.decodeFile(filename, options);
               }
            else
               photo = BitmapFactory.decodeFile(filename);

            Matrix matrix = new Matrix();

            ExifInterface exifReader;
            try
               {
                  exifReader = new ExifInterface(photopath);
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
                  //photo = Bitmap.createScaledBitmap(photo, photo.getWidth() / 2, photo.getHeight() / 2, true);
                  photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);

            return photo;

         }

      catch(
      IOException e
      )

      {
         e.printStackTrace();
      }

      return null;
   }

}
