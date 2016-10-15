import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import org.json.JSONException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Client
{
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
	
	String id;		 // id extracted from the JASON description
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
		//remove this
		client_name = userName;
		
		ip = "127.0.0.1";
		port = 9090; 
		this.phase = 0;
		this.ciphers = "RSA";
		this.id = generateNONCE();
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
					        System.out.println("ACK RECEIVED by: " + client_name + " : " + inputLine + "\n");
						
					}
					catch(IOException e){}
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
	public void send(String type, String payloadType) throws IOException, JSONException
	{   	
		this.type = type;
		json = new JsonObject();
		json.addProperty("type", type);
		
		if(type.equals("connect"))
		{
			phase += 1;
	    	//client_name = "Rafael";
	    	data = "<JSON or base64 encoded if binary (optional)>";
	    	
	    	json.addProperty("type", type);
	        json.addProperty("phase", phase);
	        json.addProperty("name", client_name);
	        json.addProperty("id", id);
	        json.addProperty("ciphers",ciphers);
	        json.addProperty("data", data);
		}
		else if(type.equals("secure"))
		{
			payload = new JsonObject();
			sa_data = "";
			payload.addProperty("type", payloadType);
			json.addProperty("sa_data", data);
			
			switch(payloadType)
			{
				case "list":
					data =  "<JSON or base64 encoded if binary (optional)>";
					payload.addProperty("data", data);
					break;
				case "client-connect":
					phase += 1;
					data = "Client1 to Client 2: hi :)";
					payload.addProperty("src", src);
					payload.addProperty("dst", dst);
					payload.addProperty("phase", phase);
					payload.addProperty("ciphers", ciphers);
					payload.addProperty("data", data);
					break;
				case "client-disconnect":
					data =  "<JSON or base64 encoded if binary (optional)>";
					payload.addProperty("src", src);
					payload.addProperty("dst", dst);
					payload.addProperty("src", data);
					break;
				case "client-com":
					data =  "<JSON or base64 encoded if binary (optional)>";
					payload.addProperty("src", src);
					payload.addProperty("dst", dst);
					payload.addProperty("src", data);
					break;
				case "ack":
					data =  "<JSON or base64 encoded if binary (optional)>";
					payload.addProperty("src", src);
					payload.addProperty("dst", dst);
					payload.addProperty("src", data);
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
}
