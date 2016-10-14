import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ClientDesign {

	JFrame frame;
	private JTextArea receiveTextArea;
	private JTextArea sendTextArea;
	private JSeparator separator_1;
	private JTextField usernameTextField;
	private JTextPane logTextPane;
	private JLabel lblLog;
	private JToggleButton tglbtnNewToggleButton;
	private ActionListener actionListener;
	private JLabel lblSendTo;
	private JTextField destinationUsernameTextField;
	private JButton btnSend;
    private String logMessage = null;
    private Color color = null;
    private StyledDocument doc;
    private Style style;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientDesign window = new ClientDesign();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientDesign() {
		initialize();
		tglbtnNewToggleButton.addActionListener(actionListener);
		btnSend.addActionListener(actionListener);

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		btnSend = new JButton("Send");
		btnSend.setEnabled(false);
		btnSend.setBounds(370, 185, 80, 29);
		frame.getContentPane().add(btnSend);
		
		usernameTextField = new JTextField();
		usernameTextField.setForeground(new Color(0, 0, 0));
		usernameTextField.setBounds(8, 51, 163, 28);
		frame.getContentPane().add(usernameTextField);
		usernameTextField.setColumns(10);
		
		receiveTextArea = new JTextArea();
		receiveTextArea.setEnabled(false);
		receiveTextArea.setEditable(false);
		receiveTextArea.setBounds(6, 218, 438, 45);
		frame.getContentPane().add(receiveTextArea);
		
		sendTextArea = new JTextArea();
		sendTextArea.setEnabled(false);
		sendTextArea.setBounds(6, 140, 438, 45);
		frame.getContentPane().add(sendTextArea);
		
		logTextPane = new JTextPane();
		logTextPane.setForeground(Color.BLACK);
		logTextPane.setBackground(Color.WHITE);
		logTextPane.setEditable(false);
		logTextPane.setToolTipText("");
		logTextPane.setBounds(162, 30, 71, 58);
		frame.getContentPane().add(logTextPane);
		
		JScrollPane sp = new JScrollPane(logTextPane);
		sp.setBounds(180,30,250,90);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);;
		frame.getContentPane().add(sp);
		
		lblLog = new JLabel("Log"); 
		lblLog.setBounds(191, 15, 61, 16);
		frame.getContentPane().add(lblLog);
		
		JLabel lblReceive = new JLabel("Receive");
		lblReceive.setBounds(6, 202, 61, 16);
		frame.getContentPane().add(lblReceive);
		
		JLabel lblName = new JLabel("Username:");
		lblName.setBounds(10, 37, 71, 16);
		frame.getContentPane().add(lblName);
		
		separator_1 = new JSeparator();
		separator_1.setBounds(6, 130, 438, 12);
		frame.getContentPane().add(separator_1);
		
		tglbtnNewToggleButton = new JToggleButton("Connect");
		tglbtnNewToggleButton.setBounds(6, 10, 161, 29);
		frame.getContentPane().add(tglbtnNewToggleButton);
		
		lblSendTo = new JLabel("Destination username:");
		lblSendTo.setBounds(10, 84, 157, 16);
		frame.getContentPane().add(lblSendTo);
		
		destinationUsernameTextField = new JTextField();
		destinationUsernameTextField.setBounds(8, 100, 163, 28);
		frame.getContentPane().add(destinationUsernameTextField);
		destinationUsernameTextField.setColumns(10);
		
		JLabel usernameLabel = new JLabel("");
		usernameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		usernameLabel.setBounds(232, 15, 212, 16);
		usernameLabel.setEnabled(false);
		frame.getContentPane().add(usernameLabel);
		
		actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
		        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
		        boolean selected = abstractButton.getModel().isSelected();
		        doc = logTextPane.getStyledDocument();
		        style = logTextPane.addStyle("Style", null);
		        if(actionEvent.getSource() == tglbtnNewToggleButton)
		        {
			        if(selected)
			        {
			        	if(usernameTextField.getText().isEmpty())
			        	{	
			        		color = Color.RED;
			        		logMessage = "Connection failed: source username missing \n";
			        	}
			        	else
			        	{	
			        		color = Color.GREEN;
			        		logMessage = "Connection successful \n";
			        		tglbtnNewToggleButton.setText("Disconnect");
				        	sendTextArea.setEnabled(true);
				        	btnSend.setEnabled(true);
				        	receiveTextArea.setEnabled(true);
				        	usernameLabel.setEnabled(true);
				        	usernameLabel.setText(usernameTextField.getText());
			        	}
			        }
			        else
			        {	
			            color = Color.GREEN;
		        		logMessage = "Disconnected \n";
			        	tglbtnNewToggleButton.setText("Connect");
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
		};
		
		
	}
}
