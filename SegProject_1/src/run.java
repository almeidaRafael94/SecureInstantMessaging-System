import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.json.JSONException;

public class run {

	static Socket socket;
	static String host;
	static int port;
	static InetAddress address;
	
	static Client client1;
	static Client client2;
	static Client client3;
	//Design application
	static ClientDesign clientWindow;
	
	public static void main(String[] args) throws IOException, JSONException 
	{	
		
		/* design client
				java.awt.EventQueue.invokeLater(new Runnable() {
		            public void run() {
		            	clientWindow = new ClientDesign();
		        		clientWindow.frmSecuruty2016.setVisible(true);
		            }
		        });
		*/
		try 
		{
			
			
			host = "127.0.0.1";
	        port = 9090;
	        address = InetAddress.getByName(host);
	        socket = new Socket(address, port);
	        
	        client1 = new Client("Rafael Almeida");
	        client2 = new Client("Pedro ferreitra");
	        client3 =  new Client("Laura");
	        
	        client1.start();
	        //System.out.println("Client connected: " + client1.connected());
	        client2.start();
	        //System.out.println("Client connected: " + client2.connected());
	        client3.start();
	        
	        client1.send("connect", "", null);
	        //System.out.println("connect command by client1: done");
	        client2.send("connect", "", null);
	        //System.out.println("connect command by client2: done");
	        client3.send("connect", "", null);
	        
	       
	        
	        client1.send("secure", "list", null);
	        //System.out.println("list command by client1: done");
	        //client2.send("secure", "list", null);
	        //System.out.println("list command by client2: done");
	        
	        //test connection between 2 clients
	        //client1.setSrc(client1.getNONCE());
	        client1.setDst(client2.getID());
	        //client2.setSrc(client2.getNONCE());
	        client2.setDst(client1.getID());
	        
	        client3.setDst(client1.getID());
	        
	        client3.send("secure", "client-connect", null);
	        client2.send("secure", "client-connect", null);
	        
	        //test function
	        //client1.showResults();
	        // client2.showResults();
	        // client3.showResults();
	        
	        
	        //client1.send("secure", "client-com");
	        //client2.send("secure", "client-com");
	        
	        //client1.send("secure", "ack");
	        //client2.send("secure", "ack");
	        
	        //client1.send("secure", "client-disconnect");
	        //client2.send("secure", "client-disconnect"); 
	       	        
	        
	        //client1.send("connect");
	        //client1.send("secure");
	        client1.disconnect();
			
	        
	        //client1.receive();
			
		} catch (Exception e) {
			e.printStackTrace();
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
