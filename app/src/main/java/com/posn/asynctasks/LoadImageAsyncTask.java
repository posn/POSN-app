package com.posn.asynctasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.posn.R;
import com.posn.datatypes.Post;
import com.posn.utility.ImageManager;


public class LoadImageAsyncTask extends AsyncTask<Object, Void, Bitmap>
   {

      private ImageView imageView;
      private RelativeLayout loader;
      private String path;
      private Post post;

      public LoadImageAsyncTask(ImageView imv, RelativeLayout loader)
         {
            this.imageView = imv;
            this.path = imv.getTag(R.id.photo_path).toString();
            this.post = (Post)imv.getTag(R.id.photo_key);
            this.loader = loader;
         }

      @Override
      protected Bitmap doInBackground(Object... params)
         {
            return ImageManager.loadEncryptedBitmap(post.multimediaKey, path);
         }

      @Override
      protected void onPostExecute(Bitmap result)
         {
            if (!imageView.getTag(R.id.photo_path).toString().equals(path))
               {
               /* The path is not same. This means that this
                  image view is handled by some other async task.
                  We don't do anything and return. */
                  return;
               }

            if (result != null && imageView != null)
               {
                  //imageView.setVisibility(View.VISIBLE);
                  imageView.setImageBitmap(result);
               }
            else
               {
                  imageView.setVisibility(View.GONE);
               }
            loader.setVisibility(View.GONE);

         }

   }