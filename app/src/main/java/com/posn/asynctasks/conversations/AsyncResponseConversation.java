package com.posn.asynctasks.conversations;

import com.posn.datatypes.Message;

import java.util.ArrayList;
import java.util.HashMap;


public interface AsyncResponseConversation
   {
      void loadingConversationFinished(HashMap<String, ArrayList<Message>> messageList);
   }