package com.posn.exceptions;

/**
 * This class provides methods to throw exceptions from crypto Symmetric and Asymmetric functions
 **/
public class POSNCryptoException extends Exception
   {

      public POSNCryptoException(String message)
         {
            super(message);
         }

      public POSNCryptoException(String message, Throwable throwable)
         {
            super(message, throwable);
         }
   }
