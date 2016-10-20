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

public class runClient2 {

	static Socket socket;
	static String host;
	static int port;
	static InetAddress address;
	static Client client2;
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
	        
	        client2 = new Client("Bruno Reis");
	        
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
				client2.start();
		        System.out.println("Client1 is Bruno Reis and connected with id:  " +  client2.getID());
				break;
			case 2:
				client2.send("connect", "", null);
				break;
			case 3:
				client2.send("secure", "list", null);
				break;
			case 4:
				System.out.println("Clinent to connect ID: ");
				String dst = sc2.nextLine();
		        client2.setDst(dst);
				break;
			case 5:
				client2.showResults();
				break;
			case 6:
				client2.send("secure", "client-connect", null);
				break;
			case 7:
				client2.send("secure", "client-com", null);
				break;
			case 8:
				client2.showSecretKeyStore();
				break;
			case 9:
				client2.disconnect();
				break;
			default:
				System.out.println("Opção Inválida!");
				break;
			}
		} while (opcao != 0);
	}
	
	
}
