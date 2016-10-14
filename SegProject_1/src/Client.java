import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
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
    
    private BufferedReader inIO;
    private PrintWriter outIO;
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
    	return socket!=null && outIO != null && socket.isConnected() && !outIO.checkError();
    }
	
    //Connects to the server and creates a thread to process requests
	public void start() throws UnknownHostException, IOException
	{
		socket = new Socket(ip, port); 	
		inIO = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outIO = new PrintWriter(socket.getOutputStream(), true);
        
        is = new DataInputStream(socket.getInputStream());
	    os = new DataOutputStream(socket.getOutputStream());
        
      //Create new Thread to handle message receiving
        thread = new Thread()
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
					        System.out.println(inputLine);
					
					}
					catch(IOException e){}
                }
            }
         };
        
         //Start thread
         thread.start();
	}
	
	// Send message to server 
	public void send(String type, String payloadType) throws IOException, JSONException
	{   	
		this.type = type;
		json = new JsonObject();
		json.addProperty("type", type);
		
		/*
		switch(type)
		{
			case "connect":
				phase += 1;
		    	//client_name = "Rafael";
		    	data = "<JSON or base64 encoded if binary (optional)>";
		    	
		    	json.addProperty("type", type);
		        json.addProperty("phase", phase);
		        json.addProperty("name", client_name);
		        json.addProperty("id", id);
		        json.addProperty("ciphers",ciphers);
		        json.addProperty("data", data);
		        		    	
				break;
			case "secure":
				payload = new JsonObject();
				sa_data = "";
				payload = null;
				json.addProperty("sa_data", data);
				json.addProperty("payload", data);
				break;
			case "list":
				data =  "<JSON or base64 encoded if binary (optional)>";
				json.addProperty("data", data);
				break;
			case "client-connect":
				phase += 1;
				data = "Client1 to Client 2: hi :)";
				json.addProperty("src", src);
				json.addProperty("dst", dst);
				json.addProperty("phase", phase);
				json.addProperty("ciphers", ciphers);
				json.addProperty("data", data);
				break;
			case "client-disconnect":
				src = "";
				dst = "";
				data = "";
				json.addProperty("src", src);
				json.addProperty("dst", dst);
				json.addProperty("src", data);
				break;
			case "client-com":
				src = "";
				dst = "";
				data = "";
				json.addProperty("src", src);
				json.addProperty("dst", dst);
				json.addProperty("src", data);
				break;
		}
		*/
		
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
		
		System.out.println("JSON: " + json.toString());
	    os.write(json.toString().getBytes());
	    
	    /*
	    BufferedReader in = new BufferedReader(new InputStreamReader(is));
	    String inputLine;
	    while ((inputLine = in.readLine()) != null)
	        System.out.println(inputLine)
	        */
		
	}
	public void receive() throws UnsupportedEncodingException, IOException
	{
		
		try (InputStreamReader isr = new InputStreamReader(
        		socket.getInputStream(),"UTF_8")) {
			
        System.out.println(isr.read());
        }
        
	}
	
	// client disconnect 
	public void disconnect()
    {
    	try
    	{
    		thread.interrupt();
	    	socket.shutdownInput();
	    	socket.shutdownOutput();
    	}
    	catch(Exception e){}
    }
	private String generateNONCE()
	{
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}
	public String getNONCE()
	{
		return id;
	}
	
	//test function, remove
	public void setSrcDst(String src, String dst)
	{
		this.src = src;
		this.dst = dst;
	}
}
