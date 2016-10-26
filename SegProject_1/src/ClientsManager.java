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
	private JTextField textFieldLevel;

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
				else if(textFieldLevel.getText().trim().length() == 0 || !textFieldLevel.getText().matches("\\d+"))
					JOptionPane.showMessageDialog(null, "Level value missing or invalid");
				else
				{
					usernameInfo = new LinkedList<String>();
					usernameInfo.add(textFieldHost.getText());
					usernameInfo.add(textFieldPort.getText());
					usersnameList.put(textFieldUserName.getText(), usernameInfo);
					try {
						client1 = new ClientDesign(textFieldUserName.getText(),textFieldLevel.getText() ,textFieldHost.getText(), textFieldPort.getText() );
					} catch (NoSuchAlgorithmException | IOException e1) {
						e1.printStackTrace();
					}
					client1.frmSecuruty2016.setVisible(true);
				}
			}
		});
		btnAddClient.setBounds(6, 230, 136, 30);
		
		textFieldUserName = new JTextField();
		textFieldUserName.setBounds(6, 25, 426, 28);
		textFieldUserName.setColumns(10);
		frmSecurityP.getContentPane().setLayout(null);
		frmSecurityP.getContentPane().add(btnAddClient);
		frmSecurityP.getContentPane().add(textFieldUserName);
		
		textFieldHost = new JTextField();
		textFieldHost.setColumns(10);
		textFieldHost.setBounds(6, 70, 136, 28);
		frmSecurityP.getContentPane().add(textFieldHost);
		
		textFieldPort = new JTextField();
		textFieldPort.setColumns(10);
		textFieldPort.setBounds(6, 115, 136, 28);
		frmSecurityP.getContentPane().add(textFieldPort);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(12, 12, 172, 16);
		frmSecurityP.getContentPane().add(lblUsername);
		
		JLabel lblHost = new JLabel("Host ");
		lblHost.setBounds(12, 55, 39, 16);
		frmSecurityP.getContentPane().add(lblHost);
		
		JLabel lblPortNumber = new JLabel("Port number");
		lblPortNumber.setBounds(12, 100, 91, 16);
		frmSecurityP.getContentPane().add(lblPortNumber);

		JLabel lblClientsAvaiable = new JLabel("Clients avaiable");
		lblClientsAvaiable.setBounds(177, 55, 104, 16);
		frmSecurityP.getContentPane().add(lblClientsAvaiable);
		
		JTextArea textAreaList = new JTextArea();
		textAreaList.setBounds(177, 115, 244, 142);
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

		comboBoxClients.setBounds(170, 70, 258, 27);
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
		btnShowClients.setBounds(6, 198, 136, 30);
		frmSecurityP.getContentPane().add(btnShowClients);
		
		textFieldLevel = new JTextField();
		textFieldLevel.setBounds(6, 160, 134, 28);
		frmSecurityP.getContentPane().add(textFieldLevel);
		textFieldLevel.setColumns(10);
		
		JLabel lblLevel = new JLabel("Level");
		lblLevel.setBounds(12, 145, 61, 16);
		frmSecurityP.getContentPane().add(lblLevel);
		
	}
}
