package com.posn.asynctasks.messages;

import com.posn.datatypes.ConversationMessage;

import java.util.ArrayList;
import java.util.HashMap;


public interface AsyncResponseConversation
   {
      void loadingConversationFinished(HashMap<String, ArrayList<ConversationMessage>> messageList);
   }