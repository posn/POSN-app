package com.posn.asynctasks.friends;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.datatypes.RequestedFriend;
import com.posn.encryption.AsymmetricKeyManager;
import com.posn.encryption.SymmetricKeyManager;
import com.posn.main.MainActivity;
import com.posn.main.friends.UserFriendsFragment;
import com.posn.utility.CloudFileManager;

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
            // create friend file with all friend group data
            String fileName = requestedFriend.ID + "_friend_file.txt";
            String deviceFilepath = Constants.friendsFilePath + "/" + fileName;
            CloudFileManager.createFriendFile(requestedFriend.groups, main.userGroupList.groups, deviceFilepath);

            // upload group wall to cloud and get direct link
            String friendFileLink = main.cloud.uploadFileToCloud(Constants.friendDirectory, fileName, deviceFilepath);
            // create URI
            String URI = "";

            try
               {
                  // encode temporal URL to maintain special chars
                  String encodedURL = URLEncoder.encode(friendFileLink, "UTF-8");

                  URI = main.user.ID + "/" + encodedURL  + "/" + requestedFriend.nonce2;
                  System.out.println("NONCE: " + requestedFriend.nonce2);

                  // generate symmetric key to encrypt data
                  String key = SymmetricKeyManager.createRandomKey();

                  String encryptedURI = SymmetricKeyManager.encrypt(key, URI);

                  System.out.println("PUB KEY: " + requestedFriend.publicKey);
                  String encryptedKey = AsymmetricKeyManager.encrypt(requestedFriend.publicKey, key);

                  URI = encryptedKey + "/" + encryptedURI;
               }
            catch (UnsupportedEncodingException e)
               {
                  e.printStackTrace();
               }

            // SEND AS DIRECT MESSAGE

            // add to temporal file and upload
            fileName = requestedFriend.nonce + "_temp_friend_file.txt";
            deviceFilepath = Constants.friendsFilePath + "/" + fileName;
            System.out.println("URI: " + URI);
            CloudFileManager.createTemporalFriendFile(URI, deviceFilepath);

            friendFileLink = main.cloud.uploadFileToCloud(Constants.friendDirectory, fileName, deviceFilepath);


            //  main.masterFriendList.saveFriendsListToFileAsyncTask(Constants.applicationDataFilePath + "/user_friends.txt");

            /*
            // add pending friend to request friends list
            main.masterFriendList.friendRequests.add(requestedFriend);


            friendFrag.listViewItems.add(new PendingFriendItem(requestedFriend));
            friendFrag.sortFriendsList();

            main.masterFriendList.saveFriendsListToFileAsyncTask(Constants.applicationDataFilePath + "/user_friends.txt");
*/


            return null;
         }


      // After completing background task Dismiss the progress dialog
      protected void onPostExecute(String file_url)
         {
            // notify the adapter that the data changed
            friendFrag.adapter.notifyDataSetChanged();

            // dismiss the dialog once done
            pDialog.dismiss();
         }
   }