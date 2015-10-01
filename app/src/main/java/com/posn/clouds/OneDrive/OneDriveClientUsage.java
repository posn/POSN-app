package com.posn.clouds.OneDrive;

import android.app.ProgressDialog;
import android.content.Context;

import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;

import java.util.HashMap;

public class OneDriveClientUsage
   {
      public final String CLIENT_ID = "0000000044165317";

      public final String[] SCOPES = { "wl.signin", "wl.basic", "wl.offline_access", "wl.skydrive_update", "wl.contacts_create", };

      public HashMap<String, String> folderIds;

      private Context context;

      public LiveAuthClient mAuthClient;
      public LiveConnectClient mConnectClient;
      public LiveConnectSession mSession;

    //  public static LiveAuthClient mAuthClient;
      private ProgressDialog mInitializeDialog;


      public OneDriveClientUsage(Context context)
         {
            // set the activity context
            this.context = context;

            mAuthClient = new LiveAuthClient(context, CLIENT_ID);
            folderIds = new HashMap<>();
         }

      public void downloadFile(String folder, String fileName, String outputPath)
         {
            new OneDriveDownloadAsyncTask(this, folderIds.get(folder), fileName, outputPath).execute();
         }

      public void uploadFile(String folder, String fileName, String inputPath)
         {
            new OneDriveUploadAsyncTask(this, folderIds.get(folder), fileName, inputPath).execute();
         }

      public void createStorageDirectories()
         {
            new OneDriveCreateDirectoriesAsyncTask(this).execute();
         }



      public LiveAuthClient getAuthClient() {
         return mAuthClient;
      }

      public LiveConnectClient getConnectClient() {
         return mConnectClient;
      }

      public LiveConnectSession getSession() {
         return mSession;
      }

      public void setAuthClient(LiveAuthClient authClient) {
         mAuthClient = authClient;
      }

      public void setConnectClient(LiveConnectClient connectClient) {
         mConnectClient = connectClient;
      }

      public void setSession(LiveConnectSession session) {
         mSession = session;
      }

   }
