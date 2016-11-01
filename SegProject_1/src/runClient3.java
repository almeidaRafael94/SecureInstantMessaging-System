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

public class runClient3 {
	static Socket socket;
	static String host;
	static int port;
	static InetAddress address;
	static Client Client3;
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
	        
	        Client3 = new Client("Francisco Pinto", "1");
	        
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
		System.out.println("                  |     9 -  diffie hellman |");
		System.out.println("                  |     10 - Disconnect    	|");
		System.out.println("                  |     0 - Sair          	|");
		System.out.println("                  =========================\n");
		do {
			System.out.println("Opção -> ");
			opcao = sc.nextInt();
			System.out.print("\n");
			switch (opcao) {
			case 1:
				Client3.start();
		        System.out.println("Client3 is Francisco Pinto and connected with id:  " +  Client3.getID());
				break;
			case 2:
				Client3.send("connect", "", null, null);
				break;
			case 3:
				Client3.send("secure", "list", null, null);
				break;
			case 4:
				System.out.println("Clinent to connect ID: ");
				String dst = sc2.nextLine();
		        Client3.setDst(dst);
				break;
			case 5:
				//System.out.println(Client3.getClientsList());
				Client3.showResults();
				break;
			case 6:
				Client3.send("secure", "client-connect", null, null);
				break;
			case 7:
				System.out.println("Insert message to send");
				String msg = sc2.nextLine();
				Client3.send("secure", "client-com", null, msg);;
				break;
			case 8:
				Client3.showSecretKeyStore();
				break;
			case 9:
				Client3.viewDiffieHellmanStructure();
				 break;
			case 10:
				Client3.disconnect();
				 break;
			default:
				System.out.println("Opção Inválida!");
				break;
			}
		} while (opcao != 0);
	}
	
}
