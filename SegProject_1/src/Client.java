import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class Client
{	
	int cnt;
	
	//Map to save <client_id, client_symmetricKey_String>
	Map<String ,Map<String, String>> mapID_KEYS;
	Map<String, String> IDInfo;
	
	/*Map to save the state of this client in the world by id
	CONNECT 				- connect with the server
	ACK_CONNECT			- ack of connection with the server
	CL_CONNECT			- connect with other client
	ACK_CL_CONNECT   	- ack of connection with the other client
	CL_COM				- communication with other client
	ACK_CL_COM			- ack communication with other client
	LIST				- request list to the server
	ACK_LIST			- ack of request list
	CL_DISCONNECT		- disconnect to the server
	ACK_CL_DISCONNECT	- ack disconnect to the server
	CL_ACK				- ack for other client
	ACK_CL_ACK
	*/
	LinkedList<String> state;
	Map<String, LinkedList<String>> clientState;
	
	
	//encryptation/decryptation keys
	private static KeyPair keyPair;
	private static SecretKey symmetricKey;
	static byte[] keyData;
	static SecretKeySpec sks;
	static Cipher cipher;
	static int size;
	private static Cipher ecipher;
	private static Cipher dcipher;
	
	//server connection attributes
	private String ip;
	private int port;
	
	//Client Thread
    private Thread thread;
    
    //IO
    private DataInputStream is;
    private DataOutputStream os;
    private BufferedReader in;
    
    private Socket socket;
	
	JsonElement description; // JSON description of the client, including id
	OutputStream out;	 // Stream to send messages to the client
	
	private String id;		 // id extracted from the JASON description
	String name;
	int level;
	String sa_data;
	
	/* Information of the messages
	 * 
	 */
	JsonObject json;
	
	String type;
	int phase;
	String client_name;
	String message_id;
	String ciphers;
	String data;
	JsonObject payload;
	String src;
	String dst;
	
	public Client(String userName)
	{	
		cnt = 0;
		
		mapID_KEYS = new HashMap<String, Map<String, String>>();
		state = new LinkedList<String>();
		clientState = new HashMap<String, LinkedList<String>>();
		
		//remove this
		client_name = userName;
		ip = "127.0.0.1";
		port = 9090; 
		this.phase = 0;
		this.ciphers = "RSA";
		this.id = generateNONCE();
		this.data = "<JSON or base64 encoded if binary (optional)>";
	}
	
	public void config(String ip, int port)
    {
    	this.ip = ip;
    	this.port = port;
    }
	
	public boolean connected()
    {
    	return socket!=null && os != null && socket.isConnected();// && !os.checkError();
    }
	
    //Connects to the server and creates a thread to process requests
	public void start() throws UnknownHostException, IOException
	{
	try {
		socket = new Socket(ip, port); 	
        is = new DataInputStream(socket.getInputStream());
	    os = new DataOutputStream(socket.getOutputStream());
    
      //Create new Thread to handle message receiving
	  
        //thread = new Thread()
	    new Thread(new Runnable()
	    {  
            public void run()
            {
                while(true)
                {	
                	
                	//Read Message from the server
					try
					{	
						in = new BufferedReader(new InputStreamReader(is));
						String inputLine;
					    while ((inputLine = in.readLine()) != null)
					    {
					        //System.out.println("ACK RECEIVED by: " + client_name + " : " + inputLine + "\n");
					        analyzeACK(inputLine);
					    }
					}	
					catch(IOException | JSONException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e){}
                }
            }
	    }).start();
        
         //Start thread
         //thread.start();
        
	    
	    
		}catch (IOException ex) {
	        System.err.println("<-cliente->: " + ex.getMessage());
	     }
	}
	
	// Send message to server 
	public void send(String type, String payloadType, String listID) throws IOException, JSONException, NoSuchAlgorithmException, InvalidKeySpecException
	{   	
		this.type = type;
		json = new JsonObject();
		json.addProperty("type", type);
		String macro = null;
		String macroID = dst;	
		
		if(type.equals("connect"))
		{
			generateAsymmetricKey(1024);
			phase += 1;
	    	ciphers = Base64.encode(keyPair.getPublic().getEncoded());
	    	json.addProperty("type", type);
	        json.addProperty("phase", phase);
	        json.addProperty("name", client_name);
	        json.addProperty("id", id);
	        json.addProperty("ciphers",ciphers);
	        json.addProperty("data", data);
	        
	        macroID = "0";
	        macro = "CONNECT";
		}
		else if(type.equals("secure"))
		{
			ciphers = " ";
			payload = new JsonObject();
			sa_data = "";
			payload.addProperty("type", payloadType);
			json.addProperty("sa_data", data);
			
			switch(payloadType)
			{
				case "list":
					if(listID == null)
						payload.addProperty("data", data);
					else
						payload.addProperty("data", listID);
					macroID = "0";
					macro = "LIST";
					break;
				case "client-connect":
					phase += 1;
					ciphers = generateSymmetricKey(this.id).toString();
					data = "Client1 to Client 2: hi :)";
					payload.addProperty("src", this.id);
					payload.addProperty("dst", dst);
					payload.addProperty("phase", phase);
					payload.addProperty("ciphers", ciphers);
					payload.addProperty("data", data);
					
					macro = "CL_CONNECT";
					break;
				case "client-disconnect":
					payload.addProperty("src", this.id);
					payload.addProperty("dst", dst);
					payload.addProperty("src", data);
					
					macro = "CL_DISCONNECT";
					break;
				case "client-com":
					payload.addProperty("src", this.id);
					payload.addProperty("dst", dst);
					payload.addProperty("src", data);
					
					macro = "CL_COM";
					break;
				case "ack":
					payload.addProperty("src", this.id);
					payload.addProperty("dst", dst);
					payload.addProperty("data", data);
					
					macro = "CL_ACK";
					break;
			}
			json.add("payload", payload);
		}
		else
		{
			macro = null;
			System.err.println("Invalid command type");
		}
		
		//update state
		updateState(macroID,macro);
		
		System.out.println("JSON send by: " + client_name + " : " + json.toString());
	    os.write(json.toString().getBytes( StandardCharsets.UTF_8 ));
	    os.flush();
	}

	// client disconnect 
	public void disconnect()
    {
    	try
    	{
    		//is.close();
    		//os.close();
    	    //in.close();
    		thread.interrupt();
	    	socket.shutdownInput();
	    	socket.shutdownOutput();
    	}
    	catch(Exception e){}
    }
	
	//Generate identification number
	private String generateNONCE()
	{
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}
	
	//Get client identification number
	public String getNONCE()
	{
		return id;
	}
	
	// Set destination id
	public void setDst(String dst)
	{
		this.dst = dst;	
	}
	
	//Set source id
	public void setSrc(String src)
	{
		this.src = src;
	}
	
	public String getID()
	{
		return this.id;
	}
	
	public void analyzeACK(String msg) throws JSONException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
	{	
		System.out.println("RESPONSE: " + msg);
		JSONObject messageReceivedJSON = new JSONObject(msg);
		String messageReceivedType = messageReceivedJSON.getString("type");
		JSONObject messageReceivedPayload;
		String payloadType;
		JSONArray listData;
		JSONObject clientData;
		String clientDataId;
		String clientDataPublicKey;
		String clientDataName;
		IDInfo = new HashMap<String,String>();
		String otherClientSymmetricKey;
		String otherClientSymmetricKeySrc;
		
		if(messageReceivedType.equals("connect"))
		{
			
		}
		else if(messageReceivedType.equals("secure"))
		{
			messageReceivedPayload = messageReceivedJSON.getJSONObject("payload");
			payloadType = messageReceivedPayload.getString("type");
			switch(payloadType)
			{	
				case "list":
					// GET public keys of different clients
					listData = messageReceivedPayload.getJSONArray("data");
					for(int i = 0; i < listData.length(); i++)
					{
						IDInfo = new HashMap<String,String>();
						clientData = listData.getJSONObject(i);
						clientDataId = clientData.getString("id");
						clientDataName = clientData.getString("name");
						clientDataPublicKey = clientData.getString("ciphers");

						if(!this.id.equals(clientDataId))
						{
							IDInfo = new HashMap<String,String>();
							IDInfo.put("name", clientDataName);
							IDInfo.put("publicKey", clientDataPublicKey);
							IDInfo.put("symmetricKey", null);
							//System.out.println(clientDataName + " " + clientDataPublicKey + " -> " +  clientDataId);
							mapID_KEYS.put(clientDataId, IDInfo);
						}
					}
					break;
					
				case "client-connect":
					otherClientSymmetricKey = messageReceivedPayload.getString("ciphers");
					otherClientSymmetricKeySrc = messageReceivedPayload.getString("src");
					//System.out.println("SRC ID: "  +otherClientSymmetricKeySrc + " SK-> " + otherClientSymmetricKey);

					IDInfo = new HashMap<String,String>();
					IDInfo = mapID_KEYS.get(otherClientSymmetricKeySrc);
					IDInfo.put("symmetricKey",otherClientSymmetricKey);
					mapID_KEYS.put(otherClientSymmetricKeySrc,IDInfo);
					
					//System.out.println(CipheringSymmetricKey(keyPair,otherClientSymmetricKey));
					//showResults();
					break;
				case "client-disconnect":
					break;
				case "client-com":
					break;
				case "ack":
					break;
			}
		}
		
		
	}
	

	public void updateState(String macroID, String macro)
	{
		if(macro != null && macroID != null)
		{
			if(clientState.containsKey(macroID))
			{
				state = clientState.get(macroID);
				state.add(macro);
				clientState.put(macroID ,state);
			}
			else
			{
				state.add(macro);
				clientState.put(macroID, state);
			}
		}
	}
	
	//Generate asymetric keypair
	public static void generateAsymmetricKey(int keySize) throws NoSuchAlgorithmException, IOException
	{
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(keySize);
		KeyPair KPair = kpg.generateKeyPair();
		keyPair = KPair;
	}
	
	//Generate symetric key by client id
	public static SecretKey generateSymmetricKey(String id) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		keyData = id.getBytes();
		sks = new SecretKeySpec(keyData, "DES");
		SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = kf.generateSecret(sks);
		return secretKey;
	}
	
	// Cyphering simetric key with asymetric public key
	public static String CipheringSymmetricKey(KeyPair keyPair, String asymetricKeyToEncrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException
	{	    
        byte[] public_key = keyPair.getPublic().getEncoded();
        byte[] text = asymetricKeyToEncrypt.getBytes();
        
        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(public_key);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, kf.generatePublic(spec));
        byte[] cipherText;
		cipherText = cipher.doFinal(text);
		
		return new String(cipherText, "UTF8");
	}
	
	// Decyphering simetric key with asymetric private key
	public static String DecipheringSymmetricKey(KeyPair keyPair, String asymetricKeyToDecrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
	{
        byte[] private_key = keyPair.getPrivate().getEncoded();
        byte[] cypheringText = asymetricKeyToDecrypt.getBytes();
        
        PKCS8EncodedKeySpec spec =
                new PKCS8EncodedKeySpec(private_key);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        Cipher dcipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, kf.generatePrivate(spec));
        byte[] decipheredText;
        decipheredText = cipher.doFinal(cypheringText);
      
        return new String(decipheredText, "UTF8");
	}
	
	// Cyphering text with symetric key
	public static String Ciphering(SecretKey secretKey, String textToCypher) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException
	{
		ecipher = Cipher.getInstance("DES");
		ecipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] utf8 = textToCypher.getBytes("UTF8");
		byte[] enc = ecipher.doFinal(utf8);
		//return new sun.misc.BASE64Encoder().encode(enc);
		
		return new String(enc, "UTF8");
	}
	
	// Decyphering text with symetric key
	public static String Decyphering(SecretKey secretKey, String cypheredText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException
	{
		dcipher = Cipher.getInstance("DES");
		dcipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(cypheredText);
		byte[] utf8 = dcipher.doFinal(dec);
		
		return new String(utf8, "UTF8");
	}
	
	public void showResults()
	{
		System.out.println("SHOW RESULTS -> \n" + mapID_KEYS.toString() + "\nEND SHOW RESULTS\n");
		//System.out.println(clientState. + "\n");
	}
}
