package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import com.posn.Constants;
import com.posn.utility.DeviceFileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ConversationList implements Parcelable
   {
      public ArrayList<Conversation> conversations;
      private String deviceFileKey;

      public ConversationList(String deviceFileKey)
         {

            this.deviceFileKey = deviceFileKey;
            conversations = new ArrayList<>();
         }

      public void loadConversationsFromFile()
         {
            // open the file
            try
               {
                  JSONObject data = DeviceFileManager.loadJSONObjectFromFile(Constants.applicationDataFilePath + "/" + Constants.converstationListFile);

                  JSONArray messageList = data.getJSONArray("messages");

                  for (int n = 0; n < messageList.length(); n++)
                     {
                        Conversation conversation = new Conversation();
                        conversation.parseJSONObject(messageList.getJSONObject(n));

                        conversations.add(conversation);
                     }
               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }

      public void saveConversationListToFile()
         {
            JSONArray messagesList = new JSONArray();

            try
               {
                  for (int i = 0; i < conversations.size(); i++)
                     {
                        Conversation conversation = conversations.get(i);
                        messagesList.put(conversation.createJSONObject());
                     }

                  JSONObject object = new JSONObject();
                  object.put("messages", messagesList);

                  DeviceFileManager.writeJSONToFile(object, Constants.applicationDataFilePath + "/" + Constants.converstationListFile);

               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }


      // Parcelling part
      public ConversationList (Parcel in)
         {
            this.conversations = in.readArrayList(Conversation.class.getClassLoader());

         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeList(this.conversations);

         }

      public static final Parcelable.Creator<ConversationList> CREATOR = new Parcelable.Creator<ConversationList>()
         {
            public ConversationList createFromParcel(Parcel in)
               {
                  return new ConversationList(in);
               }

            public ConversationList[] newArray(int size)
               {
                  return new ConversationList[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }
