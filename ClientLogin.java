
import java.io.*;
import java.net.*;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ClientLogin implements Runnable{
    
    public int port;
    public String serverAddr;
    public Socket socket;
    public ChatClient ui;
    public ObjectInputStream In;
    public ObjectOutputStream Out;
    public History hist;
    
    public ClientLogin(ChatClient frame) throws IOException{
        ui = frame; this.serverAddr = ui.serverAddr; this.port = ui.port;
        socket = new Socket(InetAddress.getByName(serverAddr), port);
            
        Out = new ObjectOutputStream(socket.getOutputStream());
        Out.flush();
        In = new ObjectInputStream(socket.getInputStream());
        
        hist = new History();
    }

    @Override
    public void run() {
        boolean keepRunning = true;
        while(keepRunning){
            try {
                MessageSent msg = (MessageSent) In.readObject();
                System.out.println("Incoming : "+msg.toString());
                
                if(msg.type.equals("message")){
                    if(msg.recipient.equals(ui.username)){
                        ui.textArea.append("["+msg.sender +" > Me] : " + msg.content + "\n");
                    }
                    else{
                        ui.textArea.append("["+ msg.sender +" > "+ msg.recipient +"] : " + msg.content + "\n");
                    }
                                            
                    
                }
                else if(msg.type.equals("login")){
                    if(msg.content.equals("TRUE")){
                        ui.btnLogin.setEnabled(false); 
                        ui.btnSignup.setEnabled(false);                        
                        ui.btnSendMessage.setEnabled(true); 
                        ui.btnFile.setEnabled(true);
                        ui.textArea.append("[SERVER > Me] : Login Successful\n");
                        ui.textField_1.setEnabled(false); 
                        ui.textField_3.setEnabled(false);
                    }
                    else{
                        ui.textArea.append("[SERVER > Me] : Login Failed\n");
                    }
                }
                else if(msg.type.equals("test")){
                    ui.btnConnect.setEnabled(false);
                    ui.btnLogin.setEnabled(true); 
                    ui.btnSignup.setEnabled(true);
                    ui.textField_1.setEnabled(true); 
                    ui.textField_3.setEnabled(true);
                    ui.textField.setEditable(false); 
                    ui.textField_2.setEditable(false);
                }
                else if(msg.type.equals("newuser")){
                    if(!msg.content.equals(ui.username)){
                        boolean exists = false;
                        for(int i = 0; i < ui.model.getSize(); i++){
                            if(ui.model.getElementAt(i).equals(msg.content)){
                                exists = true; break;
                            }
                        }
                        if(!exists){ ui.model.addElement(msg.content); }
                    }
                }
                else if(msg.type.equals("signup")){
                    if(msg.content.equals("TRUE")){
                        ui.btnLogin.setEnabled(false); ui.btnSignup.setEnabled(false);
                        ui.btnSendMessage.setEnabled(true); ui.btnFile.setEnabled(true);
                        ui.textArea.append("[SERVER > Me] : Singup Successful\n");
                    }
                    else{
                        ui.textArea.append("[SERVER > Me] : Signup Failed\n");
                    }
                }
                else if(msg.type.equals("signout")){
                    if(msg.content.equals(ui.username)){
                        ui.textArea.append("["+ msg.sender +" > Me] : Bye\n");
                        ui.btnConnect.setEnabled(true); ui.btnSendMessage.setEnabled(false); 
                        ui.textField.setEditable(true); ui.textField_2.setEditable(true);
                        
                        for(int i = 1; i < ui.model.size(); i++){
                            ui.model.removeElementAt(i);
                        }
                        
                        ui.clientThread.stop();
                    }
                    else{
                        ui.model.removeElement(msg.content);
                        ui.textArea.append("["+ msg.sender +" > All] : "+ msg.content +" has signed out\n");
                    }
                }
                else if(msg.type.equals("upload_req")){
                    
                    if(JOptionPane.showConfirmDialog(ui, ("Accept '"+msg.content+"' from "+msg.sender+" ?")) == 0){
                        
                        JFileChooser jf = new JFileChooser();
                        jf.setSelectedFile(new File(msg.content));
                        int returnVal = jf.showSaveDialog(ui);
                       
                        String saveTo = jf.getSelectedFile().getPath();
                        if(saveTo != null && returnVal == JFileChooser.APPROVE_OPTION){
                            Download dwn = new Download(saveTo, ui);
                            Thread t = new Thread(dwn);
                            t.start();
                            //send(new Message("upload_res", (""+InetAddress.getLocalHost().getHostAddress()), (""+dwn.port), msg.sender));
                            send(new MessageSent("upload_res", ui.username, (""+dwn.port), msg.sender));
                        }
                        else{
                            send(new MessageSent("upload_res", ui.username, "NO", msg.sender));
                        }
                    }
                    else{
                        send(new MessageSent("upload_res", ui.username, "NO", msg.sender));
                    }
                }
                else if(msg.type.equals("upload_res")){
                    if(!msg.content.equals("NO")){
                        int port  = Integer.parseInt(msg.content);
                        String addr = msg.sender;
                        
                        ui.btnFile.setEnabled(false); ui.btnSendFile.setEnabled(false);
                        Upload upl = new Upload(addr, port, ui.file, ui);
                        Thread t = new Thread(upl);
                        t.start();
                    }
                    else{
                        ui.textArea.append("[SERVER > Me] : "+msg.sender+" rejected file request\n");
                    }
                }
                else{
                    ui.textArea.append("[SERVER > Me] : Unknown message type\n");
                }
            }
            catch(Exception ex) {
                keepRunning = false;
                ui.textArea.append("[Application > Me] : Connection Failure\n");
                ui.btnConnect.setEnabled(true); ui.textField.setEditable(true); ui.textField_2.setEditable(true);
                ui.btnSendMessage.setEnabled(false); ui.btnFile.setEnabled(false); ui.btnFile.setEnabled(false);
                
                for(int i = 1; i < ui.model.size(); i++){
                    ui.model.removeElementAt(i);
                }
                
                ui.clientThread.stop();
                
                System.out.println("Exception SocketClient run()");
                ex.printStackTrace();
            }
        }
    }
    
    public void send(MessageSent msg){
        try {
            Out.writeObject(msg);
            Out.flush();
            System.out.println("Outgoing : "+msg.toString());
            
            
        } 
        catch (IOException ex) {
            System.out.println("Exception SocketClient send()");
        }
    }
    
    public void closeThread(Thread t){
        t = null;
    }
}
