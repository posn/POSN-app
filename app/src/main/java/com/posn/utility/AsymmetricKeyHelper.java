package com.posn.utility;

import android.util.Base64;
import android.util.Pair;

import com.posn.exceptions.POSNCryptoException;

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

/**
 * This class provides methods to create asymmetric keys and encrypt/decrypt string values (symmetric keys)
 **/

public class AsymmetricKeyHelper
   {
      /**
       * Generates a new public/private key pair from a given length and key
       *
       * @param bitLength number of bits the generated key should have
       * @param seed      value used to set the seed of the secure random generator
       * @return string pair for public key (first) and private key (second)
       * @throws POSNCryptoException
       **/
      public static Pair<String, String> generateKeys(int bitLength, int seed) throws POSNCryptoException
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
                  PrivateKey privateKey = keyPair.getPrivate();

                  byte[] publicKeyBytes = publicKey.getEncoded();
                  String pubKeyString = Base64.encodeToString(publicKeyBytes, Base64.DEFAULT);

                  byte[] privateKeyBytes = privateKey.getEncoded();
                  String privKeyString = Base64.encodeToString(privateKeyBytes, Base64.DEFAULT);

                  return Pair.create(pubKeyString, privKeyString);
               }
            catch (NoSuchAlgorithmException error)
               {
                  throw new POSNCryptoException("AsymmetricKeyManager - generateKeys", error);
               }
         }

      /**
       * Encrypts a plaintext string using the public key and returns the ciphertext string
       *
       * @param publicKey public key used to encrypt the plaintext
       * @param plaintext plaintext data
       * @return ciphertext string
       * @throws POSNCryptoException
       **/
      public static String encrypt(String publicKey, String plaintext) throws POSNCryptoException
         {
            try
               {
                  // encrypt the string using the public key
                  byte[] publicKeyByte = Base64.decode(publicKey, Base64.DEFAULT);
                  X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyByte);
                  KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                  PublicKey key = keyFactory.generatePublic(spec);

                  Cipher cipher = Cipher.getInstance("RSA");
                  cipher.init(Cipher.ENCRYPT_MODE, key);
                  byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());

                  // return the encrypted string
                  return bytesToString(encryptedBytes);
               }
            catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException error)
               {
                  throw new POSNCryptoException("AsymmetricKeyManager - encrypt", error);
               }
         }


      /**
       * Decrypts a ciphertext string using the private key and returns the plaintext string
       *
       * @param privateKey private key used to decrypt the ciphertext
       * @param cipherText ciphertext data
       * @return string of the decrypted plaintext
       * @throws POSNCryptoException
       **/
      public static String decrypt(String privateKey, String cipherText) throws POSNCryptoException
         {
            try
               {
                  // decrypt the string using the private key
                  byte[] privateKeyBytes = Base64.decode(privateKey, Base64.DEFAULT);
                  PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                  KeyFactory fact = KeyFactory.getInstance("RSA");
                  PrivateKey key = fact.generatePrivate(keySpec);

                  Cipher cipher1 = Cipher.getInstance("RSA");
                  cipher1.init(Cipher.DECRYPT_MODE, key);
                  byte[] decryptedBytes = cipher1.doFinal(stringToBytes(cipherText));

                  return new String(decryptedBytes);
               }
            catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException error)
               {
                  throw new POSNCryptoException("AsymmetricKeyManager - encrypt", error);
               }
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


   }
