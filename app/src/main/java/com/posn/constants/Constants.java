package com.posn.constants;


import android.os.Environment;

public class Constants
   {
      // Dropbox constants (These values will need to change if using your own developer console application)
      public static final String DROPBOX_APP_KEY = "bcwjrrwwyw309ol";
      public static final String DROPBOX_APP_SECRET = "xgu5rpt67mv4k25";

      // OneDrive constants (Client ID comes from the OneDrive Dev console)
      public static final String ONEDRIVE_CLIENT_ID = "aee521df-f303-4d6b-895a-b25ca917d161";

      // Cloud provider type values
      public static final int PROVIDER_DROPBOX = 0;
      public static final int PROVIDER_GOOGLEDRIVE = 1;
      public static final int PROVIDER_ONEDRIVE = 2;


      // Friend request status values
      public static final int STATUS_ACCEPTED = 1;
      public static final int STATUS_REQUEST = 2;
      public static final int STATUS_PENDING = 3;
      public static final int STATUS_TEMPORAL = 4;

      // Friend Fragment activity result values
      public static final int RESULT_ADD_FRIEND = 0;
      public static final int RESULT_ADD_FRIEND_GROUPS = 1;
      public static final int RESULT_CREATE_STATUS_POST = 2;
      public static final int RESULT_CREATE_PHOTO_POST = 3;
      public static final int RESULT_CREATE_COMMENTS = 4;
      public static final int RESULT_CREATE_GROUP = 5;
      public static final int RESULT_MANAGE_GROUP = 6;

      public static final int RESULT_PHOTO = 7;

      // add groups type value
      public static final int TYPE_FRIEND_REQUEST_NEW = 0;
      public static final int TYPE_FRIEND_REQUEST_ACCEPT = 1;

      // wall post type values
      public static final int POST_TYPE_STATUS = 0;
      public static final int POST_TYPE_LINK = 1;
      public static final int POST_TYPE_PHOTO = 2;
      public static final int POST_TYPE_VIDEO = 3;


      // device directory paths
      public static final String archiveFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/archive";
      public static final String encryptionKeyFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/keys";
      public static final String friendsFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/friends";
      public static final String multimediaFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/multimedia";
      public static final String profileFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/profile";
      public static final String wallFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/wall";
      public static final String messagesFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/messages";
      public static final String applicationDataFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/appData";

      // device application file names
      public static final String friendListFile = "/user_friends.txt";
      public static final String wallListFile = "/user_wall.txt";
      public static final String notificationListFile = "/user_notifications.txt";
      public static final String converstationListFile = "/user_messages.txt";
      public static final String userFile = "/user.txt";
      public static final String userGroupListFile = "/user_groups.txt";

      // cloud directory names individual
      public static final String archiveDirectory = "archive";
      public static final String wallDirectory = "wall";
      public static final String friendDirectory = "friends";
      public static final String multimediaDirectory = "multimedia";

      // cloud directory names as an array
      public static final int NUM_DIRECTORIES = 7;
      public static final String[] directoryNames = {
          "archive",
          "friends",
          "keys",
          "messages",
          "multimedia",
          "profile",
          "wall"
      };


   }
