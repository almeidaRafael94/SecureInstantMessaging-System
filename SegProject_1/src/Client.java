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
import java.util.HashMap;
import java.util.Map;

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
	
	
	//Save client information by client id
	private static Map<String ,Map<String, String>> mapID_KEYS;
	
	//Save clientDataName, publicKey, symmetricKey
	private static Map<String, String> IDInfo;
	

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
	LinkedList<String> state;
	Map<String, LinkedList<String>> clientState;
	
	*/
		
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
	//private String src;
	private String dst;
	
	public Client(String userName) throws NoSuchAlgorithmException, IOException
	{	
		encryptClass = new Encryptation();
		keyPair = encryptClass.generateAsymmetricKey(1024);
		secret =  new HashMap<String, SecretKey>();
		
		mapID_KEYS = new HashMap<String, Map<String, String>>();
		
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
					        analyzeACK(inputLine);
					    }
					}	
					catch(IOException | JSONException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e){}
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
	public void send(String type, String payloadType, String listID) throws IOException, JSONException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
	{   	
		this.type = type;
		json = new JsonObject();
		json.addProperty("type", type);
		
		if(type.equals("connect"))
		{
			phase += 1;
	    	ciphers = Base64.encode(keyPair.getPublic().getEncoded());
	    	json.addProperty("type", type);
	        json.addProperty("phase", phase);
	        json.addProperty("name", client_name);
	        json.addProperty("id", id);
	        json.addProperty("ciphers",ciphers);
	        json.addProperty("data", data);
	        
		}
		else if(type.equals("secure"))
		{
			ciphers = " ";
			payload = new JsonObject();
			payload.addProperty("type", payloadType);
			json.addProperty("sa_data", data);
			
			switch(payloadType)
			{
				case "list":
					if(listID == null)
						payload.addProperty("data", data);
					else
						payload.addProperty("data", listID);
					break;
				case "client-connect":
					phase += 1;
					//showResults();
					SecretKey sk = Encryptation.generateAESKey();
					secret.put(dst, sk);
					System.out.print(client_name + " try encrypt secretKey Str: " + Encryptation.convertAESkeyToString(sk) + " or SecretKey: "+ sk + " with publicKey: ");
					String KEYencrypted = "";
					showResults();
					if(mapID_KEYS.containsKey(dst))
					{	
						System.out.println(mapID_KEYS.get(dst).get("publicKey"));
						KEYencrypted = Encryptation.encrypt(Encryptation.convertAESkeyToString(sk), mapID_KEYS.get(dst).get("publicKey"));
						System.out.println(client_name + " Encryptation of symetric key sucessfull");
					}
					ciphers = KEYencrypted;
					data = "Client1 to Client 2: hi :)";
					payload.addProperty("src", this.id);
					payload.addProperty("dst", dst);
					payload.addProperty("phase", phase);
					payload.addProperty("ciphers", ciphers);
					payload.addProperty("data", data);
					
					break;
				case "client-com":
					data = "";
					if(secret.containsKey(dst))
					{	
						System.out.println(client_name + "-------->" + dst);
						if(secret.get(dst) != null)
							data = Encryptation.encryptAES("Hello, Im client 2 and you are client 1!!", secret.get(dst));			  
					}
					payload.addProperty("src", this.id);
					payload.addProperty("dst", dst);
					payload.addProperty("data", data);
					
					break;	
				case "client-disconnect":
					payload.addProperty("src", this.id);
					payload.addProperty("dst", dst);
					payload.addProperty("data", data);
					
					break;
				case "ack":
					payload.addProperty("src", this.id);
					payload.addProperty("dst", dst);
					payload.addProperty("data", data);
					
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
	
	// Set destination id
	public void setDst(String dst)
	{
		this.dst = dst;	
	}
	
	public String getID()
	{
		return this.id;
	}
	
	public void analyzeACK(String msg) throws JSONException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException
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
					SecretKey sk = Encryptation.decrypt(otherClientSymmetricKey, keyPair.getPrivate());
					String secureKeyStr = Encryptation.convertAESkeyToString(sk);
					System.out.println(client_name + " received secretKey Str:" + secureKeyStr + " or SecretKey: " + sk +" from " + otherClientSymmetricKeySrc +" and save it");
					
					IDInfo = new HashMap<String,String>();
					IDInfo = mapID_KEYS.get(otherClientSymmetricKeySrc);
					IDInfo.put("symmetricKey",secureKeyStr);
					mapID_KEYS.put(otherClientSymmetricKeySrc,IDInfo);
					
					secret.put(dst, sk);
					//System.out.println(CipheringSymmetricKey(keyPair,otherClientSymmetricKey));
					showResults();
					break;
				case "client-disconnect":
					break;
				case "client-com":
					String srcIdClient = messageReceivedPayload.getString("src");
					String clearText = messageReceivedPayload.getString("data");

					if(!clearText.equals(""))
					{
						if(secret.containsKey(srcIdClient))
						{
							if(secret.get(srcIdClient) != null)
							{
								clearText = Encryptation.decryptAES(clearText, secret.get(srcIdClient));
							}		
						}
					}
					
					System.out.println(client_name + " Clear text: " + clearText);
					
					break;
				case "ack":
					break;
			}
		}
	}
	
	public void showResults()
	{
		System.out.println("SHOW RESULTS OF : " + client_name + " with id: " + this.id + " -> \n" + mapID_KEYS.toString() + "\nEND SHOW RESULTS\n");
		//System.out.println(clientState. + "\n");
	}
	public void showSecretKeyStore()
	{
		System.out.println(client_name + " Secret key list by id " + secret.toString());
	}
}
