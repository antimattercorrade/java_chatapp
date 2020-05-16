
import java.io.*;
import java.net.*;
import java.util.*;

class ServerThread extends Thread { 
	
    public ServerLogin server = null;
    public Socket socket = null;
    public int ID = -1;
    public String username = "";
    public ObjectInputStream streamIn  =  null;
    public ObjectOutputStream streamOut = null;
    public LoginFrame ui;
    
    public ServerThread(ServerLogin _server, Socket _socket){  
    	super();
        server = _server;
        socket = _socket;
        ID     = socket.getPort();
        ui = _server.ui;
    }
    
    public void send(MessageSent msg){
        try {
            streamOut.writeObject(msg);
            streamOut.flush();
        } 
        catch (IOException ex) {
            System.out.println("Exception [SocketClient : send(...)]");
        }
    }
    
    
    
    public int getID(){  
	    return ID;
    }
   
    @SuppressWarnings("deprecation")
	public void run(){  
    	ui.textArea.append("\nServer Thread " + ID + " running.");
        while (true){  
    	    try{  
                MessageSent msg = (MessageSent) streamIn.readObject();
                server.handle(ID, msg);
            }
            catch(Exception ioe){  
            	System.out.println(ID + " ERROR reading: " + ioe.getMessage());
                server.remove(ID);
                stop();
            }
        }
    }
    
    public void open() throws IOException {  
        streamOut = new ObjectOutputStream(socket.getOutputStream());
        streamOut.flush();
        streamIn = new ObjectInputStream(socket.getInputStream());
    }
    
    public void close() throws IOException {  
    	if (socket != null)    socket.close();
        if (streamIn != null)  streamIn.close();
        if (streamOut != null) streamOut.close();
    }
}





public class ServerLogin implements Runnable {
    
    public ServerThread clients[];
    public ServerSocket server = null;
    public Thread       thread = null;
    public int clientCount = 0, port = 13000;
    public LoginFrame ui;
    public LoginDatabase db;
    public History hist;

    public ServerLogin(LoginFrame frame){
       
        clients = new ServerThread[50];
        ui = frame;
        db = new LoginDatabase();
        hist = new History();
        
	try{  
	    server = new ServerSocket(port);
            port = server.getLocalPort();
	    ui.textArea.append("Server startet. IP : " + InetAddress.getLocalHost() + ", Port : " + server.getLocalPort());
	    start(); 
        }
	catch(IOException ioe){  
            ui.textArea.append("Can not bind to port : " + port + "\nRetrying"); 
            ui.RetryStart(0);
	}
    }
    
    public ServerLogin(LoginFrame frame, int Port){
       
        clients = new ServerThread[50];
        ui = frame;
        port = Port;
        db = new LoginDatabase();
        
	try{  
	    server = new ServerSocket(port);
            port = server.getLocalPort();
	    ui.textArea.append("Server startet. IP : " + InetAddress.getLocalHost() + ", Port : " + server.getLocalPort());
	    start(); 
        }
	catch(IOException ioe){  
            ui.textArea.append("\nCan not bind to port " + port + ": " + ioe.getMessage()); 
	}
    }
	
    public void run(){  
	while (thread != null){  
            try{  
		ui.textArea.append("\nWaiting for a client ..."); 
	        addThread(server.accept()); 
	    }
	    catch(Exception ioe){ 
                ui.textArea.append("\nServer accept error: \n");
                ui.RetryStart(0);
	    }
        }
    }
	
    public void start(){  
    	if (thread == null){  
            thread = new Thread(this); 
	    thread.start();
	}
    }
    
    @SuppressWarnings("deprecation")
    public void stop(){  
        if (thread != null){  
            thread.stop(); 
	    thread = null;
	}
    }
    
    private int findClient(int ID){  
    	for (int i = 0; i < clientCount; i++){
        	if (clients[i].getID() == ID){
                    return i;
                }
	}
	return -1;
    }
	
    public synchronized void handle(int ID, MessageSent msg){  
    	
	if (msg.content.equals(".bye")){
            Announce("signout", "SERVER", msg.sender);
            remove(ID); 
	}
	else{
            if(msg.type.equals("login")){
                if(findUserThread(msg.sender) == null){
                	
                    if(db.checkLogin(msg.sender, msg.content)){
                    	clients[findClient(ID)].username = msg.sender;
                        clients[findClient(ID)].send(new MessageSent("login", "SERVER", "TRUE", msg.sender));
                        Announce("newuser", "SERVER", msg.sender);
                        SendUserList(msg.sender);
                    }
                    else{
                        clients[findClient(ID)].send(new MessageSent("login", "SERVER", "FALSE", msg.sender));
                    } 
                }
                else{
                    clients[findClient(ID)].send(new MessageSent("login", "SERVER", "FALSE", msg.sender));
                }
            }
            else if(msg.type.equals("message")){
                if(msg.recipient.equals("All")){
                    Announce("message", msg.sender, msg.content);
                }
                else{
                    findUserThread(msg.recipient).send(new MessageSent(msg.type, msg.sender, msg.content, msg.recipient));
                    clients[findClient(ID)].send(new MessageSent(msg.type, msg.sender, msg.content, msg.recipient));
                }
            }
            else if(msg.type.equals("test")){
                clients[findClient(ID)].send(new MessageSent("test", "SERVER", "OK", msg.sender));
            }
            else if(msg.type.equals("signup")){
                if(findUserThread(msg.sender) == null){
                    if(!db.userExists(msg.sender)){
                        db.addUser(msg.sender, msg.content);
                        hist.addTable(msg.sender);
                        clients[findClient(ID)].username = msg.sender;
                        clients[findClient(ID)].send(new MessageSent("signup", "SERVER", "TRUE", msg.sender));
                        clients[findClient(ID)].send(new MessageSent("login", "SERVER", "TRUE", msg.sender));
                        Announce("newuser", "SERVER", msg.sender);
                        SendUserList(msg.sender);
                    }
                    else{
                        clients[findClient(ID)].send(new MessageSent("signup", "SERVER", "FALSE", msg.sender));
                    }
                }
                else{
                    clients[findClient(ID)].send(new MessageSent("signup", "SERVER", "FALSE", msg.sender));
                }
            }
            else if(msg.type.equals("upload_req")){
                if(msg.recipient.equals("All")){
                    clients[findClient(ID)].send(new MessageSent("message", "SERVER", "Uploading to 'All' forbidden", msg.sender));
                }
                else{
                    findUserThread(msg.recipient).send(new MessageSent("upload_req", msg.sender, msg.content, msg.recipient));
                }
            }
            else if(msg.type.equals("upload_res")){
                if(!msg.content.equals("NO")){
                    String IP = findUserThread(msg.sender).socket.getInetAddress().getHostAddress();
                    findUserThread(msg.recipient).send(new MessageSent("upload_res", IP, msg.content, msg.recipient));
                }
                else{
                    findUserThread(msg.recipient).send(new MessageSent("upload_res", msg.sender, msg.content, msg.recipient));
                }
            }
	}
    }
    
    public void Announce(String type, String sender, String content){
        MessageSent msg = new MessageSent(type, sender, content, "All");
        for(int i = 0; i < clientCount; i++){
            clients[i].send(msg);
        }
    }
    
    public void SendUserList(String toWhom){
        for(int i = 0; i < clientCount; i++){
            findUserThread(toWhom).send(new MessageSent("newuser", "SERVER", clients[i].username, toWhom));
        }
    }
    
    public ServerThread findUserThread(String usr){
        for(int i = 0; i < clientCount; i++){
            if(clients[i].username.equals(usr)){
                return clients[i];
            }
        }
        return null;
    }
	
    @SuppressWarnings("deprecation")
    public synchronized void remove(int ID){  
    int pos = findClient(ID);
        if (pos >= 0){  
            ServerThread toTerminate = clients[pos];
            ui.textArea.append("\nRemoving client thread " + ID + " at " + pos);
	    if (pos < clientCount-1){
                for (int i = pos+1; i < clientCount; i++){
                    clients[i-1] = clients[i];
	        }
	    }
	    clientCount--;
	    try{  
	      	toTerminate.close(); 
	    }
	    catch(IOException ioe){  
	      	ui.textArea.append("\nError closing thread: " + ioe); 
	    }
	    toTerminate.stop(); 
	}
    }
    
    private void addThread(Socket socket){  
	if (clientCount < clients.length){  
            ui.textArea.append("\nClient accepted: " + socket);
	    clients[clientCount] = new ServerThread(this, socket);
	    try{  
	      	clients[clientCount].open(); 
	        clients[clientCount].start();  
	        clientCount++; 
	    }
	    catch(IOException ioe){  
	      	ui.textArea.append("\nError opening thread: " + ioe); 
	    } 
	}
	else{
            ui.textArea.append("\nClient refused: maximum " + clients.length + " reached.");
	}
    }
}
