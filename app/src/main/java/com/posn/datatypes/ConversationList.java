package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import com.posn.utility.DeviceFileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ConversationList implements Parcelable
   {
      public ArrayList<Conversation> conversations;

      public ConversationList()
         {
            conversations = new ArrayList<>();
         }

      public void loadConversationsFromFile(String fileName)
         {
            // open the file
            try
               {
                  JSONObject data = DeviceFileManager.loadJSONObjectFromFile(fileName);

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

      public void saveConversationListToFile(String devicePath)
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

                  DeviceFileManager.writeJSONToFile(object, devicePath);

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

      public static final Parcelable.Creator<Friend> CREATOR = new Parcelable.Creator<Friend>()
         {
            public Friend createFromParcel(Parcel in)
               {
                  return new Friend(in);
               }

            public Friend[] newArray(int size)
               {
                  return new Friend[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }
