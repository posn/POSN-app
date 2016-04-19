package com.posn.asynctasks.friends;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.datatypes.RequestedFriend;
import com.posn.email.EmailSender;
import com.posn.main.MainActivity;
import com.posn.main.friends.UserFriendsFragment;
import com.posn.utility.CloudFileManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class NewFriendInitialAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;
      private RequestedFriend requestedFriend;
      private UserFriendsFragment friendFrag;
      private MainActivity main;

      public NewFriendInitialAsyncTask(UserFriendsFragment frag, RequestedFriend requestedFriend)
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
            // create empty temporal friend wall file on device to upload to cloud
            String fileName = requestedFriend.nonce + "_temp_friend_file.txt";
            String deviceFilepath = Constants.friendsFilePath + "/" + fileName;
            CloudFileManager.createTemporalFriendFile(null, deviceFilepath);

            // upload group wall to cloud and get direct link
            String directlink = main.cloud.uploadFileToCloud(Constants.friendDirectory, fileName, deviceFilepath);

            // create URI
            String URI = "";
            try
               {
                  // replace all + chars in the key to the hex value
                  String publicKey = main.user.publicKey;
                  publicKey = publicKey.replace("+", "%2B");

                  // encode the key to maintain special chars
                  publicKey = URLEncoder.encode(publicKey, "UTF-8");

                  // encode temporal URL to maintain special chars
                  String encodedURL = URLEncoder.encode(directlink, "UTF-8");

                  URI = "http://posn.com/request/" + main.user.ID + "/" + main.user.email + "/" + main.user.firstName + "/" + main.user.lastName.trim()
                            + "/" + publicKey + "/" + encodedURL + "/" + requestedFriend.nonce;
               }
            catch (UnsupportedEncodingException e)
               {
                  e.printStackTrace();
               }

            // send the email the the user (THIS IS BAD TO HARDCODE USERNAME AND PASS
            EmailSender email = new EmailSender("projectcloudbook@gmail.com", "cnlpass!!");
            email.sendMail("POSN - New Friend Request", "SUCCESS!\n\n" + URI, "POSN", requestedFriend.email);


            // add pending friend to request friends list
            main.masterFriendList.friendRequests.add(requestedFriend);

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