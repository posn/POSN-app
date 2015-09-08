package com.posn.asynctasks.messages;

import com.posn.datatypes.ConversationMessage;

import java.util.ArrayList;


public interface AsyncResponseConversation
   {
      void loadingConversationFinished(ArrayList<ConversationMessage> messageList);
   }