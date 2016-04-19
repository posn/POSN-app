package com.posn;


import android.os.Environment;

public class Constants
   {
      public static final int STATUS_ACCEPTED = 1;
      public static final int STATUS_REQUEST = 2;
      public static final int STATUS_PENDING = 3;

      public static final int RESULT_ADD_FRIEND = 0;
      public static final int RESULT_ADD_FRIEND_GROUPS = 1;
      public static final int RESULT_CREATE_STATUS_POST = 2;
      public static final int RESULT_CREATE_PHOTO_POST = 3;

      public static final int RESULT_PHOTO = 4;


      public static final int TYPE_FRIEND_INFO = 0;
      public static final int TYPE_FRIEND_GROUPS = 1;

      // wall post types
      public static final int POST_TYPE_STATUS = 0;
      public static final int POST_TYPE_LINK = 1;
      public static final int POST_TYPE_PHOTO = 2;
      public static final int POST_TYPE_VIDEO = 3;


      // device file paths
      public static final String archiveFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/archive";
      public static final String encryptionKeyFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/keys";
      public static final String friendsFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/friends";
      public static final String multimediaFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/multimedia";
      public static final String profileFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/profile";
      public static final String wallFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/wall";
      public static final String messagesFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/messages";
      public static final String applicationDataFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/appData";

      // device file names
      public static final String groupListFile = "/user_groups.txt";
      public static final String wallListFile = "/user_wall.txt";
      public static final String notificationListFile = "/user_notifications.txt";
      public static final String converstationListFile = "/user_messages.txt";

      // cloud directory names
      public static final String wallDirectory = "wall";
      public static final String friendDirectory = "friend";
      public static final String multimediaDirectory = "multimedia";

      public static final String[] directoryNames = {
          "archive",
          "friends",
          "keys",
          "messages",
          "multimedia",
          "profile",
          "wall"
      };

      public static final int NUM_DIRECTORIES = 7;

   }
