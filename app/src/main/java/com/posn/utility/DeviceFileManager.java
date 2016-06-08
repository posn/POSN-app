package com.posn.utility;


import android.util.Log;

import com.posn.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class provides methods to process files to and from the Android device
 **/
public class DeviceFileManager
   {
      /**
       * Loads a JSON formatted file from the device
       *
       * @param deviceDirectoryPath device directory path of the file
       * @param deviceFileName      name of the file on the device
       * @return File contents as a JSON object
       * @throws IOException
       * @throws JSONException
       **/
      public static JSONObject loadJSONObjectFromFile(String deviceDirectoryPath, String deviceFileName) throws IOException, JSONException
         {
            String data = loadStringFromFile(deviceDirectoryPath, deviceFileName);
            return new JSONObject(data);
         }


      /**
       * Loads a string from a file on the device
       *
       * @param deviceDirectoryPath device directory path of the file
       * @param deviceFileName      name of the file on the device
       * @return File contents as a string
       * @throws IOException
       **/
      public static String loadStringFromFile(String deviceDirectoryPath, String deviceFileName) throws IOException
         {
            String line;

            // open the file
            BufferedReader br = new BufferedReader(new FileReader(deviceDirectoryPath + "/" + deviceFileName));

            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null)
               {
                  sb.append(line);
               }

            br.close();

            return sb.toString();
         }


      /**
       * Writes a JSON object into a new file on the device
       *
       * @param data                the data to be written to the file
       * @param deviceDirectoryPath device directory path of the file
       * @param deviceFileName      name of the file to be created on the device
       * @throws IOException
       **/
      public static void writeJSONToFile(JSONObject data, String deviceDirectoryPath, String deviceFileName) throws IOException
         {
            writeStringToFile(data.toString(), deviceDirectoryPath, deviceFileName);
         }


      /**
       * Writes a string into a new file on the device
       *
       * @param data                the data to be written to the file
       * @param deviceDirectoryPath device directory path of the file
       * @param deviceFileName      name of the file to be created on the device
       * @throws IOException
       **/
      public static void writeStringToFile(String data, String deviceDirectoryPath, String deviceFileName) throws IOException
         {
            FileWriter fw = new FileWriter(deviceDirectoryPath + "/" + deviceFileName);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data);
            bw.close();
         }


      /**
       * Downloads a file from a URL and writes it to the device
       *
       * @param urlLink             URL to the file to be downloaded
       * @param deviceDirectoryPath device directory path of the file
       * @param deviceFileName      name of the file to be created on the device
       * @throws IOException
       **/
      public static void downloadFileFromURL(String urlLink, String deviceDirectoryPath, String deviceFileName) throws IOException
         {

            URL url = new URL(urlLink);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
               {
                  System.out.println("Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                  return;
               }

            // download the file
            InputStream input = connection.getInputStream();
            OutputStream output = new FileOutputStream(deviceDirectoryPath + "/" + deviceFileName);

            byte data[] = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1)
               {
                  output.write(data, 0, count);
               }

            // close the streams and connections
            output.close();
            input.close();
            connection.disconnect();

         }

      /**
       * Creates the default storage directories on the device if it does not already exist
       **/
      public static void createDefaultStorageDirectories()
         {
            DeviceFileManager.createDirectory(Constants.archiveFilePath);
            DeviceFileManager.createDirectory(Constants.encryptionKeyFilePath);
            DeviceFileManager.createDirectory(Constants.multimediaFilePath);
            DeviceFileManager.createDirectory(Constants.profileFilePath);
            DeviceFileManager.createDirectory(Constants.wallFilePath);
            DeviceFileManager.createDirectory(Constants.messagesFilePath);
            DeviceFileManager.createDirectory(Constants.applicationDataFilePath);
            DeviceFileManager.createDirectory(Constants.friendsFilePath);
         }

      /**
       * Creates a new directory on the device if it does not already exist
       *
       * @param newDirectoryPath name of the new device directory
       **/
      private static void createDirectory(String newDirectoryPath)
         {
            // check if directory exists
            File storageDir = new File(newDirectoryPath);
            if (!storageDir.exists())
               {
                  boolean status = storageDir.mkdirs();
                  if (!status)
                     {
                        Log.d("DeviceFileManager", "Failed to created device directory: " + newDirectoryPath);
                     }
               }
         }

   }
