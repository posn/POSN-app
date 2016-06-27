package com.posn.asynctasks.wall;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.posn.Constants;
import com.posn.datatypes.WallPost;
import com.posn.exceptions.POSNCryptoException;
import com.posn.utility.ImageManager;

import java.io.IOException;

/**
 * This AsyncTask class reads in an encrypted image from the device and decrypts it to be displayed
 * <ul><li>The image view has a loading circle while the photo is processed</ul>
 **/
public class LoadImageAsyncTask extends AsyncTask<Object, Void, Bitmap>
   {
      // user interface variables
      private ImageView imageView;
      private RelativeLayout loader;


      private String path;
      private WallPost wallPost;

      public LoadImageAsyncTask(ImageView imv, RelativeLayout loader)
         {
            this.imageView = imv;
            this.wallPost = (WallPost) imv.getTag();
            this.loader = loader;

            path = Constants.multimediaFilePath + "/" + wallPost.postID + ".jpg";
         }

      @Override
      protected Bitmap doInBackground(Object... params)
         {
            // load and decrypt the photo from the device
            try
               {
                  return ImageManager.loadEncryptedBitmap(wallPost.multimediaKey, path);
               }
            catch (IOException | POSNCryptoException e)
               {
                  e.printStackTrace();
               }

            return null;
         }

      @Override
      protected void onPostExecute(Bitmap result)
         {
            // get the wall post from the image view
            WallPost viewWallPost = (WallPost) imageView.getTag();

            // construct the path for the wall post photo
            String pathCheck = Constants.multimediaFilePath + "/" + viewWallPost.postID + ".jpg";

            // check if the paths are the same
            if (!pathCheck.equals(path))
               {
                  // the path is not the same, therefore the photo is being handled by a different asynctask, so ignore
                  return;
               }

            // check if the image has been loaded and the imageview exists
            if (result != null && imageView != null)
               {
                  // set the imageview to the photo
                  imageView.setImageBitmap(result);
               }
            else
               {
                  // remove the imageview from the post
                  imageView.setVisibility(View.GONE);
               }

            // hide the loader spinner
            loader.setVisibility(View.GONE);
         }
   }