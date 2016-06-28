package com.posn.managers;

import android.os.Parcel;
import android.os.Parcelable;

import com.posn.constants.Constants;
import com.posn.datatypes.ApplicationFile;
import com.posn.datatypes.Conversation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * <p>This class creates a list of the current friends the user has a conversation with and are currently stored in an arraylist
 *    (Note: this is not fully implemented and may need to use a hashmap). Methods are included to read and write the data to and from a file.</p>
 * <p>Implements the methods defined by the ApplicationFile interface</p>
 * <p>Implements parcelable to easily pass this class between activities</p>
 **/
public class ConversationManager implements Parcelable, ApplicationFile
   {
      public ArrayList<Conversation> conversations = new ArrayList<>();

      public ConversationManager()
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
      public ConversationManager(Parcel in)
         {
            this.conversations = in.readArrayList(Conversation.class.getClassLoader());

         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeList(this.conversations);

         }

      public static final Parcelable.Creator<ConversationManager> CREATOR = new Parcelable.Creator<ConversationManager>()
         {
            public ConversationManager createFromParcel(Parcel in)
               {
                  return new ConversationManager(in);
               }

            public ConversationManager[] newArray(int size)
               {
                  return new ConversationManager[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }
