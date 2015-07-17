package com.posn.asynctasks;


import com.posn.datatypes.Post;

import java.util.ArrayList;

public interface AsyncResponseWall
   {
      void loadingWallFinished(ArrayList<Post> wallData);

   }
