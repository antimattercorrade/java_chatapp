import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

public class LoginFrame extends JFrame {

	private JPanel contentPane;
	public ServerLogin server;
    public Thread serverThread;
    public JFileChooser fileChooser;
    public JTextArea textArea;
    
    public LoginFrame() {
		
		initializeComponents();
		fileChooser = new JFileChooser();
		
	}
	
	
	private void initializeComponents()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 400);
		setTitle("Server Login");
		contentPane = new JPanel();
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnStartServer = new JButton("Start Server");
		btnStartServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				butn1action(e);
			}
		});
		btnStartServer.setBounds(233, 10, 87, 21);
		//btnNewButton_1.setEnabled(false);
		contentPane.add(btnStartServer);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 41, 516, 312);
		contentPane.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

	}
	
	private void butn1action(ActionEvent ev1)
	{
		server = new ServerLogin(this);
   
	}
   
	
	
	public void RetryStart(int port){
        if(server != null){ 
        	server.stop(); 
        	}
        server = new ServerLogin(this, port);
    }
	
public static void main(String[] args) {
		
		try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception ex){
            System.out.println("Look & Feel Exception");
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
             new LoginFrame().setVisible(true);
            }
        });
		
		}

	
}

