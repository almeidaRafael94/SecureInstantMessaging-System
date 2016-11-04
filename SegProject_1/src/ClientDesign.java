import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.json.JSONException;

public class ClientDesign implements ActionListener  {

	JFrame frmSecuruty2016;
	private JSeparator separator_1;
	private JTextPane logTextPane;
	private JLabel lblLog;
	private JButton btnConnect;

	private JLabel lblSendTo;
	private JButton btnSend;
    private String logMessage = null;
    private Color color = null;
    private StyledDocument doc;
    private Style style;
    private JButton btnList;
    private JScrollPane sendScrollPane;
    private JTextPane sendTextArea;
    private JScrollPane receiveScollPane;
    private JTextPane receiveTextArea;
    private JLabel usernameLabel;
    private JButton btnStart;
    private JComboBox comboBoxUsernames;
    
    private boolean isStarted = false;
    private boolean isConnected = false;
    private boolean isConnectedToServer = false;
    private boolean isClient_connect = false;
    private boolean isClient_comm = false;
    private String message;
    
    private String username;
    private String port;
    private String host;
    private String level;
    private Client client;
    
    private String dstinationID = "";
    

	/**
	 * Launch the application.
	 */
    

	/**
	 * Create the application.
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	public ClientDesign(String username,String level ,String host, String port) throws NoSuchAlgorithmException, IOException {
		this.username = username;
		this.host = host;
		this.port = port;
		this.level = level;
		isConnected = false;
		client = new Client(username, level);
		//client.config(host, Integer.parseInt(port));
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSecuruty2016 = new JFrame();
		frmSecuruty2016.setTitle("Security 2016 P4 68486   Username: " + username);
		frmSecuruty2016.setBounds(100, 100, 600, 400);
		frmSecuruty2016.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmSecuruty2016.getContentPane().setLayout(null);
		
		btnSend = new JButton("Send");
		btnSend.setEnabled(false);
		btnSend.setBounds(475, 154, 100, 100);
		btnSend.addActionListener(this);
		frmSecuruty2016.getContentPane().add(btnSend);
		
		logTextPane = new JTextPane();
		logTextPane.setEditable(false);
		logTextPane.setForeground(Color.BLACK);
		logTextPane.setBackground(Color.WHITE);
		logTextPane.setToolTipText("");
		logTextPane.setBounds(2, 2, 99, 16);
		frmSecuruty2016.getContentPane().add(logTextPane);
		
		JScrollPane logScollPane = new JScrollPane(logTextPane);
		logScollPane.setBounds(272,30,305,100);
		logScollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);;
		frmSecuruty2016.getContentPane().add(logScollPane);
		
		sendScrollPane = new JScrollPane((Component) null);
		sendScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sendScrollPane.setBounds(20, 154, 425, 100);
		frmSecuruty2016.getContentPane().add(sendScrollPane);
		
		sendTextArea = new JTextPane();
		sendScrollPane.setViewportView(sendTextArea);
		
		receiveScollPane = new JScrollPane((Component) null);
		receiveScollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		receiveScollPane.setBounds(145, 266, 425, 96);
		frmSecuruty2016.getContentPane().add(receiveScollPane);
		
		receiveTextArea = new JTextPane();
		receiveScollPane.setViewportView(receiveTextArea);
		
		lblLog = new JLabel("Log"); 
		lblLog.setBounds(272, 9, 23, 16);
		frmSecuruty2016.getContentPane().add(lblLog);
		
		separator_1 = new JSeparator();
		separator_1.setBounds(6, 130, 571, 12);
		frmSecuruty2016.getContentPane().add(separator_1);
		
		btnConnect = new JButton("Connect");
		btnConnect.setBounds(10, 45, 161, 29);
		btnConnect.addActionListener(this);
		frmSecuruty2016.getContentPane().add(btnConnect);
		
		lblSendTo = new JLabel("Destination username:");
		lblSendTo.setBounds(16, 88, 157, 16);
		frmSecuruty2016.getContentPane().add(lblSendTo);
		
		usernameLabel = new JLabel("");
		usernameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		usernameLabel.setBounds(365, 9, 212, 16);
		usernameLabel.setEnabled(true);
        usernameLabel.setText(username + " 		(" + this.level + ")"); 
		frmSecuruty2016.getContentPane().add(usernameLabel);
		
		btnList = new JButton("<html>Available<br />  Users </html>");
		btnList.setBounds(20, 266, 100, 100);
		btnList.addActionListener(this);
		frmSecuruty2016.getContentPane().add(btnList);
		
		btnStart = new JButton("Start");
		btnStart.setBounds(8, 12, 161, 29);
		btnStart.addActionListener(this);
		frmSecuruty2016.getContentPane().add(btnStart);
		
		comboBoxUsernames = new JComboBox();
		comboBoxUsernames.setBounds(10, 103, 232, 27);
		comboBoxUsernames.setVisible(true);
		frmSecuruty2016.getContentPane().add(comboBoxUsernames);
		
	}
	
	public void actionPerformed(ActionEvent actionEvent) {
        doc = logTextPane.getStyledDocument();
        style = logTextPane.addStyle("Style", null);
        if(actionEvent.getSource() == btnConnect)
        {
        	logMessage = "";
	        if(!isConnected && isStarted)
	        {
	        	comboBoxUsernames.removeAll();
	        	sendTextArea.removeAll();
	        	logTextPane.setText("");
	        	sendTextArea.removeAll();
	        	
	        	
	        	isConnected = true;
	        	color = Color.GREEN;
	        	logMessage = "Connection successful \n";
	        	btnConnect.setText("Disconnect");
		        sendTextArea.setEnabled(true);
		        btnSend.setEnabled(true);
		        receiveTextArea.setEnabled(true);	
		        try {
					client.send("connect", "", null, null);
				} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
						| NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | IOException
						| JSONException e) {
					e.printStackTrace();
				}
	        }
	        else if(isConnected && isStarted)
	        {	
	        	isConnected = false;
	        	client.disconnect();
	            color = Color.GREEN;
	            try {
					client.send("secure", "client-disconnect", null, null);
				} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
						| NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | IOException
						| JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		logMessage = "Disconnected \n";
	        	btnConnect.setText("Connect");
	        	sendTextArea.setText("");
	        	sendTextArea.setEnabled(false);
	        	btnSend.setEnabled(false);
	        	receiveTextArea.setEnabled(false);
	        	usernameLabel.setEnabled(false);
	        	usernameLabel.setText("");
	        }   
        }
        else if(actionEvent.getSource() == btnSend)
        {	
        	logMessage = "";
        	if(comboBoxUsernames.getSelectedItem().toString().isEmpty())
        	{	
        		JOptionPane.showMessageDialog(null, "Error: destination username not found \n Press the button List Clients to see the clients avaiable ");
        	}
        	else if(sendTextArea.getText().isEmpty())
        	{
        		JOptionPane.showMessageDialog(null, "Error: message to send is empty ");
        	}
        	else
        	{
        		List<String> clients = client.getClientsList();
        		String id = null;
        		for (String c : clients)
        		{
        			
        			if(c.split(",")[1].split(":")[1].trim().equals(comboBoxUsernames.getSelectedItem().toString()) )
        				id = c.split(",")[0].split(":")[1].trim();
        		}
        		if(id == null)
        			JOptionPane.showMessageDialog(null, "Error: destination username does not exists ");
        		else
        		{
        			color = Color.BLUE;
	        		logMessage = "Client " + username + " send message to " + comboBoxUsernames.getSelectedItem().toString() + "\n";
	        		
	        		try {
	        			client.setDst(id);
	        			if(!dstinationID.equals(id) && id != null)
	        			{
							client.send("secure", "client-connect", null, null);
							this.dstinationID = id;
	        			}
	        			
	        			//client needs time between "client-connect" and client-comm because generation of prime numbers can take a few miliseconds
	        			while(!client.clientContainSecret(id))
	        			{
	        				continue;
	        			}
	        			
						client.send("secure", "client-com", null, sendTextArea.getText());
					} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
							| NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | IOException
							| JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        	}
        }
        else if(actionEvent.getSource() == btnStart)
        {
        	logMessage = "";
        	if(!isStarted)
        	{
        		isStarted = true;
        		color = Color.GREEN;
        		logMessage = username + " begins session \n";
        		try {
					client.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
        else if(actionEvent.getSource() == btnList)
        {
        	logMessage = "";
        	if(isConnected)
        	{
        		try {
					client.send("secure", "list", null, null);
				} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
						| NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | IOException
						| JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        		List<String> clients = client.getClientsList();
        		comboBoxUsernames.removeAllItems();
        		receiveTextArea.removeAll();
        		if(clients.size() != 0)
        		{
	        		for(String client : clients)
	        		{
	        			comboBoxUsernames.addItem(client.split(",")[1].split(":")[1].trim());
	        			receiveTextArea.setText(receiveTextArea.getText() + client + "\n");
	        		}
        		}
        		else
        			receiveTextArea.setText(receiveTextArea.getText() + "Clients not faund" + "\n");
        	}
        }
        else
        	logMessage = "";
        
        Runnable WriteRunnable = new Runnable() {
            public void run() {
            	String messageTmp = client.getLastMessage(username);
            	if(isConnected)
            		if(messageTmp != message && messageTmp!= null && messageTmp!= "" && messageTmp.trim().length() != 0)
            		{
            			receiveTextArea.setText(receiveTextArea.getText() + messageTmp + "\n");
            			message = messageTmp;
            		}
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(WriteRunnable, 0, 2, TimeUnit.SECONDS);
        	
        StyleConstants.setForeground(style,color);
        try { 
        	if(!logMessage.equals(""))
        		doc.insertString(doc.getLength(), logMessage,style);}
        catch (BadLocationException e){}
   }
}

