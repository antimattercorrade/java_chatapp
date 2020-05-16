import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JTextArea;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.BorderLayout;
import javax.swing.JSeparator;
import java.awt.event.ActionEvent;


public class ChatClient extends JFrame {
	public JTextField textField,textField_1,textField_2,textField_3, textField_4,textField_5;
	public JButton btnConnect,btnLogin,btnSignup,btnSendMessage,btnFile,btnSendFile,btnYes;
	public JList<String> list;
	
	public ClientLogin client;
    public int port;
    public String serverAddr, username, password;
    public Thread clientThread;
    public DefaultListModel<String> model;
    public File file;
    public ServerLogin server;
    private JButton btnChatHistory;
    public History hist;
    public JTextArea textArea;
    private JScrollPane scrollPane;
    
	
	public ChatClient()
	{
		initializeComponents();
		
		this.addWindowListener(new WindowListener() {
            @Override public void windowClosing(WindowEvent e) { 
            	try{ 
            		client.send(new MessageSent("message", username, ".bye", "SERVER")); 
            		clientThread.stop();  
            		}
            	catch(Exception ex){} 
            	}

			@Override
			public void windowOpened(WindowEvent e) {
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
        });
        
    }

        
	
	
	
	
	
	
	
	private void initializeComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Chat Client");
		setSize(660,500);
		getContentPane().setLayout(null);
		
		JLabel lblUsername = new JLabel("Username : ");
		lblUsername.setBounds(10, 50, 86, 18);
		getContentPane().add(lblUsername);
		
		JLabel lblHostAddress = new JLabel("Host Address : ");
		lblHostAddress.setBounds(10, 22, 108, 18);
		getContentPane().add(lblHostAddress);
		
		textField = new JTextField();
		textField.setBounds(128, 22, 163, 19);
		textField.setText("localhost");
		getContentPane().add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(128, 50, 163, 19);
		getContentPane().add(textField_1);
		
		JLabel lblHostPort = new JLabel("Host Port : ");
		lblHostPort.setBounds(319, 22, 73, 18);
		getContentPane().add(lblHostPort);
		
		JLabel lblPassword = new JLabel("Password : ");
		lblPassword.setBounds(319, 53, 73, 18);
		getContentPane().add(lblPassword);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setText("13000");
		textField_2.setBounds(390, 22, 163, 19);
		getContentPane().add(textField_2);
		
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(390, 50, 163, 19);
		getContentPane().add(textField_3);
		
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnConnectActionPerformed(e);
			}
		});
		btnConnect.setBounds(20, 101, 85, 21);
		getContentPane().add(btnConnect);
		
		btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLoginActionPerformed(e);
			}
		});
		btnLogin.setBounds(172, 101, 85, 21);
		getContentPane().add(btnLogin);
		
		btnSignup = new JButton("Signup");
		btnSignup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSignupActionPerformed(e);
			}
		});
		btnSignup.setBounds(319, 101, 85, 21);
		getContentPane().add(btnSignup);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 132, 613, 8);
		getContentPane().add(separator);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(456, 154, 149, 196);
		getContentPane().add(scrollPane_1);
		
		model=new DefaultListModel<String>();
		list=new JList<String>(model);
		model.addElement("All");
		list.setSelectedIndex(0);
		scrollPane_1.setViewportView(list);
		
		JLabel lblMessage = new JLabel("Message : ");
		lblMessage.setBounds(10, 370, 73, 35);
		getContentPane().add(lblMessage);
		
		JLabel lblFile = new JLabel("File : ");
		lblFile.setBounds(10, 432, 73, 18);
		getContentPane().add(lblFile);
		
		textField_4 = new JTextField();
		textField_4.setColumns(10);
		textField_4.setBounds(79, 370, 382, 35);
		getContentPane().add(textField_4);
		
		textField_5 = new JTextField();
		textField_5.setColumns(10);
		textField_5.setBounds(79, 431, 382, 21);
		getContentPane().add(textField_5);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 414, 613, 8);
		getContentPane().add(separator_1);
		
		btnSendMessage = new JButton("Send Message");
		btnSendMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSendMessageActionPerformed(e);
			}
		});
		btnSendMessage.setBounds(481, 369, 142, 35);
		getContentPane().add(btnSendMessage);
		
		btnFile = new JButton("...");
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnActionPerformed(e);
			}
		});
		btnFile.setBounds(468, 431, 55, 21);
		getContentPane().add(btnFile);
		
		btnSendFile = new JButton("Send File");
		btnSendFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSendFileActionPerformed(e);
			}
		});
		btnSendFile.setBounds(533, 431, 90, 21);
		getContentPane().add(btnSendFile);	
		
		btnChatHistory = new JButton("Save Chat History");
		btnChatHistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hist = new History();
				String area=textArea.getText();
				String username= textField_1.getText();
				hist.deleteChat(username);
				for(String line: area.split("\\n"))
				{
						hist.addData(username, line);
					
				}
				JOptionPane.showMessageDialog(null,"Chat History Saved.");
			}
		});
		btnChatHistory.setBounds(468, 101, 125, 21);
		getContentPane().add(btnChatHistory);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 150, 402, 200);
		getContentPane().add(scrollPane);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
	}
	
	private void btnConnectActionPerformed(ActionEvent e)
	{

		serverAddr = textField.getText(); port = Integer.parseInt(textField_2.getText());
        
        if(!serverAddr.isEmpty() && !textField_2.getText().isEmpty()){
            try{
                client = new ClientLogin(this);
                clientThread = new Thread(client);
                clientThread.start();
                client.send(new MessageSent("test", "testUser", "testContent", "SERVER"));
            }
            catch(Exception ex){
                textArea.append("[Application > Me] : Server not found\n");
            }
        }
	}
	
	private void btnLoginActionPerformed(ActionEvent e)
	{
		username = textField_1.getText();
        password = textField_3.getText();
        
        if(!username.isEmpty() && !password.isEmpty()){
            client.send(new MessageSent("login", username, password, "SERVER"));
        }
    	hist = new History();
    	String area=hist.readData(textField_1.getText());
    	if(area.contentEquals("No"))
    	{
    		
    	}
    	else
    	{
        	area=area+System.lineSeparator();
        	textArea.append(area);
    	}
        
	}
	
	private void btnSendMessageActionPerformed(ActionEvent e)
	{
		String msg = textField_4.getText();
        String target = list.getSelectedValue().toString();
        
        if(!msg.isEmpty() && !target.isEmpty()){
            textField_4.setText("");
            client.send(new MessageSent("message", username, msg, target));
        }
	}
	
	private void btnSignupActionPerformed(ActionEvent e)
	{
		username = textField_1.getText();
        password = textField_3.getText();
        
        if(!username.isEmpty() && !password.isEmpty()){
            client.send(new MessageSent("signup", username, password, "SERVER"));
        }
        
	}
	private void btnActionPerformed(ActionEvent e)
	{
		JFileChooser fileChooser = new JFileChooser();
        fileChooser.showDialog(this, "Select File");
        file = fileChooser.getSelectedFile();
        
        if(file != null){
            if(!file.getName().isEmpty()){
                btnSendFile.setEnabled(true); String str;
                
                if(textField_5.getText().length() > 30){
                    String t = file.getPath();
                    str = t.substring(0, 20) + " [...] " + t.substring(t.length() - 20, t.length());
                }
                else{
                    str = file.getPath();
                }
                textField_5.setText(str);
            }
        }
	}
	private void btnSendFileActionPerformed(ActionEvent e)
	{
		long size = file.length();
        if(size < 120 * 1024 * 1024){
            client.send(new MessageSent("upload_req", username, file.getName(), list.getSelectedValue().toString()));
        }
        else{
            textArea.append("[Application > Me] : File is size too large\n");
        }
	}
	
	
	
	
	public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
        catch(Exception ex){
            System.out.println("Look & Feel exception");
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChatClient().setVisible(true);
            }
        });
    }
}
