package com.posn.datatypes;

import com.posn.utility.FileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ConversationList
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
                  JSONObject data = FileManager.loadJSONObjectFromFile(fileName);

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

                  FileManager.writeJSONToFile(object, devicePath);

               }
            catch (JSONException e)
               {
                  e.printStackTrace();
               }
         }
   }
