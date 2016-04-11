package com.posn;


import android.os.Environment;

public class Constants
   {
      public static final int STATUS_ACCEPTED = 1;
      public static final int STATUS_REQUEST = 2;
      public static final int STATUS_PENDING = 3;

      public static final int ADD_FRIEND_RESULT = 1;


      // device file paths
      public static final String archiveFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/archive";
      public static final String encryptionKeyFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/keys";
      public static final String multimediaFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/multimedia";
      public static final String profileFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/profile";
      public static final String wallFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/wall";
      public static final String messagesFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/messages";
      public static final String applicationDataFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/appData";

      // device file names
      public static final String groupListFile = "/user_groups.txt";

      // cloud directory names
      public static final String wallDirectory = "wall";

      public static final String[] directoryNames = {
                                                  "archive",
                                                  "keys",
                                                  "messages",
                                                  "multimedia",
                                                  "profile",
                                                  "wall"
      };

      public static final int NUM_DIRECTORIES = 6;

   }
