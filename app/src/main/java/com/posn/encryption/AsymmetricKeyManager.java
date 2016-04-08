package com.posn.encryption;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class AsymmetricKeyManager
   {

      // variable declarations
      private static PublicKey publicKey;
      private static PrivateKey privateKey;

      public static void setPublicKey(PublicKey pubKey)
         {
            publicKey = pubKey;
         }

      public static void setPrivateKey(PrivateKey privKey)
         {
            privateKey = privKey;
         }


      public static boolean createKeys(int bitLength, int seed)
         {
            try
               {
                  // create a new RSA keypair generator
                  KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

                  // create a secure random number generator using the seed value
                  SecureRandom secureRandom = new SecureRandom();
                  secureRandom.setSeed(seed);

                  // set the RSA keypair generator's bit length and the RNG
                  keyPairGenerator.initialize(bitLength, secureRandom);

                  // generate and store the public and private keys
                  KeyPair keyPair = keyPairGenerator.genKeyPair();
                  publicKey = keyPair.getPublic();
                  System.out.println(publicKey.getFormat());
                  privateKey = keyPair.getPrivate();
                  System.out.println(privateKey.getFormat());

               }
            catch (NoSuchAlgorithmException e)
               {
                  e.printStackTrace();
                  return false;
               }

            return true;
         }


      public static String encrypt(String plain)
         {
            // declare variables
            String encryptedString = null;

            // try to encrypt the string using the public key
            try
               {
                  Cipher cipher = Cipher.getInstance("RSA");
                  cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                  byte[] encryptedBytes = cipher.doFinal(plain.getBytes());

                  encryptedString = bytesToString(encryptedBytes);
               }
            catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e)
               {
                  e.printStackTrace();
               }

            // return the encrypted string
            return encryptedString;
         }


      public static String decrypt(String result)
         {
            // declare variables
            String decryptedString = null;

            // try to decrypt the string using the private key
            try
               {
                  Cipher cipher1 = Cipher.getInstance("RSA");
                  cipher1.init(Cipher.DECRYPT_MODE, privateKey);
                  byte[] decryptedBytes = cipher1.doFinal(stringToBytes(result));
                  decryptedString = new String(decryptedBytes);
               }
            catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e)
               {
                  e.printStackTrace();
               }

            return decryptedString;
         }


      private static String bytesToString(byte[] b)
         {
            byte[] b2 = new byte[b.length + 1];
            b2[0] = 1;
            System.arraycopy(b, 0, b2, 1, b.length);
            return new BigInteger(b2).toString(36);
         }


      private static byte[] stringToBytes(String s)
         {
            byte[] b2 = new BigInteger(s, 36).toByteArray();
            return Arrays.copyOfRange(b2, 1, b2.length);
         }


      public static PublicKey getPublicKey()
         {
            return publicKey;
         }


      public static PrivateKey getPrivateKey()
         {
            return privateKey;
         }

   }
