package com.posn.utility;


import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.Calendar;

/**
 * This class provides methods to generate unique IDs
 **/
public class IDGeneratorHelper
   {
      /**
       * <p>Generates a unique ID based on the initial value and the date/time the ID was created</p>
       *
       * <p>Uses SHA-256 hashing algorithm</p>
       *
       * @param inputString value used to generate IDs
       * @return unique ID string
       **/
      public static String generate(String inputString)
         {
            // get the current
            Calendar c = Calendar.getInstance();
            String timeDate = c.toString();

            HashCode hashCode = Hashing.sha256().hashString(inputString + timeDate, Charset.defaultCharset());

            return hashCode.toString();
         }
   }
