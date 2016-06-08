package com.posn.utility;

import com.posn.exceptions.POSNCryptoException;

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


/**
 * This class provides methods to create symmetric keys and encrypt/decrypt files or input values
 **/

public class SymmetricKeyManager
   {

      /**
       * Generates a new symmetric key from an initial input string
       *
       * @param input inital value to create 128 bit key, padding added if input less than 128 bits
       * @throws POSNCryptoException
       **/
      public static String createKeyFromString(String input) throws POSNCryptoException
         {
            SecretKey key = create128BitPassword(input);
            return bytesToString(key.getEncoded());
         }


      /**
       * Generates a new symmetric key (256 bits) from a secure random value
       *
       * @throws POSNCryptoException
       **/
      public static String createRandomKey() throws POSNCryptoException
         {
            try
               {
                  SecretKey key;
                  SecureRandom rand = new SecureRandom();
                  KeyGenerator generator = KeyGenerator.getInstance("AES");
                  generator.init(rand);
                  generator.init(256);
                  key = generator.generateKey();

                  return bytesToString(key.getEncoded());
               }
            catch (NoSuchAlgorithmException error)
               {
                  throw new POSNCryptoException("SymmetricKeyManager - createRandomKey", error);
               }
         }

      /**
       * Encrypts a plaintext file on the device. Creates a new separate encrypted file
       *
       * @param encryptKey     key used to encrypt the file
       * @param inputFilePath  device file path to the plaintext file
       * @param outputFilePath device file path to created encrypted file
       * @throws POSNCryptoException
       **/
      public static void encryptFile(String encryptKey, String inputFilePath, String outputFilePath) throws POSNCryptoException
         {
            try
               {
                  // create an input stream to read in the plaintext file
                  FileInputStream fis = new FileInputStream(inputFilePath);

                  // create an output stream for the encrypted file
                  FileOutputStream fos = new FileOutputStream(outputFilePath);

                  // create a secret key from the key string
                  byte[] encoded = stringToBytes(encryptKey);
                  SecretKey key = new SecretKeySpec(encoded, "AES");

                  // create cipher
                  Cipher cipher = Cipher.getInstance("AES");
                  cipher.init(Cipher.ENCRYPT_MODE, key);

                  // wrap the output stream
                  CipherOutputStream cos = new CipherOutputStream(fos, cipher);

                  // write bytes to the file
                  int b;
                  byte[] d = new byte[1024 * 1024];
                  while ((b = fis.read(d)) != -1)
                     {
                        cos.write(d, 0, b);
                     }

                  // flush and close streams
                  cos.flush();
                  cos.close();
                  fis.close();
               }
            catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException error)
               {
                  throw new POSNCryptoException("SymmetricKeyManager - encryptFile", error);
               }

         }

      /**
       * Encrypts a string of plaintext and returns a string of the encrypted data
       *
       * @param encryptKey key used to encrypt the file
       * @param plainText  plaintext data
       * @return string of encrypted data
       * @throws POSNCryptoException
       **/
      public static String encrypt(String encryptKey, String plainText) throws POSNCryptoException
         {
            byte[] encryptedBytes = encrypt(encryptKey, plainText.getBytes());

            // convert the bytes to a string
            return bytesToString(encryptedBytes);
         }

      /**
       * Encrypts a byte array of plaintext and returns a byte array of the encrypted data
       *
       * @param encryptKey key used to encrypt the file
       * @param plainText  plaintext data
       * @return byte array of encrypted data
       * @throws POSNCryptoException
       **/
      private static byte[] encrypt(String encryptKey, byte[] plainText) throws POSNCryptoException
         {
            try
               {
                  // get secret key from string
                  byte[] encoded = stringToBytes(encryptKey);
                  SecretKey key = new SecretKeySpec(encoded, "AES");

                  // create a new AES encrypt cipher with the key
                  Cipher encryptCipher = Cipher.getInstance("AES");
                  encryptCipher.init(Cipher.ENCRYPT_MODE, key);

                  // encrypt the string
                  return encryptCipher.doFinal(plainText);
               }
            catch (BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException error)
               {
                  throw new POSNCryptoException("SymmetricKeyManager - encrypt (byte array)", error);
               }
         }

      /**
       * Decrypts an encrypted file on the device and returns a byte array of plaintext
       *
       * @param decryptKey    decryption key
       * @param inputFilePath device file path to the plaintext file
       * @return byte array of plaintext
       * @throws POSNCryptoException
       **/
      public static byte[] decryptFile(String decryptKey, String inputFilePath) throws POSNCryptoException
         {
            try
               {
                  FileInputStream fis = new FileInputStream(inputFilePath);

                  // get secret key from string
                  byte[] encoded = stringToBytes(decryptKey);
                  SecretKey key = new SecretKeySpec(encoded, "AES");

                  Cipher cipher = Cipher.getInstance("AES");
                  cipher.init(Cipher.DECRYPT_MODE, key);
                  CipherInputStream cis = new CipherInputStream(fis, cipher);

                  byte[] contents = new byte[cis.available()];

                  cis.read(contents);
                  cis.close();

                  return contents;
               }
            catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException error)
               {
                  throw new POSNCryptoException("SymmetricKeyManager - decryptFile", error);
               }
         }


      /**
       * Decrypts an encrypted string and returns the plaintext string
       *
       * @param decryptKey    decryption key
       * @param encryptedText string of enecrypted data
       * @return string of plaintext
       * @throws POSNCryptoException
       **/
      public static String decrypt(String decryptKey, String encryptedText) throws POSNCryptoException
         {
            byte[] plainText = decrypt(decryptKey, stringToBytes(encryptedText));

            // convert the byte array to a string
            return new String(plainText);
         }

      /**
       * Decrypts an encrypted string and returns a byte array of plaintext
       *
       * @param decryptKey    decryption key
       * @param encryptedText byte array of encrypted data
       * @return byte array of plaintext
       * @throws POSNCryptoException
       **/
      public static byte[] decrypt(String decryptKey, byte[] encryptedText) throws POSNCryptoException
         {
            try
               {
                  // variable declaration
                  Cipher decryptCipher;

                  // get secret key from string
                  byte[] encoded = stringToBytes(decryptKey);
                  SecretKey key = new SecretKeySpec(encoded, "AES");


                  // create a new AES decrypt cipher with the key
                  decryptCipher = Cipher.getInstance("AES");
                  decryptCipher.init(Cipher.DECRYPT_MODE, key);

                  // decrypt the string
                  return decryptCipher.doFinal(encryptedText);

               }
            catch (BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException error)
               {
                  throw new POSNCryptoException("SymmetricKeyManager - decrypt (byte array)", error);
               }

         }

      /**
       * Generates a 128 bit Secret Key from an input password. Adds padding if input is less than 128 bits
       *
       * @param password input password
       * @return generated secret key
       * @throws POSNCryptoException
       **/

      private static SecretKey create128BitPassword(String password) throws POSNCryptoException
         {
            try
               {
                  // declare variables
                  int length, keyLength = 128;
                  byte[] keyBytes = new byte[keyLength / 8];
                  byte[] passwordBytes;

                  // explicitly fill with zeros
                  Arrays.fill(keyBytes, (byte) 0x0);

                  // if password is shorter then key length, it will be zero-padded to key length

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
                  return new SecretKeySpec(keyBytes, "AES");
               }
            catch (UnsupportedEncodingException error)
               {
                  throw new POSNCryptoException("SymmetricKeyManager - create128BitPassword", error);
               }
         }


      /**
       * Converts a string to byte array
       *
       * @param inputString input string
       * @return byte array of the string
       **/
      private static byte[] stringToBytes(String inputString)
         {
            byte[] b2 = new BigInteger(inputString, 36).toByteArray();
            return Arrays.copyOfRange(b2, 1, b2.length);
         }

      /**
       * Converts a byte array to a string
       *
       * @param inputByteArray input byte array
       * @return string of the byte array
       **/
      private static String bytesToString(byte[] inputByteArray)
         {
            byte[] b2 = new byte[inputByteArray.length + 1];
            b2[0] = 1;
            System.arraycopy(inputByteArray, 0, b2, 1, inputByteArray.length);
            return new BigInteger(b2).toString(36);
         }
   }
