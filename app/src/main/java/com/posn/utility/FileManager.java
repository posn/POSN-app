package com.posn.utility;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileManager
   {
      public static String loadFileFromDevice(String filename)
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
   }
