package com.posn.encryption;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class SymmetricKeyManager
   {


      public static String createKeyFromString(String password)
         {
            SecretKey key = create128BitPassword(password);
            return bytesToString(key.getEncoded());
         }

      public static String createRandomKey()
         {
            String stringKey = null;
            try
               {
                  SecretKey key;
                  SecureRandom rand = new SecureRandom();
                  KeyGenerator generator = KeyGenerator.getInstance("AES");
                  generator.init(rand);
                  generator.init(256);
                  key = generator.generateKey();
                  stringKey = bytesToString(key.getEncoded());
               }
            catch (NoSuchAlgorithmException e)
               {
                  e.printStackTrace();
               }


            return stringKey;
         }


      public static String encrypt(String keyString, String plainText)
         {
            // variable declaration
            String encryptedString = null;

            // get secret key from string
            byte[] encoded = stringToBytes(keyString);
            SecretKey key = new SecretKeySpec(encoded, "AES");

            // try to encrypt the plaintext
            try
               {
                  // create a new AES encrypt cipher with the key
                  Cipher encryptCipher = Cipher.getInstance("AES");
                  encryptCipher.init(Cipher.ENCRYPT_MODE, key);

                  // encrypt the string
                  byte[] encryptedBytes = encryptCipher.doFinal(plainText.getBytes());

                  // convert the bytes to a string
                  encryptedString = bytesToString(encryptedBytes);
               }
            catch (NoSuchAlgorithmException e)
               {
                  e.printStackTrace();
                  Log.e("AES Encrypt", "no such algorithm");
               }
            catch (NoSuchPaddingException e)
               {
                  e.printStackTrace();
                  Log.e("AES Encrypt", "no such padding");
               }
            catch (InvalidKeyException e)
               {
                  e.printStackTrace();
                  Log.e("AES Encrypt", "invalid key");
               }
            catch (IllegalBlockSizeException e)
               {
                  e.printStackTrace();
                  Log.e("AES Encrypt", "invalid block size");
               }
            catch (BadPaddingException e)
               {
                  e.printStackTrace();
                  Log.e("AES Encrypt", "bad padding");
               }

            return encryptedString;
         }


      public static String decrypt(String keyString, String encryptedText)
         {

            // variable declaration
            Cipher decryptCipher;
            String decryptedString = null;

            // get secret key from string
            byte[] encoded = stringToBytes(keyString);
            SecretKey key = new SecretKeySpec(encoded, "AES");

            // try to decrypt the encrypted text
            try
               {
                  // create a new AES decrypt cipher with the key

                  decryptCipher = Cipher.getInstance("AES");
                  decryptCipher.init(Cipher.DECRYPT_MODE, key);

                  // decrypt the string
                  byte[] decryptedBytes = decryptCipher.doFinal(stringToBytes(encryptedText));

                  // convert the byte array to a string
                  decryptedString = new String(decryptedBytes);
               }
            catch (NoSuchAlgorithmException e)
               {
                  e.printStackTrace();
                  Log.e("AES Decrypt", "no such algorithm");
               }
            catch (NoSuchPaddingException e)
               {
                  e.printStackTrace();
                  Log.e("AES Decrypt", "no such apadding");
               }
            catch (InvalidKeyException e)
               {
                  e.printStackTrace();
                  Log.e("AES Decrypt", "invalid key");
               }
            catch (IllegalBlockSizeException e)
               {
                  e.printStackTrace();
                  Log.e("AES Decrypt", "IllegalBlockSizeException");
               }
            catch (BadPaddingException e)
               {
                  e.printStackTrace();
                  Log.e("AES Decrypt", "BadPaddingException");
               }

            return decryptedString;
         }


      private static SecretKey create128BitPassword(String password)
         {
            // declare variables
            int length, keyLength = 128;
            byte[] keyBytes = new byte[keyLength / 8];
            byte[] passwordBytes;
            SecretKeySpec key = null;

            // explicitly fill with zeros
            Arrays.fill(keyBytes, (byte) 0x0);

            // if password is shorter then key length, it will be zero-padded to key length
            try
               {
                  // get the bytes from the password
                  passwordBytes = password.getBytes("UTF-8");

                  // get whichever length is shorter
                  if (passwordBytes.length < keyBytes.length)
                     {
                        length = passwordBytes.length;
                     }
                  else
                     {
                        length = keyBytes.length;
                     }

                  // copy the password bytes into the key bytes
                  System.arraycopy(passwordBytes, 0, keyBytes, 0, length);

                  // create a new key
                  key = new SecretKeySpec(keyBytes, "AES");

               }
            catch (UnsupportedEncodingException e)
               {
                  e.printStackTrace();
               }
            return key;
         }


      private static byte[] stringToBytes(String s)
         {
            byte[] b2 = new BigInteger(s, 36).toByteArray();
            return Arrays.copyOfRange(b2, 1, b2.length);
         }

      private static String bytesToString(byte[] b)
         {
            byte[] b2 = new byte[b.length + 1];
            b2[0] = 1;
            System.arraycopy(b, 0, b2, 1, b.length);
            return new BigInteger(b2).toString(36);
         }

   }
