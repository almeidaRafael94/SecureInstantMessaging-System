import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ClientDesign implements WindowListener,ActionListener  {

	JFrame frmSecuruty2016;
	private JSeparator separator_1;
	private JTextField usernameTextField;
	private JTextPane logTextPane;
	private JLabel lblLog;
	private JButton btnConnect;

	private JLabel lblSendTo;
	private JTextField destinationUsernameTextField;
	private JButton btnSend;
    private String logMessage = null;
    private Color color = null;
    private StyledDocument doc;
    private Style style;
    private JButton btnNButtonEw;
    private JScrollPane sendScrollPane;
    private JTextPane sendTextArea;
    private JScrollPane receiveScollPane;
    private JTextPane receiveTextArea;
    private JLabel usernameLabel;
    
    private boolean isConnected = false;
    private boolean isConnectedToServer = false;

	/**
	 * Launch the application.
	 */
	
   
    /*
    public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientDesign window = new ClientDesign();
					window.frmSecuruty2016.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
    */

	/**
	 * Create the application.
	 */
	public ClientDesign() {
		isConnected = false;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSecuruty2016 = new JFrame();
		frmSecuruty2016.setTitle("Securuty 2016 P4 GX");
		frmSecuruty2016.setBounds(100, 100, 600, 400);
		frmSecuruty2016.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSecuruty2016.getContentPane().setLayout(null);
		
		btnSend = new JButton("Send");
		btnSend.setEnabled(false);
		btnSend.setBounds(470, 154, 107, 100);
		btnSend.addActionListener(this);
		frmSecuruty2016.getContentPane().add(btnSend);
		
		usernameTextField = new JTextField();
		usernameTextField.setForeground(new Color(0, 0, 0));
		usernameTextField.setBounds(8, 51, 240, 30);
		frmSecuruty2016.getContentPane().add(usernameTextField);
		usernameTextField.setColumns(10);
		
		logTextPane = new JTextPane();
		logTextPane.setEditable(false);
		logTextPane.setForeground(Color.BLACK);
		logTextPane.setBackground(Color.WHITE);
		logTextPane.setToolTipText("");
		logTextPane.setBounds(16, 154, 180, 39);
		frmSecuruty2016.getContentPane().add(logTextPane);
		
		JScrollPane logScollPane = new JScrollPane(logTextPane);
		logScollPane.setBounds(272,30,305,100);
		logScollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);;
		frmSecuruty2016.getContentPane().add(logScollPane);
		
		sendScrollPane = new JScrollPane((Component) null);
		sendScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sendScrollPane.setBounds(26, 154, 420, 100);
		frmSecuruty2016.getContentPane().add(sendScrollPane);
		
		sendTextArea = new JTextPane();
		sendScrollPane.setViewportView(sendTextArea);
		
		receiveScollPane = new JScrollPane((Component) null);
		receiveScollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		receiveScollPane.setBounds(145, 266, 431, 96);
		frmSecuruty2016.getContentPane().add(receiveScollPane);
		
		receiveTextArea = new JTextPane();
		receiveScollPane.setViewportView(receiveTextArea);
		
		lblLog = new JLabel("Log"); 
		lblLog.setBounds(272, 9, 23, 16);
		frmSecuruty2016.getContentPane().add(lblLog);
		
		JLabel lblName = new JLabel("Username:");
		lblName.setBounds(10, 37, 71, 16);
		frmSecuruty2016.getContentPane().add(lblName);
		
		separator_1 = new JSeparator();
		separator_1.setBounds(6, 130, 571, 12);
		frmSecuruty2016.getContentPane().add(separator_1);
		
		btnConnect = new JButton("Connect");
		btnConnect.setBounds(6, 10, 161, 29);
		btnConnect.addActionListener(this);
		frmSecuruty2016.getContentPane().add(btnConnect);
		
		lblSendTo = new JLabel("Destination username:");
		lblSendTo.setBounds(10, 84, 157, 16);
		frmSecuruty2016.getContentPane().add(lblSendTo);
		
		destinationUsernameTextField = new JTextField();
		destinationUsernameTextField.setBounds(8, 100, 240, 30);
		frmSecuruty2016.getContentPane().add(destinationUsernameTextField);
		destinationUsernameTextField.setColumns(10);
		
		usernameLabel = new JLabel("");
		usernameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		usernameLabel.setBounds(365, 9, 212, 16);
		usernameLabel.setEnabled(false);
		frmSecuruty2016.getContentPane().add(usernameLabel);
		
		btnNButtonEw = new JButton("List clients");
		btnNButtonEw.setBounds(26, 262, 99, 100);
		frmSecuruty2016.getContentPane().add(btnNButtonEw);
	}
	
	public void actionPerformed(ActionEvent actionEvent) {
        doc = logTextPane.getStyledDocument();
        style = logTextPane.addStyle("Style", null);
        if(actionEvent.getSource() == btnConnect)
        {
	        if(!isConnected)
	        {
	        	if(usernameTextField.getText().isEmpty())
	        	{	
	        		color = Color.RED;
	        		logMessage = "Connection failed: source username missing \n";
	        	}
	        	else
	        	{	
	        		isConnected = true;
	        		usernameTextField.setEnabled(false);
	        		color = Color.GREEN;
	        		logMessage = "Connection successful \n";
	        		btnConnect.setText("Disconnect");
		        	sendTextArea.setEnabled(true);
		        	btnSend.setEnabled(true);
		        	receiveTextArea.setEnabled(true);
		        	usernameLabel.setEnabled(true);
		        	usernameLabel.setText(usernameTextField.getText()); 	
	        	}
	        }
	        else
	        {	
	        	isConnected = false;
	        	usernameTextField.setEnabled(true);
	            color = Color.GREEN;
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
        	if(destinationUsernameTextField.getText().isEmpty())
        	{	
        		color = Color.RED;
        		logMessage = "Error: destination username missing \n";
        	}
        	else if(sendTextArea.getText().isEmpty())
        	{
        		color = Color.RED;
        		logMessage = "Error: message box is empty \n";
        	}
        	else
        	{
        		color = Color.BLUE;
        		logMessage = "Message send \n";
        	}
        	
        }
        StyleConstants.setForeground(style,color);
        try { doc.insertString(doc.getLength(), logMessage,style);}
        catch (BadLocationException e){}
   }
			
	public boolean isConnected() {
		return isConnected;
	}
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
	public String getUsername() {
		return usernameTextField.getText();
	}
	public String getDestinationUsername() {
		return destinationUsernameTextField.getText();
	}
	public String getSendMessage() {
		return sendTextArea.getText();
	}
	public void setReceiveMessage(String message)
	{
		receiveTextArea.setText(receiveTextArea.getText() + "\n" + message);
	}
	public void setLogMessage(String message)
	{
		style = logTextPane.addStyle("Style", null);
		StyleConstants.setForeground(style,Color.blue);
        try { doc.insertString(doc.getLength(), logMessage,style);}
        catch (BadLocationException e){}
		//logTextPane.setText(logTextPane.getText() + "\n" + message);
	}
	public void setIsConnectedToServer(boolean c)
	{
		isConnectedToServer = c;
	}


	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
