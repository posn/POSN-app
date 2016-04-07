package com.posn.asynctasks.conversations;

import com.posn.datatypes.Conversation;

import java.util.ArrayList;


public interface AsyncResponseMessages
   {
      void loadingMessagesFinished(ArrayList<Conversation> conversationList);
   }