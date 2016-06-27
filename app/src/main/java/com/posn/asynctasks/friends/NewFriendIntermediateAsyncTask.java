package com.posn.asynctasks.friends;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.posn.Constants;
import com.posn.datatypes.Friend;
import com.posn.datatypes.RequestedFriend;
import com.posn.email.EmailSender;
import com.posn.exceptions.POSNCryptoException;
import com.posn.main.AppDataManager;
import com.posn.main.MainActivity;
import com.posn.main.friends.UserFriendsFragment;
import com.posn.utility.AsymmetricKeyManager;
import com.posn.utility.SymmetricKeyManager;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * This AsyncTask class implements the functionality for the second of three phasesof the friendID request process, where the desired friendID accepts the
 * friendID request and sends their information to the user.
 * <ul><li>Takes in the requested friendID object of the accepted friendID and updates the friends list and creates a friendID file
 * <li>Builds a friendID request URI with the following user data: id, name, email, public key, URL to the friendID file, friendID file key, and two nonce values
 * <li>The URI data is encrypted with a unique symmetric key and the symmetric key is encrypted with the friendID's public key and created into a URI
 * <li>Creates a new email containing the URI and sends it to the desired friendID</ul>
 **/
public class NewFriendIntermediateAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;
      private RequestedFriend requestedFriend;
      private UserFriendsFragment friendFrag;
      private MainActivity main;
      private AppDataManager dataManager;


      public NewFriendIntermediateAsyncTask(UserFriendsFragment frag, RequestedFriend requestedFriend)
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


      protected String doInBackground(String... params)
         {
            try
               {
                  // create a new friendID object from the requested friendID
                  Friend newFriend = dataManager.masterFriendList.addNewAcceptedFriend(requestedFriend, Constants.STATUS_TEMPORAL);

                  // create friendID file with all friendID group data
                  String fileName = requestedFriend.ID + "_friend_file.txt";
                  String deviceFilepath = Constants.friendsFilePath;
                  dataManager.createFriendFile(newFriend.ID, deviceFilepath, fileName);

                  // upload group wall to cloud and get direct link
                  String friendFileLink = main.cloud.uploadFileToCloud(Constants.friendDirectory, fileName, deviceFilepath + "/" + fileName);

                  // create the URI with the user's data for the friendID
                  String URI = createURI(newFriend, friendFileLink);

                  // send the email the the user
                  sendEmailToFriend(URI);

                  // save the friends list to the device
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
            // add them to the list view as an accepted friendID
            friendFrag.updateFriendList();

            // dismiss the dialog once done
            pDialog.dismiss();
         }


      private String createURI(Friend newFriend, String friendFileLink) throws POSNCryptoException, UnsupportedEncodingException
         {
            // replace all + chars in the user's public key to the hex value
            String publicKey = dataManager.user.publicKey;
            publicKey = publicKey.replace("+", "%2B");

            // encode the user's public key to maintain special chars
            publicKey = URLEncoder.encode(publicKey, "UTF-8");

            // encode friendID file URL to maintain special chars
            String encodedURL = URLEncoder.encode(friendFileLink, "UTF-8");
            String encodedFriendFileKey = URLEncoder.encode(newFriend.userFriendFileKey, "UTF-8");

            // create URI
            String URIData = dataManager.user.ID + "/" + dataManager.user.firstName + "/" + dataManager.user.lastName.trim()
                                 + "/" + publicKey + "/" + encodedURL + "/" + encodedFriendFileKey + "/" + requestedFriend.nonce + "/" + requestedFriend.nonce2;

            // generate symmetric key and encrypt URI data (need to encrypt with symmetric because RSA keys cannot handle the size of the URI data)
            String key = SymmetricKeyManager.createRandomKey();
            String encryptedURI = SymmetricKeyManager.encrypt(key, URIData);

            // encrypt the symmetric key with the friendID's public key (to guarantee that only the friendID can access)
            String encryptedKey = AsymmetricKeyManager.encrypt(requestedFriend.publicKey, key);

            // build the final URI to send
            return "http://posn.com/accept/" + encryptedKey + "/" + encryptedURI;
         }


      private void sendEmailToFriend(String URI)
         {
            // BAD TO HARDCODE EMAIL ADDRESS AND PASSWORD
            EmailSender email = new EmailSender("projectcloudbook@gmail.com", "cnlpass!!");
            String body = email.emailBodyFormatter(dataManager.user.firstName + " " + dataManager.user.lastName + " has accepted your friend request!", URI, "Click to Finalize the Request");
            email.sendMail("POSN - Accepted Friend Request", body, "POSN", requestedFriend.email);
         }
   }