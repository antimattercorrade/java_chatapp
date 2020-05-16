
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JOptionPane;

public class LoginDatabase {
    
    public boolean userExists(String username){
        
        try{
            
        	Class.forName("com.mysql.cj.jdbc.Driver");
			Connection cn= DriverManager.getConnection("jdbc:mysql://localhost/chat?user=root&password=123456789");
			String q1="select * from login where username=?";
			PreparedStatement st1= cn.prepareStatement(q1);
			st1.setString(1, username);
			ResultSet rs=st1.executeQuery();
			boolean ch=false;
			while(rs.next())
			{
				ch=true;
			}
			cn.close();
			return ch;
        	
        	}
        catch(Exception ex){
            System.out.println("Error: "+ex.getMessage());
            return false;
        }
    }
    
    public boolean checkLogin(String username, String password){
        
        
        try{
        	
        	Class.forName("com.mysql.cj.jdbc.Driver");
			Connection cn= DriverManager.getConnection("jdbc:mysql://localhost/chat?user=root&password=123456789");
			String q1="select * from login where username=? and password=?";
			PreparedStatement st1= cn.prepareStatement(q1);
			st1.setString(1, username);
			st1.setString(2, password);
			ResultSet resultSet=st1.executeQuery();
			boolean ch=false;
			while(resultSet.next())
			{
				ch=true;
			}
			cn.close();
			return ch;
        	
        }
        catch(Exception ex){
            System.out.println("Database exception : userExists()");
            return false;
        }
    }
    
    public void addUser(String username, String password){
        
        try {
            
        	Class.forName("com.mysql.cj.jdbc.Driver");
			Connection cn= DriverManager.getConnection("jdbc:mysql://localhost/chat?user=root&password=123456789");
			String query= "insert into login values(?,?)";
			PreparedStatement st= cn.prepareStatement(query);
			st.setString(1, username);
			st.setString(2, password);
			st.executeUpdate();
			cn.close();
			//JOptionPane.showMessageDialog(null, "User ID Added");
        	
	   } 
           catch(Exception ex){
		System.out.println("Exception Modify Database");
	   }
	}
    
}
