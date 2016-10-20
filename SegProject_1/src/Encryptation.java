import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Encryptation 
{
	//Generate asymetric keypair
			public static KeyPair generateAsymmetricKey(int keySize) throws NoSuchAlgorithmException, IOException
			{
				KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
				kpg.initialize(keySize);
				KeyPair KPair = kpg.generateKeyPair();
				return KPair;
			}
			
			//Generate symetric key by client id
			public static SecretKey generateAESKey() throws NoSuchAlgorithmException
			{
				KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		        keyGen.init(128);
		        SecretKey secretKey = keyGen.generateKey();
		        return secretKey;
			}
			

			public static SecretKey decrypt(String text, PrivateKey key) throws IOException 
			{
				byte[] textByte = new BASE64Decoder().decodeBuffer(text);
				byte[] dectyptedText = null;
				SecretKey originalAESKey = null;
				try 
				{
					// get an RSA cipher object and print the provider
				    final Cipher cipher = Cipher.getInstance("RSA");

				    // decrypt the text using the private key
				    cipher.init(Cipher.DECRYPT_MODE, key);
				    dectyptedText = cipher.doFinal(textByte);
				    byte[] secretKey = Base64.getDecoder().decode(dectyptedText);
			        originalAESKey = new SecretKeySpec(secretKey, 0, secretKey.length, "AES");
				    } catch (Exception ex) {
				      ex.printStackTrace();
				    }

					return originalAESKey;
				 }
			
			public static String encrypt(String text, String keyStr) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException 
			{
				 PublicKey key = convertStringToPublicKey(keyStr);
				 String secretEncryptedByRSAasStr;
				 byte[] cipherText = null;
				 try 
				 {
					 // get an RSA cipher object and print the provider
					 final Cipher cipher = Cipher.getInstance("RSA");
					 // encrypt the plain text using the public key
					 cipher.init(Cipher.ENCRYPT_MODE, key);
					 cipherText = cipher.doFinal(text.getBytes());
				 } catch (Exception e) {
					 e.printStackTrace();
				 }
				 secretEncryptedByRSAasStr = new BASE64Encoder().encode(cipherText);
				 return secretEncryptedByRSAasStr;
			}
			
			public static String encryptAES(String strDataToEncrypt, SecretKey secretKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
			{
				Cipher aesCipher = Cipher.getInstance("AES");
				aesCipher.init(Cipher.ENCRYPT_MODE,secretKey);
		        byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
		        byte[] byteCipherText = aesCipher.doFinal(byteDataToEncrypt); 
		        String strCipherText = new BASE64Encoder().encode(byteCipherText);
		        return strCipherText;
			}
			public static String decryptAES(String strCipherText, SecretKey secretKey) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException, InvalidKeyException, InvalidAlgorithmParameterException
			{
				Cipher aesCipher = Cipher.getInstance("AES");
				aesCipher.init(Cipher.DECRYPT_MODE,secretKey,aesCipher.getParameters());
		        byte[] byteDecryptedText = aesCipher.doFinal(new BASE64Decoder().decodeBuffer(strCipherText));
		        String strDecryptedText = new String(byteDecryptedText);
		        return strDecryptedText;
			}
			public static String convertPublicKeyToString(PublicKey pk)
			{
				return Base64.getEncoder().encodeToString(pk.getEncoded());
			}
			public static PublicKey convertStringToPublicKey(String pkStr) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
			{
				BASE64Decoder decoder = new BASE64Decoder();
			    byte[] decodedBytes = decoder.decodeBuffer(pkStr);
		        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(decodedBytes);
		        KeyFactory kf = KeyFactory.getInstance("RSA");
		        PublicKey pk = kf.generatePublic(X509publicKey);
		        return pk;
			}
			public static String convertAESkeyToString(SecretKey secret)
			{
				return Base64.getEncoder().encodeToString(secret.getEncoded());
			}
			public static SecretKey convertStringToAESkey(String key)
			{
				byte[] decodedKey = Base64.getDecoder().decode(key);
				SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
				return originalKey;
			}
}
