package com.posn.asynctasks.wall;


import com.posn.datatypes.WallPost;

import java.util.ArrayList;

public interface AsyncResponseWall
   {
      void loadingWallFinished(ArrayList<WallPost> wallData);

   }
