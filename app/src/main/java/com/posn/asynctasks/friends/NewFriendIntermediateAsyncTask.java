package com.posn.asynctasks.friends;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.datatypes.Friend;
import com.posn.datatypes.RequestedFriend;
import com.posn.email.EmailSender;
import com.posn.encryption.AsymmetricKeyManager;
import com.posn.encryption.SymmetricKeyManager;
import com.posn.main.MainActivity;
import com.posn.main.friends.UserFriendsFragment;
import com.posn.utility.CloudFileManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class NewFriendIntermediateAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;
      private RequestedFriend requestedFriend;
      private UserFriendsFragment friendFrag;
      private MainActivity main;

      Friend newFriend;

      public NewFriendIntermediateAsyncTask(UserFriendsFragment frag, RequestedFriend requestedFriend)
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
            // create a new friend from the requested friend
            newFriend = new Friend(requestedFriend, Constants.STATUS_TEMPORAL);

            // create a new current friend for the accepted user and add them to the current friends list
            main.masterFriendList.currentFriends.put(newFriend.ID, newFriend);


            // create friend file with all friend group data
            String fileName = requestedFriend.ID + "_friend_file.txt";
            String deviceFilepath = Constants.friendsFilePath + "/" + fileName;
            CloudFileManager.createFriendFile(main.user, newFriend, deviceFilepath);

            // upload group wall to cloud and get direct link
            String friendFileLink = main.cloud.uploadFileToCloud(Constants.friendDirectory, fileName, deviceFilepath);


            // create URI
            String URI = "";

            try
               {
                  // replace all + chars in the key to the hex value
                  String publicKey = main.user.publicKey;
                  publicKey = publicKey.replace("+", "%2B");

                  // encode the key to maintain special chars
                  publicKey = URLEncoder.encode(publicKey, "UTF-8");

                  // encode friend file URL to maintain special chars
                  String encodedURL = URLEncoder.encode(friendFileLink, "UTF-8");
                  String encodedFriendFileKey = URLEncoder.encode(newFriend.userFriendFileKey, "UTF-8");

                  URI = main.user.ID + "/" + main.user.firstName + "/" + main.user.lastName.trim() + "/" + publicKey + "/" + encodedURL + "/" + encodedFriendFileKey + "/" + requestedFriend.nonce + "/" + requestedFriend.nonce2;

                  // generate symmetric key to encrypt data
                  String key = SymmetricKeyManager.createRandomKey();
                  System.out.println("KEY: " + key);

                  String encryptedURI = SymmetricKeyManager.encrypt(key, URI);

                  String encryptedKey = AsymmetricKeyManager.encrypt(requestedFriend.publicKey, key);

                  URI = "http://posn.com/accept/" + encryptedKey + "/" + encryptedURI;
               }
            catch (UnsupportedEncodingException e)
               {
                  e.printStackTrace();
               }


            // encrypt URI with friend's public key

            // send the email the the user (THIS IS BAD TO HARDCODE USERNAME AND PASS
            EmailSender email = new EmailSender("projectcloudbook@gmail.com", "cnlpass!!");
            email.sendMail("POSN - New Friend Request", "SUCCESS!\n\n" + URI, "POSN", requestedFriend.email);

            main.masterFriendList.saveFriendsListToFile();

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
            // add them to the list view as an accepted friend
            friendFrag.updateFriendList();

            // dismiss the dialog once done
            pDialog.dismiss();
         }


   }