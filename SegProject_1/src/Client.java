import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class Client
{	
	//Cryptation functions
	Encryptation encryptClass;
	
	// Key pair of this client
	private static KeyPair keyPair;
	
	// Map with secret key by client_id
	private static Map<String, SecretKey> secret;
	
	// Map with secretX and secretValue by client_id
	private static Map<String, JSONObject> secretDiffieHellman;
	
	//Save last message received by each client
	private static Map<String , String> messages;
	
	//Register messages and respective ack
	private static Map<String , LinkedList<String>> messagesHistory;
	
	//Save client information by client id
	private static Map<String ,Map<String, String>> mapID_KEYS;
	
	//Save clientDataName, publicKey, symmetricKey
	private static Map<String, String> IDInfo;
		
	//server connection attributes
	private String ip;
	private int port;
	
	//Client Thread
    private Thread thread;
    
    //Input/Output
    private DataInputStream is;
    private DataOutputStream os;
    private BufferedReader in;
    
    //Socket to transmit messages
    private Socket socket;
    
    //control variables
    private boolean clientCreated = false;
    private boolean clientStarted = false;
    
	
    //Client attributes
	private String id;
	
	//Information of the messages
	private JsonObject json;
	private String type;
	private int phase;
	private String client_name;
	private String ciphers;
	private String data;
	private JsonObject payload;
	private String dst;
	private String level;
	
	//Diffie-Hellman
	private int bitLength = 512;
	private BigInteger p;
	private BigInteger g;
	private BigInteger A;
	private BigInteger B;
	private BigInteger secretA;
	private BigInteger secretX;
	
	public Client(String userName, String level) throws NoSuchAlgorithmException, IOException
	{	
		if(level != null && userName != null)
		{
			//encryptClass = new Encryptation();
			keyPair = Encryptation.generateAsymmetricKey(1024);
			secret =  new HashMap<String, SecretKey>();
			messages = new HashMap<String,String>();
			secretDiffieHellman = new HashMap<String, JSONObject>();
			mapID_KEYS = new HashMap<String, Map<String, String>>();
			messagesHistory = new HashMap<String , LinkedList<String>>();
			
			this.level = level;
			this.client_name = userName;
			
			this.phase = 0;
			this.ciphers = "RSA AES";
			this.id = generateNONCE();
			this.data = "<JSON or base64 encoded if binary (optional)>";
			
			clientCreated = true;
		}
	}
	
    //Connects to the server and creates a thread to process requests
	public void start() throws UnknownHostException, IOException
	{
		try 
		{
			// socket default configuration
			this.ip = "127.0.0.1";
			this.port = 9090; 
			
			socket = new Socket(ip, port); 	
	        is = new DataInputStream(socket.getInputStream());
		    os = new DataOutputStream(socket.getOutputStream());
    
		    //Create new Thread to handle message receiving
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
						        analyzeACK(inputLine);
						    }
						}	
						catch(IOException | JSONException | NoSuchAlgorithmException | 
							  InvalidKeySpecException | InvalidKeyException | NoSuchPaddingException | 
							  IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e){}
		    		}
		    	}
		    }).start();
		    clientStarted = true;
		}catch (IOException ex) {
			clientStarted = false;
	        System.err.println("<-cliente->: " + ex.getMessage());
	    }
	}
	
	// Send message to server 
	public void send(String type, String payloadType, String clientID, String text) 
			throws IOException, JSONException, NoSuchAlgorithmException, InvalidKeySpecException, 
			InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
	{   	
		this.type = type;
		json = new JsonObject();
		json.addProperty("type", type);
		JsonObject jsonData = new JsonObject();
		LinkedList<String> tmpList;
		
		if(type.equals("connect"))
		{	
			phase += 1;
			
			jsonData.addProperty("level",this.level);
			jsonData.addProperty("public",Base64.encode(keyPair.getPublic().getEncoded()));
			
	        json.addProperty("phase", phase);
	        json.addProperty("name", client_name);
	        json.addProperty("id", id);
	        json.addProperty("ciphers","RSA");
	        json.add("data", jsonData);	        
		}
		else if(type.equals("secure"))
		{
			ciphers = " ";
			payload = new JsonObject();
			payload.addProperty("type", payloadType);
			json.addProperty("sa_data", "<JSON or base64 encoded if binary (optional)>");
			SecretKey sk;
			
			switch(payloadType)
			{	
				case "list":
					payload.addProperty("data", clientID + "," + this.level);
					break;
				case "client-connect":
					
					phase += 1;	
					String KEYencrypted = "";	
					
					if(mapID_KEYS.containsKey(dst))
					{	
						sk = Encryptation.generateAESKey();
						secret.put(dst, sk);
						
						System.out.println(mapID_KEYS.get(dst).get("publicKey"));
						KEYencrypted = Encryptation.encrypt(Encryptation.convertAESkeyToString(sk), mapID_KEYS.get(dst).get("publicKey"));
						System.out.println(client_name + " Encryptation of symetric key sucessfull");
						
						IDInfo = new HashMap<String,String>();
						secretA = new BigInteger(bitLength-2 , new SecureRandom());
						IDInfo = mapID_KEYS.get(dst);
					    
					    BigInteger pTmp = BigInteger.probablePrime(bitLength, new SecureRandom());
					    BigInteger gTmp = BigInteger.probablePrime(bitLength, new SecureRandom());
					    BigInteger ATmp = gTmp.modPow(secretA, pTmp);
						
						JSONObject j = new JSONObject();		
						j.put("p" , pTmp);
						j.put("g" , gTmp);
						j.put("A" , ATmp);
						j.put("secret", secretA);
						secretDiffieHellman.put(dst, j);
						
						jsonData.addProperty("AESKeyEncrypted", KEYencrypted);
						jsonData.addProperty("A", ATmp);
						jsonData.addProperty("p", pTmp);
						jsonData.addProperty("g", gTmp);
					}
					
					data = KEYencrypted;
					payload.addProperty("src", this.id);
					payload.addProperty("dst", dst);
					payload.addProperty("phase", phase);
					payload.addProperty("ciphers", ciphers);
					payload.add("data", jsonData);				
					
					tmpList = new LinkedList<String>();
					tmpList.add("client-connect");
					messagesHistory.put(dst, tmpList);
					break;
				case "client-com":
					if(text != null && 
					!text.equals("") && 
					!text.trim().isEmpty())
					{
						jsonData = new JsonObject();
						
						if(mapID_KEYS.containsKey(dst) && secret.containsKey(dst) && secretDiffieHellman.containsKey(dst))
						{	
							if(secret.get(dst) != null && secretDiffieHellman.get(dst).get("secretX") != null)
							{	
								sk = Encryptation.generateAESKey();
								secret.put(dst, sk);
								KEYencrypted = Encryptation.encrypt(Encryptation.convertAESkeyToString(sk), mapID_KEYS.get(dst).get("publicKey"));
								jsonData.addProperty("AESKeyEncrypted", KEYencrypted);
								jsonData.addProperty("encryptedText", Encryptation.encryptAES(secretDiffieHellman.get(dst).get("secretX") + text, sk));
							}
						}
						payload.addProperty("src", this.id);
						payload.addProperty("dst", dst);
						payload.add("data", jsonData);
						
						if(messagesHistory.containsKey(dst))
						{
							tmpList = messagesHistory.get(dst);
							tmpList.add("client-com");
							messagesHistory.put(dst, tmpList);
						}
					}
					break;	
				case "client-disconnect":
					payload.addProperty("src", this.id);
					payload.addProperty("dst", dst);
					payload.addProperty("data", data);
					
					if(messagesHistory.containsKey(dst))
					{
						tmpList = messagesHistory.get(dst);
						tmpList.add("client-disconnect");
						messagesHistory.put(dst, tmpList);
					}
					break;
				case "ack":
					jsonData = new JsonObject();
					jsonData.addProperty("B", text);
					payload.addProperty("src", this.id);
					payload.addProperty("dst", dst);
					payload.add("data", jsonData);
					break;
			}
			json.add("payload", payload);
		}
		else
		{
			System.err.println("Invalid command type");
		}
		
		System.out.println("JSON send by: " + client_name + " : " + json.toString());
	    os.write(json.toString().getBytes( StandardCharsets.UTF_8 ));
	    os.flush();
	}
	
	public void analyzeACK(String msg)
			throws JSONException, NoSuchAlgorithmException, IOException, 
				   InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, 
				   IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException
	{	
		System.out.println(client_name + " RESPONSE: " + msg);
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
		String level;
		
		JSONObject j = new JSONObject();
		
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
					// GET public keys of different clients and other informations like p and g values
					listData = messageReceivedPayload.getJSONArray("data");
					for(int i = 0; i < listData.length(); i++)
					{	
						IDInfo = new HashMap<String,String>();
						clientData = listData.getJSONObject(i);
						clientDataId = clientData.getString("id");
						clientDataName = clientData.getString("name");
						j = clientData.getJSONObject("data");
						level = j.getString("level");
						clientDataPublicKey = j.getString("public");

						if(!this.id.equals(clientDataId) && Integer.parseInt(level) <= Integer.parseInt(this.level))
						{
							IDInfo = new HashMap<String,String>();
							IDInfo.put("name", clientDataName);
							IDInfo.put("publicKey", clientDataPublicKey);
							IDInfo.put("symmetricKey", null);
							IDInfo.put("level", level);
							mapID_KEYS.put(clientDataId, IDInfo);
						}
					}
					break;
					
				case "client-connect":
					
					j = messageReceivedPayload.getJSONObject("data");
					otherClientSymmetricKey = j.getString("AESKeyEncrypted");
					B = new BigInteger(j.getString("A").toString());
					p = new BigInteger(j.getString("p").toString());
					g = new BigInteger(j.getString("g").toString());
					otherClientSymmetricKeySrc = messageReceivedPayload.getString("src");
					SecretKey sk = Encryptation.decrypt(otherClientSymmetricKey, keyPair.getPrivate());
					String secureKeyStr = Encryptation.convertAESkeyToString(sk);
					System.out.println(client_name + " received secretKey Str:" + secureKeyStr + " or SecretKey: " + sk +" from " + otherClientSymmetricKeySrc + " B: " + B + " and save it");
					
					IDInfo = new HashMap<String,String>();
					if(mapID_KEYS.containsKey(otherClientSymmetricKeySrc))
						IDInfo = mapID_KEYS.get(otherClientSymmetricKeySrc);
					IDInfo.put("symmetricKey",secureKeyStr);
					IDInfo.put("B",B.toString());
					
					secretA = new BigInteger(bitLength-2 , new SecureRandom());
					A = g.modPow(secretA, p);
					IDInfo.put("A",A.toString());
					secretX = B.modPow(secretA, p);
					mapID_KEYS.put(otherClientSymmetricKeySrc,IDInfo);
					
					j = new JSONObject();
					j.put("p" , p);
					j.put("g" , g);
					j.put("A", A);
					j.put("B", B);
					j.put("secret", secretA);
					j.put("secretX", secretX);
					System.out.println("SECRET FORMED!!!!!!!!!!! \n" 
									  + "p: " + p + "\n" 
									  + "g: " + g + "\n"
									  + "secret: " + secretA + "\n"
									  + "secretX: " + secretX + "\n");
					secretDiffieHellman.put(otherClientSymmetricKeySrc, j);
					secret.put(otherClientSymmetricKeySrc, sk);
					
					showResults();
					
					setDst(otherClientSymmetricKeySrc);
					send("secure", "ack", null, A.toString());
					
					break;
				case "client-disconnect":
					send("secure", "ack", null, "client-disconnect " + messageReceivedPayload.getString("src"));
					break;
				case "client-com":
					String srcIdClient = messageReceivedPayload.getString("src");
					j = messageReceivedPayload.getJSONObject("data");
					String clearText = j.getString("encryptedText");
					
					String encryptedKey = j.getString("AESKeyEncrypted");
					sk = Encryptation.decrypt(encryptedKey, keyPair.getPrivate());
					secureKeyStr = Encryptation.convertAESkeyToString(sk);
					secret.put(srcIdClient, sk);
					IDInfo = new HashMap<String,String>();
					if(mapID_KEYS.containsKey(srcIdClient))
						IDInfo = mapID_KEYS.get(srcIdClient);
					IDInfo.put("symmetricKey",secureKeyStr);
					mapID_KEYS.put(srcIdClient,IDInfo);

					if(!clearText.equals(""))
					{
						if(secret.containsKey(srcIdClient))
						{
							if(secret.get(srcIdClient) != null)
							{	
								clearText = Encryptation.decryptAES(clearText, secret.get(srcIdClient));
								String otherClientSecret = clearText.substring(0, secretDiffieHellman.get(srcIdClient).getString("secretX").length());
								String clearText2 = clearText.substring(secretDiffieHellman.get(srcIdClient).getString("secretX").length());
								System.out.println(otherClientSecret.equals( secretDiffieHellman.get(srcIdClient).getString("secretX")));
								if(clearText2 != null && otherClientSecret.equals( secretDiffieHellman.get(srcIdClient).getString("secretX")))
									messages.put(this.client_name, clearText2);
								System.out.println("Total: " + clearText );
								System.out.println("Text:  " + clearText2);
								System.out.println("OtherClientSecret: " + otherClientSecret);
								System.out.println("MySecret: " + secretDiffieHellman.get(srcIdClient).getString("secretX"));
							}		
						}
						send("secure", "ack", null, "client-com " + messageReceivedPayload.getString("src"));
					}
					System.out.println(client_name + " Clear text: " + clearText + " Message map: " + messages.toString());
					
					break;
				case "ack":
					srcIdClient = messageReceivedPayload.getString("src");
					System.out.println("ACK RECEIVED!!!!");
					j = messageReceivedPayload.getJSONObject("data");
					System.out.println("History: " + messagesHistory + " text: " + j.getString("B").split(" ")[0]);
					
					if(messagesHistory.containsKey(dst))
					{
						LinkedList<String> tmpList = messagesHistory.get(dst);
					
						if(j.getString("B").split(" ")[0].equals("client-disconnect") ||
								j.getString("B").split(" ")[0].equals("client-com") )
						{
								if(tmpList.contains(j.getString("B").split(" ")[0]))
									tmpList.remove(j.getString("B").split(" ")[0]);
						}
						else
						{
							if(tmpList.contains("client-connect"))
								tmpList.remove("client-connect");
							
							B = new BigInteger(j.getString("B"));
							if(secretDiffieHellman.containsKey(srcIdClient))
							{
								JSONObject a = new JSONObject();
								a = secretDiffieHellman.get(srcIdClient);
								secretX = B.modPow(new BigInteger(a.getString("secret")), new BigInteger(a.get("p").toString()));
								a.put("secretX", secretX);
								a.put("B", B);
								secretDiffieHellman.put(srcIdClient, a);
								System.out.println("SECRET FORMED!!!!!!!!!!! \n" 
										  + "p: " + a.get("p") + "\n" 
										  + "g: " + a.get("g") + "\n"
										  + "secret: " + secretA + "\n"
										  + "secretX: " + secretX + "\n");
							}
						}
					}
					System.out.println("History: " + messagesHistory);
					break;
			}
		}
	}
	
	//Returns TRUE if the client creation was successful
	public boolean clientCreated()
	{
		return clientCreated;
	}
	
	//Returns TRUE if the client start was successful
	public boolean clientStarted()
	{
		return clientStarted;
	}
	
	//Return ack messages received by this client sends by other client
	public LinkedList<String> getAckHistory(String dst)
	{
		return messagesHistory.get(dst);
	}
	
	// client disconnect 
	public void disconnect()
    {
		boolean historyEmpty = true;
		for(String l: messagesHistory.keySet())
		{
			if(!messagesHistory.get(l).isEmpty())
				historyEmpty = false;	
		}
 
		if(historyEmpty)
		{
	    	try
	    	{
	    		is.close();
	    		os.close();
	    	    in.close();
	    		thread.interrupt();
		    	socket.shutdownInput();
		    	socket.shutdownOutput();
		    	socket.close();
	    	}
	    	catch(Exception e){}
		}
    }
	
	//Generate identification number
	private String generateNONCE()
	{
		return UUID.randomUUID().toString();
	}
	
	// Set destination id
	public void setDst(String dst)
	{
		this.dst = dst;	
	}
	public boolean clientContainSecret(String id)
	{
		return secretDiffieHellman.get(id).has("secretX");
	}
	
	public String getID()
	{
		return this.id;
	}
	
	public void showResults()
	{
		System.out.println("SHOW RESULTS OF : " + client_name + " with id: " + this.id + " -> \n" + mapID_KEYS.toString() + "\nEND SHOW RESULTS\n");
	}
	public void showSecretKeyStore()
	{
		System.out.println(client_name + " Secret key list by id " + secret.toString());
	}
	public List<String> getClientsList()
	{
		List<String> cleints = new ArrayList<String>();
		Set<String> idKey = mapID_KEYS.keySet();
		if(!idKey.isEmpty())
		{
			for(String id : idKey)
			{
				if(mapID_KEYS.get(id).get("level") != null && mapID_KEYS.get(id).get("level").matches("\\d+"))
					if(!id.equals(this.id) && Integer.parseInt(mapID_KEYS.get(id).get("level")) <= Integer.parseInt(this.level) )
						cleints.add("ID: " + id + ", Name: "  + mapID_KEYS.get(id).get("name"));
			}
		}
		return cleints;
	}
	public String getLastMessage(String username)
	{
		if(messages.containsKey(username))
			return messages.get(username);
		return null;
	}
	
	public void viewDiffieHellmanStructure()
	{
		System.out.println("------------------------- DIFFIE-HELLMAN STRUCT-------------------\n" + secretDiffieHellman + "\n END DIFFIE-HELLMAN STRUCT");
	}
}
