package com.posn.datatypes;


import org.json.JSONException;


/**
 * This interface class defines functions that application data related lists should have
 **/
public interface ApplicationFile
   {
      String createApplicationFileContents() throws JSONException;
      void parseApplicationFileContents(String fileContents) throws JSONException;

      String getDirectoryPath();
      String getFileName();

   }
