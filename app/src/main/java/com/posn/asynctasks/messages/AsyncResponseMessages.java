package com.posn.asynctasks.messages;

import com.posn.datatypes.Message;

import java.util.ArrayList;


public interface AsyncResponseMessages
   {
      void loadingMessagesFinished(ArrayList<Message> messageList);
   }