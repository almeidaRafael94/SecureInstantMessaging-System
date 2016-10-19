import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
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

public class testEncryptDecrypt {

	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException 
	{        
		//test RSA single
		String initial = "RafaelAlmeida";
		KeyPair asymmetricKey = generateAsymmetricKey(1024);
		byte[] symEncoded = encrypt(initial, asymmetricKey.getPublic());
	    String symDecoded =  decrypt(symEncoded, asymmetricKey.getPrivate());
	    if(symDecoded.equals(initial))
	    	System.out.println("RSA SUCESSFUL");
	    
	    //test AES single
	    SecretKey sk = generateAESKey();
	    String encAEStext = encryptAES(initial,sk);
	    String decAEStext =  decryptAES(encAEStext,sk);
	    if(decAEStext.equals(initial))
	    	System.out.println("AES SUCESSFUL");
	    
	    
	    //test completed flux
	    //Situation: Client1 (sender) send client-connect to Client 2 (receiver)
	    
	    //Client 1 connects to the server and publish yours public key
	    KeyPair keyRSA = generateAsymmetricKey(1024);
	    
	    //Client1 (sender) create text to send
	    String textToCypher = "RafaelAlmeida311094";
	    // Client1 (sender) send request (cmd = "client-connect")
	    
	    // Client 2 receives client1 request to establish connection
	    // Client 2 generate secret key
	    SecretKey secret = generateAESKey();
	    
	    // Client2 get Clinet1 public key 
	    // Client2 encrypt secret key with client 1 public key
	    byte[] secretEncryptedByRSA = encrypt(Base64.getEncoder().encodeToString(secret.getEncoded()), keyRSA.getPublic());
	    
	    //Client 2 convert secretEncryptedByRSA in String to send for other client in string format
	    String secretEncryptedByRSAasStr = new BASE64Encoder().encode(secretEncryptedByRSA);
	    // Client 2 send secret key encrypted used Client 1 public key to Client 1

	    
	    //Client 1 receives secret key encrypted 
	    //Client 1 convert secret key encrypted to byte array
	    byte[] secretEncryptedByRSAaByte = new BASE64Decoder().decodeBuffer(secretEncryptedByRSAasStr);
	    
	    // Client 1 decrypt secret key with your private key
	    String secretDecryptedByRSA =  decrypt(secretEncryptedByRSAaByte, keyRSA.getPrivate());
	    
	    // Client 1 convert secret key decrypted to SecretKey object
	    byte[] secretKey = Base64.getDecoder().decode(secretDecryptedByRSA);
        SecretKey originalAESKey = new SecretKeySpec(secretKey, 0, secretKey.length, "AES");
	    
        //Now both clients have de secret key and can send text encrypted with this key
	    
        // Client 1 encrypt text to send with secret key and send this to Client 2
	    String cypherAEStext = encryptAES(textToCypher,secret);
	    
	    // Client 1 decrypt text
        String decypherAEStext =  decryptAES(cypherAEStext,originalAESKey);
        
        //Conclusion
        if(textToCypher.equals(decypherAEStext))
        	System.out.println("completed flux SUCESSFUL");
        
	}

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
		

		public static String decrypt(byte[] text, PrivateKey key) 
		{
			byte[] dectyptedText = null;
			try 
			{
				// get an RSA cipher object and print the provider
			    final Cipher cipher = Cipher.getInstance("RSA");

			    // decrypt the text using the private key
			    cipher.init(Cipher.DECRYPT_MODE, key);
			    dectyptedText = cipher.doFinal(text);
			    
			    } catch (Exception ex) {
			      ex.printStackTrace();
			    }

			    return new String(dectyptedText);
			 }
		
		public static byte[] encrypt(String text, PublicKey key) 
		{
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
			 return cipherText;
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
}