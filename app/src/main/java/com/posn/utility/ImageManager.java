package com.posn.utility;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;

import com.google.common.io.Files;
import com.posn.exceptions.POSNCryptoException;

import java.io.File;
import java.io.IOException;

/**
 * This class implements methods that facilitate loading and processing images
 **/
public class ImageManager
   {
      /**
       * This method resizes a bitmap given a new width and height
       **/
      public static Bitmap resizeImage(Bitmap bitmap, int newWidth, int newHeight)
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

      /**
       * This method reads in an encrypted image from the device and decrypts it and returns it as a bitmap
       **/
      public static Bitmap loadEncryptedBitmap(String key, String filename)
         {
            // declare variables
            Bitmap photo;

            // create a file object for the image
            File imgFile = new File(filename);

            try
               {
                  // get the file as a byte array
                  byte[] contents = Files.toByteArray(imgFile);

                  // decrypt the byte array to plaintext
                  contents = SymmetricKeyManager.decrypt(key, contents);

                  // get the file size to downsize the image
                  int file_size = Integer.parseInt(String.valueOf(imgFile.length() / 1024));

                  // resize the image based on the file size
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
                     {
                        photo = BitmapFactory.decodeByteArray(contents, 0, contents.length);
                     }

                  // create the rotation matrix to rotate the image
                  Matrix matrix = createRotationMatrix(filename);

                  // create a new rotated bitmap
                  photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);

                  return photo;
               }
            catch (IOException | POSNCryptoException e)
               {
                  e.printStackTrace();
               }
            return null;
         }

      /**
       * This method creates a rotation matrix based on the image's orientation property
       **/
      private static Matrix createRotationMatrix(String filepath) throws IOException
         {
            // create a new matrix to rotate the photo
            Matrix matrix = new Matrix();

            // read the image property tags
            ExifInterface exifReader = new ExifInterface(filepath);

            // get the image orientation
            int orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

            // rotate the image
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

            return matrix;
         }
   }
