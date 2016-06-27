package com.posn.main.wall;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import com.posn.main.groups.SelectGroupArrayAdapter;
import com.posn.datatypes.UserGroup;
import com.posn.utility.ImageManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * This activity class implements the functionality for a user to create a new photo post
 * <ul><li>The user has the option to take a photo from the camera or load one from the gallery
 * <li>The user must selected which group(s) to share the photo with
 * <li>The new post object is returned the main activity through an activity result</ul>
 **/
public class CreateNewPhotoPostActivity extends Activity implements View.OnClickListener
   {
      // declare variables
      ArrayList<UserGroup> userGroupList;
      ArrayList<String> selectedGroups = new ArrayList<>();
      SelectGroupArrayAdapter adapter;
      ImageView imageView;

      private Uri outputFileUri;
      String photopath = null;


      /**
       * This method is called when the activity is stopped and saves the current data values (required for taking a photo with the camera)
       **/
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


      /**
       * This method is called when the activity needs to be created and handles the interface elements.
       * <ul><li>Sets up the list view for groups and add listeners for buttons</ul>
       **/
      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_new_photo_post);

            // get the user defined groups from the wall fragment
            userGroupList = getIntent().getExtras().getParcelableArrayList("groups");

            // get the buttons from the layout and set listeners
            Button postPhotoButton = (Button) findViewById(R.id.post_photo_button);
            Button choosePhotoButton = (Button) findViewById(R.id.choose_photo_button);
            postPhotoButton.setOnClickListener(this);
            choosePhotoButton.setOnClickListener(this);

            // get the image view from the layout
            imageView = (ImageView) findViewById(R.id.photo_preview);

            // get the listview from the layout and set it up
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
                        try
                           {
                              Bitmap photo = ImageManager.loadBitmap(photopath);
                              imageView.setImageBitmap(photo);
                              imageView.invalidate();
                           }
                        catch (IOException e)
                           {
                              e.printStackTrace();
                           }
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


      /**
       * This method is called when the user clicks the different user interface elements and implements each element's functionality
       **/
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


      /**
       * This method is called after the user selected a photo from the gallery or captured one with the camera
       **/
      @Override
      public void onActivityResult(int requestCode, int resultCode, Intent data)
         {
            if (requestCode == Constants.RESULT_PHOTO && resultCode == Activity.RESULT_OK)
               {
                  final boolean isCamera;

                  // check if the photo was from the camera
                  if (data == null || data.getData() == null)
                     {
                        // mark as true
                        isCamera = true;
                     }
                  else
                     {
                        // otherwise get the
                        final String action = data.getAction();
                        isCamera = action != null && action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                     }

                  // get the uri of the image in the gallery
                  if (!isCamera)
                     {
                        outputFileUri = data.getData();
                     }

                  // move the storage cursor to the image
                  String[] projection = {MediaStore.Images.Media.DATA};
                  Cursor cursor = getContentResolver().query(outputFileUri, projection, null, null, null);
                  int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                  cursor.moveToFirst();

                  // get the path of the photo from the cursor
                  photopath = cursor.getString(column_index_data);
                  cursor.close();

                  try
                     {
                        // create a new bitmap object
                        Bitmap photo = BitmapFactory.decodeFile(photopath);

                        // check if the photo needs to be rotated first
                        Matrix matrix = ImageManager.createRotationMatrix(photopath);

                        // create a bitmap with the correct rotation
                        photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);

                        // create an output stream and write the photo to the device
                        FileOutputStream fOut = new FileOutputStream(photopath);
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.flush();
                        fOut.close();

                        // reload the bitmap and resize it for the preview imageview
                        photo = ImageManager.loadBitmap(photopath);

                        // set the photo to the image view
                        imageView.setImageBitmap(photo);
                     }
                  catch (IOException e)
                     {
                        e.printStackTrace();
                     }
               }
         }


      /**
       * This method creates intents for the user to select where to get or capture a photo
       **/
      private void openImageIntent()
         {
            ContentValues values = new ContentValues();
            outputFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // create a new camera intent for the photo selection options
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

            // create a new gallery intent for the photo selection options
            final Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_PICK);

            // Chooser of filesystem options.
            final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

            // Add the camera options.
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

            startActivityForResult(chooserIntent, Constants.RESULT_PHOTO);
         }


   }
