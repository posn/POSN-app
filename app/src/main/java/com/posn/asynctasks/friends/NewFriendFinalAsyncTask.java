package com.posn.asynctasks.friends;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.datatypes.Friend;
import com.posn.datatypes.RequestedFriend;
import com.posn.utility.AsymmetricKeyManager;
import com.posn.utility.SymmetricKeyManager;
import com.posn.main.MainActivity;
import com.posn.main.friends.AcceptedFriendItem;
import com.posn.main.friends.UserFriendsFragment;
import com.posn.utility.CloudFileManager;
import com.posn.utility.DeviceFileManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class NewFriendFinalAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;
      private RequestedFriend requestedFriend;
      private UserFriendsFragment friendFrag;
      private MainActivity main;

      public NewFriendFinalAsyncTask(UserFriendsFragment frag, RequestedFriend requestedFriend)
         {
            super();
            friendFrag = frag;
            this.requestedFriend = requestedFriend;
            main = friendFrag.activity;
         }


      // Before starting background thread Show Progress Dialog
      @Override
      protected void onPreExecute()
         {
            super.onPreExecute();
            pDialog = new ProgressDialog(main);
            pDialog.setMessage("Adding New Friend...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }


      // Checking login in background
      protected String doInBackground(String... params)
         {
            // create a new friend from requested friend
            Friend newFriend = new Friend(requestedFriend, Constants.STATUS_ACCEPTED);

            // create friend file with all friend group data
            String fileName = requestedFriend.ID + "_friend_file.txt";
            String deviceFilepath = Constants.friendsFilePath;
            CloudFileManager.createFriendFile(main.user, newFriend, deviceFilepath, fileName);

            // upload group wall to cloud and get direct link
            String friendFileLink = main.cloud.uploadFileToCloud(Constants.friendDirectory, fileName, deviceFilepath + "/" + fileName);
            // create URI
            String URI = "";

            try
               {
                  // encode the friend file URL and user friend file key to maintain special chars
                  String encodedURL = URLEncoder.encode(friendFileLink, "UTF-8");
                  String encodedKey = URLEncoder.encode(newFriend.userFriendFileKey, "UTF-8");

                  // create URI with appropriate data
                  URI = main.user.ID + "/" + encodedURL + "/" + encodedKey  + "/" + requestedFriend.nonce2;

                  // generate symmetric key to encrypt the URI
                  String key = SymmetricKeyManager.createRandomKey();
                  String encryptedURI = SymmetricKeyManager.encrypt(key, URI);

                  // encrypt the symmetric key will the friend's public key
                  String encryptedKey = AsymmetricKeyManager.encrypt(requestedFriend.publicKey, key);

                  // build final URI to send to friend
                  URI = encryptedKey + "/" + encryptedURI;
               }
            catch (UnsupportedEncodingException e)
               {
                  e.printStackTrace();
               }

            // SEND AS DIRECT MESSAGE

            // add to temporal file and upload to cloud
            fileName = requestedFriend.nonce + "_temp_friend_file.txt";
            deviceFilepath = Constants.friendsFilePath;
            System.out.println("URI: " + URI);
            CloudFileManager.createTemporalFriendFile(URI, deviceFilepath, fileName);

            friendFileLink = main.cloud.uploadFileToCloud(Constants.friendDirectory, fileName, deviceFilepath + "/" + fileName);

            friendFrag.listViewItems.add(new AcceptedFriendItem(friendFrag, newFriend));
            friendFrag.sortFriendsList();


            // NEED TO FETCH ACCEPTED FRIEND'S FRIEND FILE FOR THE USER
            fileName = requestedFriend.ID + "_friend_user_file.txt";
            deviceFilepath = Constants.wallFilePath;
            DeviceFileManager.downloadFileFromURL(newFriend.friendFileLink, deviceFilepath, fileName);

            CloudFileManager.loadFriendFile(newFriend, deviceFilepath, fileName);

            for(int i = 0; i < newFriend.friendGroups.size(); i++)
               {
                  System.out.println(newFriend.friendGroups.get(i).groupFileLink);
               }

            // remove request friend from friend request list and add to current friends list
            main.masterFriendList.currentFriends.put(newFriend.ID, newFriend);
            main.masterFriendList.saveFriendsListToFile();

            return null;
         }


      // After completing background task Dismiss the progress dialog
      protected void onPostExecute(String file_url)
         {
            // notify the adapter that the data changed
            friendFrag.updateFriendList();

            // dismiss the dialog once done
            pDialog.dismiss();
         }
   }