package com.posn.utility;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

public class ImageManager
   {
      public static Bitmap BITMAP_RESIZER(Bitmap bitmap, int newWidth, int newHeight)
         {
            Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

            float ratioX = newWidth / (float) bitmap.getWidth();
            float ratioY = newHeight / (float) bitmap.getHeight();
            float middleX = newWidth / 2.0f;
            float middleY = newHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

            return scaledBitmap;

         }

      public static Bitmap loadEncryptedBitmap(String key, String filename)
         {
            File imgFile = new File(filename);

            try
               {
                  byte[] contents = Files.toByteArray(imgFile);
                  contents = SymmetricKeyManager.decrypt(key, contents);


                  Bitmap photo;
                  int file_size = Integer.parseInt(String.valueOf(imgFile.length() / 1024));
                  if (file_size > 2048)
                     {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4;
                        photo = BitmapFactory.decodeByteArray(contents, 0, contents.length, options);
                     }
                  else if (file_size > 1024)
                     {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        photo = BitmapFactory.decodeByteArray(contents, 0, contents.length, options);
                     }
                  else
                     photo = BitmapFactory.decodeByteArray(contents, 0, contents.length);

                  Matrix matrix = new Matrix();

                  ExifInterface exifReader;


                  exifReader = new ExifInterface(filename);
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
            catch (IOException e)
               {
                  e.printStackTrace();
               }
            return null;
         }
   }
