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


public class RSAEncryption
	{

		// variable declarations
		private KeyPairGenerator keyPairGenerator;
		private KeyPair keyPair;
		private PublicKey publicKey;
		private PrivateKey privateKey;
		private byte[] encryptedBytes, decryptedBytes;
		private Cipher cipher, cipher1;
		private SecureRandom secureRandom;


		public void setPublicKey(PublicKey publicKey)
			{
				this.publicKey = publicKey;
			}


		public void setPrivateKey(PrivateKey privateKey)
			{
				this.privateKey = privateKey;
			}


		public boolean createRSAKeys(int bitLength, int seed)
			{
				try
					{
						// create a new RSA keypair generator
						keyPairGenerator = KeyPairGenerator.getInstance("RSA");

						// create a secure random number generator using the seed value
						secureRandom = new SecureRandom();
						secureRandom.setSeed(seed);

						// set the RSA keypair generator's bit length and the RNG
						keyPairGenerator.initialize(bitLength, secureRandom);

						// generate and store the public and private keys
						keyPair = keyPairGenerator.genKeyPair();
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


		public String Encrypt(String plain)
			{
				// declare variables
				String encryptedString = null;

				// try to encrypt the string using the public key
				try
					{
						cipher = Cipher.getInstance("RSA");
						cipher.init(Cipher.ENCRYPT_MODE, publicKey);
						encryptedBytes = cipher.doFinal(plain.getBytes());

						encryptedString = bytesToString(encryptedBytes);
					}
				catch (NoSuchAlgorithmException e)
					{
						e.printStackTrace();
					}
				catch (InvalidKeyException e)
					{
						e.printStackTrace();
					}
				catch (NoSuchPaddingException e)
					{
						e.printStackTrace();
					}
				catch (IllegalBlockSizeException e)
					{
						e.printStackTrace();
					}
				catch (BadPaddingException e)
					{
						e.printStackTrace();
					}

				// return the encrypted string
				return encryptedString;
			}


		public String Decrypt(String result)
			{
				// declare variables
				String decryptedString = null;

				// try to decrypt the string using the private key
				try
					{
						cipher1 = Cipher.getInstance("RSA");
						cipher1.init(Cipher.DECRYPT_MODE, privateKey);
						decryptedBytes = cipher1.doFinal(stringToBytes(result));
						decryptedString = new String(decryptedBytes);
					}
				catch (NoSuchAlgorithmException e)
					{
						e.printStackTrace();
					}
				catch (NoSuchPaddingException e)
					{
						e.printStackTrace();
					}
				catch (InvalidKeyException e)
					{
						e.printStackTrace();
					}
				catch (IllegalBlockSizeException e)
					{
						e.printStackTrace();
					}
				catch (BadPaddingException e)
					{
						e.printStackTrace();
					}

				return decryptedString;

			}


		public String bytesToString(byte[] b)
			{
				byte[] b2 = new byte[b.length + 1];
				b2[0] = 1;
				System.arraycopy(b, 0, b2, 1, b.length);
				return new BigInteger(b2).toString(36);
			}


		public byte[] stringToBytes(String s)
			{
				byte[] b2 = new BigInteger(s, 36).toByteArray();
				return Arrays.copyOfRange(b2, 1, b2.length);
			}


		public PublicKey getPublicKey()
			{
				return publicKey;
			}


		public PrivateKey getPrivateKey()
			{
				return privateKey;
			}

	}
