package com.posn.utility;

import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
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

      public static void encryptFile(String keyString, String inputPath, String outputPath)
         {
            try
               {
                  // Here you read the cleartext.
                  FileInputStream fis = new FileInputStream(inputPath);
                  // This stream write the encrypted text. This stream will be wrapped by another stream.
                  FileOutputStream fos = new FileOutputStream(outputPath);

                  // get secret key from string
                  byte[] encoded = stringToBytes(keyString);
                  SecretKey key = new SecretKeySpec(encoded, "AES");


                  // Create cipher
                  Cipher cipher = Cipher.getInstance("AES");
                  cipher.init(Cipher.ENCRYPT_MODE, key);
                  // Wrap the output stream
                  CipherOutputStream cos = new CipherOutputStream(fos, cipher);
                  // Write bytes
                  int b;
                  byte[] d = new byte[1024 * 1024];
                  while ((b = fis.read(d)) != -1)
                     {
                        cos.write(d, 0, b);
                     }
                  // Flush and close streams.
                  cos.flush();
                  cos.close();
                  fis.close();
               }
            catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e)
               {
                  e.printStackTrace();
               }
         }


      public static String encrypt(String keyString, String plainText)
         {
            byte[] encryptedBytes = encrypt(keyString, plainText.getBytes());

            // convert the bytes to a string
            return bytesToString(encryptedBytes);
         }


      public static byte[] encrypt(String keyString, byte[] plainText)
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
                  return encryptCipher.doFinal(plainText);

               }
            catch (BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e)
               {
                  e.printStackTrace();
               }

            return null;
         }


      public static byte[] decryptFile(String keyString, String inputPath)
         {
            try
               {

                  FileInputStream fis = new FileInputStream(inputPath);

                  // get secret key from string
                  byte[] encoded = stringToBytes(keyString);
                  SecretKey key = new SecretKeySpec(encoded, "AES");

                  Cipher cipher = Cipher.getInstance("AES");
                  cipher.init(Cipher.DECRYPT_MODE, key);
                  CipherInputStream cis = new CipherInputStream(fis, cipher);

                  byte[] contents = new byte[cis.available()];

                  int status = cis.read(contents);

                  cis.close();

                  return contents;
               }
            catch (IOException e)
               {
                  e.printStackTrace();
                  Log.e("AES Encrypt", "IO EXCEPTION");
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
            return null;
         }


      public static void decryptFile(String keyString, String inputPath, String outputPath)
         {
            try
               {

                  FileInputStream fis = new FileInputStream(inputPath);

                  FileOutputStream fos = new FileOutputStream(outputPath);

                  // get secret key from string
                  byte[] encoded = stringToBytes(keyString);
                  SecretKey key = new SecretKeySpec(encoded, "AES");

                  Cipher cipher = Cipher.getInstance("AES");
                  cipher.init(Cipher.DECRYPT_MODE, key);
                  CipherInputStream cis = new CipherInputStream(fis, cipher);
                  int b;
                  byte[] d = new byte[1024 * 1024];
                  while ((b = cis.read(d)) != -1)
                     {
                        fos.write(d, 0, b);
                     }
                  fos.flush();
                  fos.close();
                  cis.close();
               }
            catch (IOException e)
               {
                  e.printStackTrace();
                  Log.e("AES Encrypt", "IO EXCEPTION");
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
         }


      public static String decrypt(String keyString, String encryptedText)
         {
            byte[] plainText = decrypt(keyString, stringToBytes(encryptedText));

            // convert the byte array to a string
            return new String(plainText);
         }


      public static byte[] decrypt(String keyString, byte[] encryptedText)
         {
            // variable declaration
            Cipher decryptCipher;

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
                  return decryptCipher.doFinal(encryptedText);
               }
            catch (BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e)
               {
                  e.printStackTrace();
               }

            return null;
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
