package com.posn.managers;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import com.posn.constants.Constants;
import com.posn.datatypes.ApplicationFile;
import com.posn.datatypes.Friend;
import com.posn.datatypes.FriendGroup;
import com.posn.main.main.friends.FriendManager;
import com.posn.datatypes.RequestedFriend;
import com.posn.datatypes.UserGroup;
import com.posn.datatypes.WallPost;
import com.posn.exceptions.POSNCryptoException;
import com.posn.utility.AsymmetricKeyHelper;
import com.posn.utility.SymmetricKeyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


/**
 * This class stores all of the application data and provides methods to create and process files
 **/
public class AppDataManager implements Parcelable
   {
      // symmetric key used to encrypt/decrypt the user file on the device (created from the user's password)
      private String deviceFileKey = null;

      // boolean flag used to determine if a new friendID request needs to be processed in the processFriendRequest function
      private boolean newFriendRequest = false;

      // user object used to hold data belonging to the data owner
      public UserManager userManager = null;

      // list used to hold both the data owner and friends wall posts
      public WallPostManager wallPostManager = new WallPostManager();

      // list used to hold information for the data owner's friends
      public FriendManager friendManager = new FriendManager();

      // list of notifications
      public NotificationManager notificationManager = new NotificationManager();

      // list of conversations
      public ConversationManager conversationManager = new ConversationManager();

      // list of all the user defined groups
      public UserGroupManager userGroupManager = new UserGroupManager();

      // friendID request object to hold the newest friendID request from the URI
      public RequestedFriend requestedFriend = null;


      public AppDataManager(String deviceFileKey)
         {
            this.userManager = new UserManager();
            this.deviceFileKey = deviceFileKey;
         }

      public AppDataManager(UserManager userManager, String deviceFileKey)
         {
            this.userManager = userManager;
            this.deviceFileKey = deviceFileKey;
         }


      public RequestedFriend processFriendRequest() throws POSNCryptoException
         {
            if (newFriendRequest)
               {
                  // check if the friendID is a new incoming friendID request
                  if (requestedFriend.status == Constants.STATUS_REQUEST)
                     {
                        friendManager.friendRequests.add(requestedFriend);
                        newFriendRequest = false;
                        return requestedFriend;
                     }
                  // check if the friendID accepted the sent request
                  else if (requestedFriend.status == Constants.STATUS_ACCEPTED)
                     {
                        // get the requested from from the friendID request list
                        int index = friendManager.friendRequests.indexOf(requestedFriend);
                        RequestedFriend pendingFriend = friendManager.friendRequests.get(index);

                        // merge data
                        RequestedFriend friend = new RequestedFriend();

                        friend.status = Constants.STATUS_ACCEPTED;
                        friend.name = pendingFriend.name;
                        friend.email = pendingFriend.email;
                        friend.groups = pendingFriend.groups;
                        friend.fileLink = requestedFriend.fileLink;
                        friend.fileKey = requestedFriend.fileKey;
                        friend.publicKey = requestedFriend.publicKey;
                        friend.ID = requestedFriend.ID;
                        friend.nonce = requestedFriend.nonce;
                        friend.nonce2 = requestedFriend.nonce2;

                        newFriendRequest = false;
                        return friend;
                     }
               }
            return null;
         }


      public void parseFriendRequestURI(Uri uriData) throws UnsupportedEncodingException, POSNCryptoException
         {
            if (uriData != null)
               {
                  requestedFriend = new RequestedFriend();

                  // get the path segments of the URI
                  List<String> params = uriData.getPathSegments();

                  // check the type of URI
                  String uriType = params.get(0);

                  if (uriType.equals("request"))
                     {
                        // set friendID status
                        requestedFriend.status = Constants.STATUS_REQUEST;

                        // get ID
                        requestedFriend.ID = params.get(1);

                        // get email
                        requestedFriend.email = params.get(2);

                        // get first and last name
                        requestedFriend.name = params.get(3) + " " + params.get(4);

                        // get public key
                        requestedFriend.publicKey = URLDecoder.decode(params.get(5), "UTF-8");
                        requestedFriend.publicKey = requestedFriend.publicKey.replace("%2B", "+");

                        // get file link
                        requestedFriend.fileLink = URLDecoder.decode(params.get(6), "UTF-8");

                        // get nonce
                        requestedFriend.nonce = params.get(7);
                        newFriendRequest = true;
                     }
                  else if (uriType.equals("accept"))
                     {
                        // get encrypted key
                        String encryptedSymmetricKey = params.get(1);

                        // decrypt symmetric key
                        String key = AsymmetricKeyHelper.decrypt(userManager.privateKey, encryptedSymmetricKey);

                        // decrypt URI data
                        String encryptedURI = params.get(2);

                        String URI = SymmetricKeyHelper.decrypt(key, encryptedURI);
                        String[] paths = URI.split("/");

                        // set friendID status
                        requestedFriend.status = Constants.STATUS_ACCEPTED;

                        // get ID
                        requestedFriend.ID = paths[0];

                        // get first and last name
                        requestedFriend.name = paths[1] + " " + paths[2];

                        // get public key
                        requestedFriend.publicKey = URLDecoder.decode(paths[3], "UTF-8");
                        requestedFriend.publicKey = requestedFriend.publicKey.replace("%2B", "+");

                        // get file link
                        requestedFriend.fileLink = URLDecoder.decode(paths[4], "UTF-8");

                        requestedFriend.fileKey = URLDecoder.decode(paths[5], "UTF-8");

                        // get nonces
                        requestedFriend.nonce = paths[6];
                        requestedFriend.nonce2 = paths[7];
                        newFriendRequest = true;
                     }
               }
         }


      public void saveUserGroupListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            saveAppDataFileToDevice(userGroupManager);
         }

      public void loadUserGroupListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            loadAppDataFileFromDevice(userGroupManager);
         }

      public void loadNotificationListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            loadAppDataFileFromDevice(notificationManager);
         }

      public void saveNotificationListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            saveAppDataFileToDevice(notificationManager);
         }

      public void loadConversationListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            loadAppDataFileFromDevice(conversationManager);
         }

      public void saveConversationListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            saveAppDataFileToDevice(conversationManager);
         }

      public void saveFriendListAppFile(boolean executeAsAsync) throws IOException, JSONException, POSNCryptoException
         {
            if (executeAsAsync)
               saveAppDataFileToDeviceAsync(friendManager);
            else
               saveAppDataFileToDevice(friendManager);
         }

      public void loadFriendListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            loadAppDataFileFromDevice(friendManager);
         }

      public void saveWallPostListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            saveAppDataFileToDevice(wallPostManager);
         }

      public void loadWallPostListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            loadAppDataFileFromDevice(wallPostManager);
         }

      public void saveUserAppFile() throws IOException, JSONException, POSNCryptoException
         {
            saveAppDataFileToDevice(userManager);
         }

      public void loadUserAppFile() throws IOException, JSONException, POSNCryptoException
         {
            loadAppDataFileFromDevice(userManager);
         }

      public void createGroupWallFile(String groupID, String deviceDirectoryPath, String deviceFileName) throws IOException, JSONException, POSNCryptoException
         {
            UserGroup group = userGroupManager.getUserGroup(groupID);

            JSONObject object = new JSONObject();

            object.put("version", group.version);
            object.put("archive_link", JSONObject.NULL);
            object.put("archive_key", JSONObject.NULL);

            JSONArray postList = new JSONArray();

            for (int i = 0; i < group.wallPostList.size(); i++)
               {
                  WallPost wallPost = wallPostManager.getWallPost(group.wallPostList.get(i));
                  postList.put(wallPost.createJSONObject());
               }

            object.put("posts", postList);

            // convert JSON object to JSON formatted string
            String fileContents = object.toString();

            // encrypt the
            String encryptedData = SymmetricKeyHelper.encrypt(group.groupFileKey, fileContents);

            DeviceFileManager.writeStringToFile(encryptedData, deviceDirectoryPath, deviceFileName);
         }


      public ArrayList<WallPost> loadFriendGroupWallFile(FriendGroup group, String deviceDirectory, String fileName) throws IOException, JSONException, POSNCryptoException
         {
            ArrayList<WallPost> wallPostArrayList = new ArrayList<>();

            // download the wall file from the cloud (SHOULD BE MOVED OUT)
            DeviceFileManager.downloadFileFromURL(group.groupFileLink, deviceDirectory, fileName);

            // read in the encrypted data
            String encryptedString = DeviceFileManager.loadStringFromFile(deviceDirectory, fileName);

            // decrypt the file data
            String fileContents = SymmetricKeyHelper.decrypt(group.groupFileKey, encryptedString);

            JSONObject object = new JSONObject(fileContents);

            JSONArray postList = object.getJSONArray("posts");

            for (int i = 0; i < postList.length(); i++)
               {
                  WallPost wallPost = new WallPost();
                  wallPost.parseJSONObject(postList.getJSONObject(i));

                  wallPostArrayList.add(wallPost);
               }

            return wallPostArrayList;
         }

      public void createTemporalFriendFile(String uri, String deviceDirectory, String fileName) throws IOException, JSONException
         {
            JSONObject object = new JSONObject();

            if (uri != null)
               {
                  object.put("uri", uri);
               }
            else
               {
                  object.put("uri", JSONObject.NULL);
               }

            String fileContents = object.toString();

            DeviceFileManager.writeStringToFile(fileContents, deviceDirectory, fileName);
         }

      public boolean loadTemporalFriendFile(Friend friend, String deviceDirectory, String fileName) throws JSONException, IOException, POSNCryptoException
         {
            // read friendID file in
            String fileContents = DeviceFileManager.loadStringFromFile(deviceDirectory, fileName);

            JSONObject object = new JSONObject(fileContents);

            String URI = object.getString("uri");

            if (!URI.equals("null"))
               {
                  // parse temporal file
                  String[] paths = URI.split("/");

                  String encryptedSymmetricKey = paths[0];

                  // decrypt symmetric key
                  String key = AsymmetricKeyHelper.decrypt(userManager.privateKey, encryptedSymmetricKey);

                  // decrypt URI data
                  String encryptedURI = paths[1];

                  URI = SymmetricKeyHelper.decrypt(key, encryptedURI);
                  paths = URI.split("/");

                  // get file link
                  friend.friendFileLink = URLDecoder.decode(paths[1], "UTF-8");
                  friend.friendFileKey = URLDecoder.decode(paths[2], "UTF-8");

                  // get nonces
                  String nonce = paths[3];

                  return true;
               }

            return false;
         }

      public void createFriendFile(String friendID, String deviceDirectory, String fileName) throws IOException, JSONException, POSNCryptoException
         {
            // need to add user file link and key
            JSONObject obj = new JSONObject();
            JSONArray groupList = new JSONArray();

            Friend friend = friendManager.currentFriends.get(friendID);

            // ADD USER FILE LINK AND KEY

            for (int i = 0; i < friend.userGroups.size(); i++)
               {
                  UserGroup userGroup = userGroupManager.getUserGroup(friend.userGroups.get(i));
                  groupList.put(userGroup.createFriendFileJSONObject());
               }
            obj.put("groups", groupList);

            String fileContents = obj.toString();
            String encryptedString = SymmetricKeyHelper.encrypt(friend.userFriendFileKey, fileContents);

            DeviceFileManager.writeStringToFile(encryptedString, deviceDirectory, fileName);

         }

      public void loadFriendFile(String friendID, String deviceDirectory, String fileName) throws IOException, JSONException, POSNCryptoException
         {

            Friend friend = friendManager.currentFriends.get(friendID);

            // read friendID file in
            String encyrptedString = DeviceFileManager.loadStringFromFile(deviceDirectory, fileName);

            // decrypt string
            String friendFileData = SymmetricKeyHelper.decrypt(friend.friendFileKey, encyrptedString);

            // need to add user file link and key

            // GET USER FILE LINK AND KEY


            JSONObject obj = new JSONObject(friendFileData);
            JSONArray groupList = obj.getJSONArray("groups");

            for (int i = 0; i < groupList.length(); i++)
               {
                  FriendGroup friendGroup = new FriendGroup();

                  friendGroup.parseJSONObject(groupList.getJSONObject(i));

                  friend.friendGroups.add(friendGroup);
               }

         }

      private void saveAppDataFileToDevice(ApplicationFile object) throws IOException, JSONException, POSNCryptoException
         {
            // create a JSON formatted string to store the user data
            String fileContents = object.createApplicationFileContents();

            // encrypt file contents using the deviceFileKey
            String encryptedFileContents = SymmetricKeyHelper.encrypt(deviceFileKey, fileContents);

            // write the encrypted string to the user data file on the device
            DeviceFileManager.writeStringToFile(encryptedFileContents, object.getDirectoryPath(), object.getFileName());
         }

      private void saveAppDataFileToDeviceAsync(final ApplicationFile object)
         {
            new AsyncTask<Void, Void, Void>()
               {
                  protected Void doInBackground(Void... unused)
                     {
                        try
                           {
                              saveAppDataFileToDevice(object);
                           }
                        catch (IOException | JSONException | POSNCryptoException e)
                           {
                              e.printStackTrace();
                           }
                        return null;
                     }

               }.execute();
         }

      private void loadAppDataFileFromDevice(ApplicationFile object) throws IOException, JSONException, POSNCryptoException
         {
            String encryptedFileContents = DeviceFileManager.loadStringFromFile(object.getDirectoryPath(), object.getFileName());

            String fileContents = SymmetricKeyHelper.decrypt(deviceFileKey, encryptedFileContents);

            // create a JSON formatted string to store the user data
            object.parseApplicationFileContents(fileContents);
         }


      // Parcelling part
      public AppDataManager(Parcel in)
         {
            this.deviceFileKey = in.readString();
            this.userManager = in.readParcelable(UserManager.class.getClassLoader());
            this.wallPostManager = in.readParcelable(WallPostManager.class.getClassLoader());
            this.friendManager = in.readParcelable(FriendManager.class.getClassLoader());
            this.notificationManager = in.readParcelable(NotificationManager.class.getClassLoader());
            this.conversationManager = in.readParcelable(ConversationManager.class.getClassLoader());
            this.userGroupManager = in.readParcelable(UserGroupManager.class.getClassLoader());
            this.requestedFriend = in.readParcelable(RequestedFriend.class.getClassLoader());
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeString(deviceFileKey);
            dest.writeParcelable(userManager, flags);
            dest.writeParcelable(wallPostManager, flags);
            dest.writeParcelable(friendManager, flags);
            dest.writeParcelable(notificationManager, flags);
            dest.writeParcelable(conversationManager, flags);
            dest.writeParcelable(userGroupManager, flags);
            dest.writeParcelable(requestedFriend, flags);
         }

      public static final Parcelable.Creator<AppDataManager> CREATOR = new Parcelable.Creator<AppDataManager>()
         {
            public AppDataManager createFromParcel(Parcel in)
               {
                  return new AppDataManager(in);
               }

            public AppDataManager[] newArray(int size)
               {
                  return new AppDataManager[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }
