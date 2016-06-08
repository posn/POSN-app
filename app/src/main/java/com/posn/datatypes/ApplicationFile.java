package com.posn.datatypes;


import org.json.JSONException;

public interface ApplicationFile
   {
      String createApplicationFileContents() throws JSONException;
      void parseApplicationFileContents(String fileContents) throws JSONException;

      String getDirectoryPath();
      String getFileName();

   }
