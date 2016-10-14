import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.json.JSONException;

public class run {

	static Socket socket;
	static String host;
	static int port;
	static InetAddress address;
	
	public static void main(String[] args) throws IOException, JSONException 
	{	
		Client client1;
		Client client2;
		try
        {	
			
			host = "127.0.0.1";
	        port = 9090;
	        address = InetAddress.getByName(host);
	        socket = new Socket(address, port);
	        
	        client1 = new Client("Rafael Almeida");
	        client2 = new Client("Pedro ferreitra");
	        client1.start();
	        //System.out.println("Client connected: " + client1.connected());
	        client2.start();
	        //System.out.println("Client connected: " + client2.connected());
	        client1.send("connect", "");
	        //System.out.println("connect command by client1: done");
	        client2.send("connect", "");
	        //System.out.println("connect command by client2: done");
	       
	        
	        //client1.send("secure", "list");
	        //System.out.println("list command by client1: done");
	        //client2.send("secure", "list");
	        //System.out.println("list command by client2: done");
	        
	        //test connection between 2 clients
	        client1.setSrcDst(client1.getNONCE(), client2.getNONCE());
	        client1.send("secure", "client-connect");
	        
	        
	        //client1.send("connect");
	        //client1.send("secure");
	        //client1.disconnect();
			
	        
	        //client1.receive();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
		finally
        {
            //Closing the socket
            try
            {
            	socket.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
	}

}
