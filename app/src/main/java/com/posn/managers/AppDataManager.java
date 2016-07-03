package com.posn.managers;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import com.posn.constants.Constants;
import com.posn.datatypes.ApplicationFile;
import com.posn.datatypes.Friend;
import com.posn.datatypes.FriendGroup;
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

      // friend request object to hold the newest friend request from the URI
      public RequestedFriend requestedFriend = null;

      // boolean flag used to determine if a new friend request needs to be processed in the processFriendRequest function
      private boolean newFriendRequest = false;


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


      /**
       * This method processes new friend requests and accepted friend requests by creating and updating the requested friend objects
       **/
      public RequestedFriend processFriendRequest() throws POSNCryptoException
         {
            // check if there is a new friend request to process
            if (newFriendRequest)
               {
                  // check if the friend is a new incoming friend request
                  if (requestedFriend.status == Constants.STATUS_REQUEST)
                     {
                        // add the requesting friend to the friend request list
                        friendManager.friendRequests.add(requestedFriend);
                        newFriendRequest = false;
                        return requestedFriend;
                     }
                  // check if the friend accepted the sent request
                  else if (requestedFriend.status == Constants.STATUS_ACCEPTED)
                     {
                        // get the requested from from the friend request list
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


      /**
       * This method extracts the data from the friend request URI and populates a requested friend object with the data
       **/
      public void parseFriendRequestURI(Uri uriData) throws UnsupportedEncodingException, POSNCryptoException
         {
            // check if the URI needs to be parsed
            if (uriData != null)
               {
                  // create a new requested friend object to fill with data from the URI
                  requestedFriend = new RequestedFriend();

                  // get the path segments of the URI
                  List<String> params = uriData.getPathSegments();

                  // check the type of URI
                  String uriType = params.get(0);

                  // check if the URI is a new friend request
                  if (uriType.equals("request"))
                     {
                        // set friend status
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
                  // check if the URI is an accepted notification
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

                        // set friend status
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

      public void createArchivedGroupWallFile(String groupID, String deviceDirectoryPath, String deviceFileName) throws IOException, JSONException, POSNCryptoException
         {

         }


      /**
       * This method creates a group wall file that holds the wall version number, archive link/key, and the list of wall posts for that group
       **/
      public void createGroupWallFile(String groupID, String deviceDirectoryPath, String deviceFileName) throws IOException, JSONException, POSNCryptoException
         {
            // get the group object from the user group manager
            UserGroup group = userGroupManager.getUserGroup(groupID);

            // create a JSON object and add the version number and archive link and key
            JSONObject object = new JSONObject();
            object.put("version", group.version);
            object.put("archive_link", group.archiveFileLink);
            object.put("archive_key", group.archiveFileKey);

            // create a JSON array to add the wall post IDs
            JSONArray postList = new JSONArray();
            for (int i = 0; i < group.wallPostList.size(); i++)
               {
                  WallPost wallPost = wallPostManager.getWallPost(group.wallPostList.get(i));
                  postList.put(wallPost.createJSONObject());
               }

            // add the JSON array to the main JSON object
            object.put("posts", postList);

            // convert JSON object to JSON formatted string
            String fileContents = object.toString();

            // encrypt the file contents
            String encryptedData = SymmetricKeyHelper.encrypt(group.groupFileKey, fileContents);

            // write out the encrypted string to a file
            DeviceFileManager.writeStringToFile(encryptedData, deviceDirectoryPath, deviceFileName);
         }


      /**
       * This method loads the group wall file information from the user's friends and returns out the updated wall posts
       **/
      public ArrayList<WallPost> loadFriendGroupWallFile(FriendGroup group, String deviceDirectory, String fileName) throws IOException, JSONException, POSNCryptoException
         {
            ArrayList<WallPost> wallPostArrayList = new ArrayList<>();

            // read in the encrypted data
            String encryptedString = DeviceFileManager.loadStringFromFile(deviceDirectory, fileName);

            // check if there was an issue fetching the link
            if(encryptedString.contains("Link not found"))
               {
                  System.out.println("ERROR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                  return null;
               }

            // decrypt the file data
            String fileContents = SymmetricKeyHelper.decrypt(group.groupFileKey, encryptedString);

            // get the JSON object from the string
            JSONObject object = new JSONObject(fileContents);

            // get the wall post data from the JSON object
            JSONArray postList = object.getJSONArray("posts");
            for (int i = 0; i < postList.length(); i++)
               {
                  // get the wall post and convert it from JSON to the wall post format
                  WallPost wallPost = new WallPost();
                  wallPost.parseJSONObject(postList.getJSONObject(i));

                  // add the post to the list of wall posts
                  wallPostArrayList.add(wallPost);
               }

            return wallPostArrayList;
         }


      /**
       * This method creates a temporal friend file that is used during the friend request process
       **/
      public void createTemporalFriendFile(String uri, String deviceDirectory, String fileName) throws IOException, JSONException
         {
            // create a new JSON object
            JSONObject object = new JSONObject();

            // check if a URI needs to be added
            if (uri != null)
               {
                  // put the uri in the JSON object
                  object.put("uri", uri);
               }
            else
               {
                  // otherwise put a null in the JSON object
                  object.put("uri", JSONObject.NULL);
               }

            // convert the JSON object to a string
            String fileContents = object.toString();

            // write the file string to a file on the device
            DeviceFileManager.writeStringToFile(fileContents, deviceDirectory, fileName);
         }


      /**
       * This method reads in and parses the temporal friend file. Reads in a URI if it exists
       **/
      public boolean loadTemporalFriendFile(Friend friend, String deviceDirectory, String fileName) throws JSONException, IOException, POSNCryptoException
         {
            // read in the file contents in
            String fileContents = DeviceFileManager.loadStringFromFile(deviceDirectory, fileName);

            // create a JSON object from the file contents string
            JSONObject object = new JSONObject(fileContents);

            // get the URI field
            String URI = object.getString("uri");

            // check if the URI needs to be handled
            if (!URI.equals("null"))
               {
                  // parse temporal file data
                  String[] paths = URI.split("/");

                  // get the encrypted symmetric key
                  String encryptedSymmetricKey = paths[0];

                  // decrypt symmetric key with the user's private key
                  String key = AsymmetricKeyHelper.decrypt(userManager.privateKey, encryptedSymmetricKey);

                  // get the encrypted URI
                  String encryptedURI = paths[1];

                  // decrypt the URI with the symmetric key
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


      /**
       * This method creates a friend file that contains the information about what groups a friend has access to
       **/
      public void createFriendFile(String friendID, String deviceDirectory, String fileName) throws IOException, JSONException, POSNCryptoException
         {
            // create a JSON object and array
            JSONObject obj = new JSONObject();
            JSONArray groupList = new JSONArray();

            // get the friend object from the friend manager
            Friend friend = friendManager.getFriend(friendID);

            // ADD USER FILE LINK AND KEY

            // loop through all the groups the friend is in
            for (int i = 0; i < friend.userGroups.size(); i++)
               {
                  // get the user object
                  UserGroup userGroup = userGroupManager.getUserGroup(friend.userGroups.get(i));

                  // create the group information and put it in the JSON array
                  groupList.put(userGroup.createFriendFileJSONObject());
               }

            // add the JSON array to the main JSON object
            obj.put("groups", groupList);

            // convert the JSON object to a string
            String fileContents = obj.toString();

            // encrypt the file contents with the friend's friend file key
            String encryptedString = SymmetricKeyHelper.encrypt(friend.userFriendFileKey, fileContents);

            // write the encrypted string to the device
            DeviceFileManager.writeStringToFile(encryptedString, deviceDirectory, fileName);
         }


      /**
       * This method loads in the user's friend file from a friend. Adds friend groups into the friend object. Returns if the file was loaded or not
       **/
      public boolean loadFriendFile(String friendID, String deviceDirectory, String fileName) throws IOException, JSONException, POSNCryptoException
         {
            // get the friend object from the friend manager
            Friend friend = friendManager.getFriend(friendID);

            // read friend file in from the device
            String encryptedString = DeviceFileManager.loadStringFromFile(deviceDirectory, fileName);

            // check if there was an issue fetching the link
            if(encryptedString.contains("Link not found"))
               {
                  return false;
               }

            // decrypt the file contents string
            String friendFileData = SymmetricKeyHelper.decrypt(friend.friendFileKey, encryptedString);

            // NEED TO ADD FIELDS FOR FRIEND USER FILE LINK AND KEY

            // create a JSON object from the file contents
            JSONObject obj = new JSONObject(friendFileData);

            // get the groups field
            JSONArray groupList = obj.getJSONArray("groups");

            // clear the number of friend groups
            friend.friendGroups.clear();

            // iterate through all the groups
            for (int i = 0; i < groupList.length(); i++)
               {
                  // create a new friend group object and fill it with data
                  FriendGroup friendGroup = new FriendGroup();
                  friendGroup.parseJSONObject(groupList.getJSONObject(i));

                  // add to the friend object
                  friend.friendGroups.add(friendGroup);
               }

            // update the friend object in the friend manager
            friendManager.currentFriends.put(friend.ID, friend);

            return true;
         }


      /**
       * This method saves a generic application file object to the device and encrypts it with the device key
       **/
      private void saveAppDataFileToDevice(ApplicationFile object) throws IOException, JSONException, POSNCryptoException
         {
            // create a JSON formatted string to store the user data
            String fileContents = object.createApplicationFileContents();

            // encrypt file contents using the deviceFileKey
            String encryptedFileContents = SymmetricKeyHelper.encrypt(deviceFileKey, fileContents);

            // write the encrypted string to the user data file on the device
            DeviceFileManager.writeStringToFile(encryptedFileContents, object.getDirectoryPath(), object.getFileName());
         }


      /**
       * This method saves the user group manager to the device
       **/
      public void saveUserGroupListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            saveAppDataFileToDevice(userGroupManager);
         }


      /**
       * This method loads the user group manager data from a file
       **/
      public void loadUserGroupListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            loadAppDataFileFromDevice(userGroupManager);
         }


      /**
       * This method loads the notification manager data from a file
       **/
      public void loadNotificationListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            loadAppDataFileFromDevice(notificationManager);
         }


      /**
       * This method saves the notification manager to the device
       **/
      public void saveNotificationListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            saveAppDataFileToDevice(notificationManager);
         }


      /**
       * This method loads the conversation manager data from a file
       **/
      public void loadConversationListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            loadAppDataFileFromDevice(conversationManager);
         }


      /**
       * This method saves the conversation manager to the device
       **/
      public void saveConversationListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            saveAppDataFileToDevice(conversationManager);
         }


      /**
       * This method saves the friend manager to the device
       **/
      public void saveFriendListAppFile(boolean executeAsAsync) throws IOException, JSONException, POSNCryptoException
         {
            if (executeAsAsync)
               saveAppDataFileToDeviceAsync(friendManager);
            else
               saveAppDataFileToDevice(friendManager);
         }


      /**
       * This method loads the friend manager data from a file
       **/
      public void loadFriendListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            loadAppDataFileFromDevice(friendManager);
         }


      /**
       * This method saves the wall post manager to the device
       **/
      public void saveWallPostListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            saveAppDataFileToDevice(wallPostManager);
         }


      /**
       * This method loads the wall post manager data from a file
       **/
      public void loadWallPostListAppFile() throws IOException, JSONException, POSNCryptoException
         {
            loadAppDataFileFromDevice(wallPostManager);
         }


      /**
       * This method saves the user manager to the device
       **/
      public void saveUserAppFile() throws IOException, JSONException, POSNCryptoException
         {
            saveAppDataFileToDevice(userManager);
         }


      /**
       * This method loads the user manager data from a file
       **/
      public void loadUserAppFile() throws IOException, JSONException, POSNCryptoException
         {
            loadAppDataFileFromDevice(userManager);
         }

      /**
       * This method saves a generic application file object to the device and encrypts it with the device key as an async task
       **/
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


      /**
       * This method loads the data of a generic application file object from the device and decrypts it with the device key
       **/
      private void loadAppDataFileFromDevice(ApplicationFile object) throws IOException, JSONException, POSNCryptoException
         {
            // load the encrypted string from the device
            String encryptedFileContents = DeviceFileManager.loadStringFromFile(object.getDirectoryPath(), object.getFileName());

            // decrypt the encrypted string using the device file key
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
