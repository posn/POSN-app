package com.posn.encryption;

import android.util.Base64;
import android.util.Pair;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class AsymmetricKeyManager
   {
      public static Pair<String, String> generateKeys(int bitLength, int seed)
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
                  PublicKey publicKey = keyPair.getPublic();
                  System.out.println(publicKey.getFormat());
                  PrivateKey privateKey = keyPair.getPrivate();
                  System.out.println(privateKey.getFormat());

                  byte[] publicKeyBytes = publicKey.getEncoded();
                  String pubKeyString = Base64.encodeToString(publicKeyBytes, Base64.DEFAULT);

                  byte[] privateKeyBytes = privateKey.getEncoded();
                  String privKeyString = Base64.encodeToString(privateKeyBytes, Base64.DEFAULT);

                  return Pair.create(pubKeyString, privKeyString);
               }
            catch (NoSuchAlgorithmException e)
               {
                  e.printStackTrace();
               }
            return null;
         }


      public static String encrypt(String publicKey, String plain)
         {
            // declare variables
            String encryptedString = null;

            // try to encrypt the string using the public key
            try
               {
                  byte[] publicKeyByte = Base64.decode(publicKey, Base64.DEFAULT);
                  X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyByte);
                  KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                  PublicKey key = keyFactory.generatePublic(spec);

                  Cipher cipher = Cipher.getInstance("RSA");
                  cipher.init(Cipher.ENCRYPT_MODE, key);
                  byte[] encryptedBytes = cipher.doFinal(plain.getBytes());

                  encryptedString = bytesToString(encryptedBytes);
               }
            catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e)
               {
                  e.printStackTrace();
               }

            // return the encrypted string
            return encryptedString;
         }


      public static String decrypt(String privateKey, String result)
         {
            // declare variables
            String decryptedString = null;

            // try to decrypt the string using the private key
            try
               {
                  byte[] privateKeyBytes = Base64.decode(privateKey, Base64.DEFAULT);
                  PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                  KeyFactory fact = KeyFactory.getInstance("RSA");
                  PrivateKey key = fact.generatePrivate(keySpec);

                  Cipher cipher1 = Cipher.getInstance("RSA");
                  cipher1.init(Cipher.DECRYPT_MODE, key);
                  byte[] decryptedBytes = cipher1.doFinal(stringToBytes(result));
                  decryptedString = new String(decryptedBytes);
               }
            catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e)
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


   }
