package com.posn.utility;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileManager
   {
      public static JSONObject loadJSONObjectFromFile(String devicePath)
         {
            String data = loadStringFromFile(devicePath);
            JSONObject object = null;

            try
               {
                  object = new JSONObject(data);
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
            return object;
         }

      public static String loadStringFromFile(String filename)
         {
            String line, fileContents = null;
            // open the file
            try
               {
                  BufferedReader br = new BufferedReader(new FileReader(filename));

                  StringBuilder sb = new StringBuilder();
                  while ((line = br.readLine()) != null)
                     {
                        sb.append(line);
                     }

                  br.close();
                  fileContents = sb.toString();
               }
            catch (IOException e)
               {
                  System.out.println(e.getMessage());
               }

            return fileContents;
         }

      public static void writeJSONToFile(JSONObject data, String devicePath)
         {
            writeStringToFile(data.toString(), devicePath);
         }

      public static void writeStringToFile(String data, String devicePath)
         {
            try
               {
                  FileWriter fw = new FileWriter(devicePath);
                  BufferedWriter bw = new BufferedWriter(fw);
                  bw.write(data);
                  bw.close();
               }
            catch (IOException e)
               {
                  e.printStackTrace();
               }

         }

      public static void downloadFileFromURL(String urlLink, String filePath, String fileName)
         {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try
               {
                  URL url = new URL(urlLink);
                  connection = (HttpURLConnection) url.openConnection();
                  connection.connect();

                  // expect HTTP 200 OK, so we don't mistakenly save error report
                  // instead of the file
                  if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                     {
                        System.out.println("Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                        return;
                     }

                  // download the file
                  input = connection.getInputStream();
                  output = new FileOutputStream(filePath + "/" + fileName);

                  byte data[] = new byte[4096];
                  long total = 0;
                  int count;
                  while ((count = input.read(data)) != -1)
                     {
                        total += count;
                        output.write(data, 0, count);
                     }
               }
            catch (Exception e)
               {
                  System.out.println(e.toString());
               }
            finally
               {
                  try
                     {
                        if (output != null)
                           output.close();
                        if (input != null)
                           input.close();
                     }
                  catch (IOException ignored)
                     {
                     }

                  if (connection != null)
                     connection.disconnect();
               }
         }


   }
