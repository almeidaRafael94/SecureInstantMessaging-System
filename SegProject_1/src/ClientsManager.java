import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientsManager {

	private JFrame frmSecurityP;
	private JTextField textFieldUserName;
	private JTextField textFieldHost;
	private JTextField textFieldPort;
	private ClientDesign client1;
	private LinkedList<String> usernameInfo;
	private Map<String,LinkedList<String>> usersnameList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientsManager window = new ClientsManager();
					window.frmSecurityP.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientsManager() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		usersnameList = new HashMap<String,LinkedList<String>>();
		
		frmSecurityP = new JFrame();
		frmSecurityP.setTitle("Security 2016 P4 68486");
		frmSecurityP.setBounds(100, 100, 450, 300);
		frmSecurityP.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton btnAddClient = new JButton("Add client");
		btnAddClient.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(textFieldUserName.getText().trim().length() == 0)
					JOptionPane.showMessageDialog(null, "Username value missing");
				else if(textFieldHost.getText().trim().length() == 0)
					JOptionPane.showMessageDialog(null, "Hostname value missing");
				else if(textFieldPort.getText().trim().length() == 0 || !textFieldPort.getText().matches("\\d+"))
					JOptionPane.showMessageDialog(null, "Port value missing or invalid");
				else if(usersnameList.containsKey(textFieldUserName.getText()))
					JOptionPane.showMessageDialog(null, "Username unavailable");
				else
				{
					usernameInfo = new LinkedList<String>();
					usernameInfo.add(textFieldHost.getText());
					usernameInfo.add(textFieldPort.getText());
					usersnameList.put(textFieldUserName.getText(), usernameInfo);
					try {
						client1 = new ClientDesign(textFieldUserName.getText(), textFieldHost.getText(), textFieldPort.getText() );
					} catch (NoSuchAlgorithmException | IOException e1) {
						e1.printStackTrace();
					}
					client1.frmSecuruty2016.setVisible(true);
				}
			}
		});
		btnAddClient.setBounds(6, 220, 136, 37);
		
		textFieldUserName = new JTextField();
		textFieldUserName.setBounds(6, 32, 426, 28);
		textFieldUserName.setColumns(10);
		frmSecurityP.getContentPane().setLayout(null);
		frmSecurityP.getContentPane().add(btnAddClient);
		frmSecurityP.getContentPane().add(textFieldUserName);
		
		textFieldHost = new JTextField();
		textFieldHost.setColumns(10);
		textFieldHost.setBounds(6, 85, 136, 28);
		frmSecurityP.getContentPane().add(textFieldHost);
		
		textFieldPort = new JTextField();
		textFieldPort.setColumns(10);
		textFieldPort.setBounds(6, 137, 136, 28);
		frmSecurityP.getContentPane().add(textFieldPort);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(6, 15, 172, 16);
		frmSecurityP.getContentPane().add(lblUsername);
		
		JLabel lblHost = new JLabel("Host ");
		lblHost.setBounds(12, 72, 39, 16);
		frmSecurityP.getContentPane().add(lblHost);
		
		JLabel lblPortNumber = new JLabel("Port number");
		lblPortNumber.setBounds(12, 125, 91, 16);
		frmSecurityP.getContentPane().add(lblPortNumber);

		JLabel lblClientsAvaiable = new JLabel("Clients avaiable");
		lblClientsAvaiable.setBounds(183, 72, 238, 16);
		frmSecurityP.getContentPane().add(lblClientsAvaiable);
		
		JTextArea textAreaList = new JTextArea();
		textAreaList.setBounds(177, 141, 244, 116);
		frmSecurityP.getContentPane().add(textAreaList);
		
		JComboBox comboBoxClients = new JComboBox();
		comboBoxClients.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	String value = comboBoxClients.getSelectedItem().toString();
				if(usersnameList.containsKey(value))
				{
					LinkedList<String> info = usersnameList.get(value);
					if(info != null)
					{
						textAreaList.setText("");
						textAreaList.replaceSelection("Name: " + value + "\nHost:" + info.getFirst() + "\nPort: " +  info.getLast());
					}
				}
		    }
		});

		comboBoxClients.setBounds(174, 87, 258, 27);
		frmSecurityP.getContentPane().add(comboBoxClients);
		
		JButton btnShowClients = new JButton("Show clients");
		btnShowClients.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i=0;i < usersnameList.size(); i++)
				{
				    Set<String> Items = usersnameList.keySet();
				    Object[] s = Items.toArray();
				    comboBoxClients.addItem(s[i].toString());
				}
			}
		});
		btnShowClients.setBounds(6, 179, 136, 29);
		frmSecurityP.getContentPane().add(btnShowClients);
		
	}
}
