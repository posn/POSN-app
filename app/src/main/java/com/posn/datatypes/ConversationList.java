package com.posn.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import com.posn.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ConversationList implements Parcelable, ApplicationFile
   {
      public ArrayList<Conversation> conversations = new ArrayList<>();

      public ConversationList()
         {
         }


      @Override
      public void parseApplicationFileContents(String fileContents) throws JSONException
         {
            JSONObject data = new JSONObject(fileContents);

            JSONArray messageList = data.getJSONArray("messages");

            for (int n = 0; n < messageList.length(); n++)
               {
                  Conversation conversation = new Conversation();
                  conversation.parseJSONObject(messageList.getJSONObject(n));

                  conversations.add(conversation);
               }
         }

      @Override
      public String getDirectoryPath()
         {
            return Constants.applicationDataFilePath;
         }

      @Override
      public String getFileName()
         {
            return Constants.converstationListFile;
         }

      @Override
      public String createApplicationFileContents() throws JSONException
         {
            JSONArray messagesList = new JSONArray();


            for (int i = 0; i < conversations.size(); i++)
               {
                  Conversation conversation = conversations.get(i);
                  messagesList.put(conversation.createJSONObject());
               }

            JSONObject object = new JSONObject();
            object.put("messages", messagesList);

            return object.toString();
         }


      // Parcelling part
      public ConversationList(Parcel in)
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
