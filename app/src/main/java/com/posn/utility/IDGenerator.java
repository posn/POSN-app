package com.posn.utility;


import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.Calendar;

public class IDGenerator
   {
      public static String generate(String seed)
         {
            Calendar c = Calendar.getInstance();
            String timeDate = c.toString();

            HashCode hashCode = Hashing.sha256().hashString(seed + timeDate, Charset.defaultCharset());

            return hashCode.toString();
         }
   }
