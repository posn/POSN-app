package com.posn.asynctasks.friends;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.datatypes.RequestedFriend;
import com.posn.email.EmailSender;
import com.posn.exceptions.POSNCryptoException;
import com.posn.main.MainActivity;
import com.posn.main.friends.UserFriendsFragment;
import com.posn.main.AppDataManager;

import org.json.JSONException;

import java.io.IOException;
import java.net.URLEncoder;


public class NewFriendInitialAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;
      private RequestedFriend requestedFriend;
      private UserFriendsFragment friendFrag;
      private MainActivity main;
      private AppDataManager dataManager;

      public NewFriendInitialAsyncTask(UserFriendsFragment frag, RequestedFriend requestedFriend)
         {
            super();
            friendFrag = frag;
            this.requestedFriend = requestedFriend;
            main = friendFrag.activity;
            dataManager = main.dataManager;
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
            try
               {
                  // create empty temporal friend wall file on device to upload to cloud
                  String fileName = requestedFriend.nonce + "_temp_friend_file.txt";
                  String deviceFilepath = Constants.friendsFilePath;
                  dataManager.createTemporalFriendFile(null, deviceFilepath, fileName);

                  // upload group wall to cloud and get direct link
                  String directLink = main.cloud.uploadFileToCloud(Constants.friendDirectory, fileName, deviceFilepath + "/" + fileName);

                  // create URI
                  String URI;

                  // replace all + chars in the key to the hex value
                  String publicKey = dataManager.user.publicKey;
                  publicKey = publicKey.replace("+", "%2B");

                  // encode the key to maintain special chars
                  publicKey = URLEncoder.encode(publicKey, "UTF-8");

                  // encode temporal URL to maintain special chars
                  String encodedURL = URLEncoder.encode(directLink, "UTF-8");

                  URI = "http://posn.com/request/" + dataManager.user.ID + "/" + dataManager.user.email + "/" + dataManager.user.firstName + "/" + dataManager.user.lastName.trim()
                            + "/" + publicKey + "/" + encodedURL + "/" + requestedFriend.nonce;


                  // send the email the the user (THIS IS BAD TO HARDCODE USERNAME AND PASS)
                  EmailSender email = new EmailSender("projectcloudbook@gmail.com", "cnlpass!!");
                  String body = email.emailBodyFormatter(dataManager.user.firstName + " " +  dataManager.user.lastName + " wants to be your friend in POSN!", URI, "Click to Respond to the Request");
                  email.sendMail("POSN - New Friend Request", body, "POSN", requestedFriend.email);


                  // add pending friend to request friends list
                  dataManager.masterFriendList.friendRequests.add(requestedFriend);

                  dataManager.saveFriendListAppFile(false);
               }
            catch (JSONException | IOException | POSNCryptoException error)
               {
                  error.printStackTrace();
               }

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