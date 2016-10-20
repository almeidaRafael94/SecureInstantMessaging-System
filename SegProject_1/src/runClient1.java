import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONException;

public class runClient1 {

	static Socket socket;
	static String host;
	static int port;
	static InetAddress address;
	static Client client1;
	static Scanner sc = new Scanner(System.in);
	static Scanner sc2 = new Scanner(System.in);
	
	public static void main(String[] args) 
	{
		try 
		{
			host = "127.0.0.1";
	        port = 9090;
	        address = InetAddress.getByName(host);
	        socket = new Socket(address, port);
	        
	        client1 = new Client("Rafael Almeida");
	        
	        menu();
			
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
	private static void menu() throws UnknownHostException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, JSONException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException { // menu principal
		int opcao = 0;
		System.out.println("\n                  =========================");
		System.out.println("                  |     1 - Start         	|");
		System.out.println("                  |     2 - Connect       	|");
		System.out.println("                  |     3 - List          	|");
		System.out.println("                  |     4 - Set dst id    	|");
		System.out.println("                  |     5 - show register 	|");
		System.out.println("                  |     6 - client-connect 	|");
		System.out.println("                  |     7 - client-comm 	|");
		System.out.println("                  |     8 - show secret key |");
		System.out.println("                  |     9 - Disconnect    	|");
		System.out.println("                  |     0 - Sair          	|");
		System.out.println("                  =========================\n");
		do {
			System.out.println("Opção -> ");
			opcao = sc.nextInt();
			System.out.print("\n");
			switch (opcao) {
			case 1:
				client1.start();
		        System.out.println("Client1 is Rafael Almeida and connected with id:  " +  client1.getID());;
				break;
			case 2:
				client1.send("connect", "", null, null);
				break;
			case 3:
				client1.send("secure", "list", null, null);
				break;
			case 4:
				System.out.println("Clinent to connect ID: ");
				String dst = sc2.nextLine();
		        client1.setDst(dst);
				break;
			case 5:
				client1.showResults();
				break;
			case 6:
				client1.send("secure", "client-connect", null, null);
				break;
			case 7:
				System.out.println("Insert message to send");
				String msg = sc2.nextLine();
				client1.send("secure", "client-com", null, msg);
				break;
			case 8:
				client1.showSecretKeyStore();
				break;
			case 9:
				 client1.disconnect();
				 break;
			default:
				System.out.println("Opção Inválida!");
				break;
			}
		} while (opcao != 0);
	}
	
}
