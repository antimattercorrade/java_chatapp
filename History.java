
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class History {
    
    public void addData(String username,String msg){
        
        try{
            
        	Class.forName("com.mysql.cj.jdbc.Driver");
			Connection cn= DriverManager.getConnection("jdbc:mysql://localhost/chat?user=root&password=123456789");
			Statement st=cn.createStatement();
			st.executeUpdate("INSERT INTO "+username+" VALUES('"+msg+"')");
			cn.close();
			
        	}
        catch(Exception ex){
            System.out.println("Error: "+ex.getMessage());
            }
    }
    
    public String readData(String username)
    {
    	String area="";
    	try{
            
        	Class.forName("com.mysql.cj.jdbc.Driver");
			Connection cn= DriverManager.getConnection("jdbc:mysql://localhost/chat?user=root&password=123456789");
			String q2="select count(*) from "+username;
			PreparedStatement st2= cn.prepareStatement(q2);
			ResultSet rs1=st2.executeQuery();
			rs1.next();
			if(rs1.getString(1).contentEquals("0"))
			{
				return "No";
			}
			String q1="select * from "+username;
			PreparedStatement st1= cn.prepareStatement(q1);
			ResultSet rs=st1.executeQuery();
			int ct=0;
			while(rs.next())
			{
				if(ct==0)
				{
					area=area+rs.getString(1);
					ct=1;
				}
				else
				{
					area=area+System.lineSeparator()+rs.getString(1);
				}
			}
			cn.close();
			
			
        	}
        catch(Exception ex){
            System.out.println("Error: "+ex.getMessage());
            }
    	return area;
    }
    
    public void deleteChat(String username)
    {

    	try{
            
        	Class.forName("com.mysql.cj.jdbc.Driver");
			Connection cn= DriverManager.getConnection("jdbc:mysql://localhost/chat?user=root&password=123456789");
			Statement st=cn.createStatement();
			st.executeUpdate("DELETE FROM "+username);
			cn.close();
			
        	}
        catch(Exception ex){
            System.out.println("Error: "+ex.getMessage());
            }
    }
    
    public void addTable(String username){
        
        try {
            
        	Class.forName("com.mysql.cj.jdbc.Driver");
			Connection cn= DriverManager.getConnection("jdbc:mysql://localhost/chat?user=root&password=123456789");
			Statement st=cn.createStatement();
			st.executeUpdate("CREATE TABLE "+username+" "+"(history VARCHAR(255))");
			cn.close();
			//JOptionPane.showMessageDialog(null, "User ID Added");
        	
	   } 
           catch(Exception ex){
		System.out.println("Exception Modify Database");
	   }
	}
    
}
