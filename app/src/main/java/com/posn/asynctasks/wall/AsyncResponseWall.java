package com.posn.asynctasks.wall;


import com.posn.datatypes.Post;

import java.util.ArrayList;

public interface AsyncResponseWall
   {
      void loadingWallFinished(ArrayList<Post> wallData);

   }
