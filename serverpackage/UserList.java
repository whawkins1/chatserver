package serverpackage;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;

public final class UserList extends ConcurrentHashMap<String, User>  {
    private static final long serialVersionUID = 1L;
	User putUser;
    File fFileUsers;
	
	public UserList(File aFileUsers) {
		this.fFileUsers = aFileUsers;
	}
	
	    public final boolean isAUser(String checkUser) {
	        return containsKey(checkUser);
	    }	   
	
	    public final boolean checkPassword(String checkPass)  {
	         return containsValue(checkPass);
	    }
	
	    public final User getUser(String findUser) {
	         return get(findUser);
	    }
	
	   
	    public final void addUser(String userNameKey, User addThisUser)  {
	        put(userNameKey, addThisUser);
	    }
	   
	    public final void store()     {
	        try {
				final FileOutputStream fos = new FileOutputStream(fFileUsers);
			    DataOutputStream dos = new DataOutputStream(fos);
			    final int numberUsers = size();
	            dos.writeInt(numberUsers);	
	            for(Map.Entry<String, User> entry : entrySet()) {
	            	final User user = (User)entry.getValue();
	            	user.store(dos);
	            }
			} catch (IOException ioe) {
			     createErrorMessage("Error Writing File " + fFileUsers.getName());
			     System.exit(1);
			}
	    }
	
	    public final void read()  {
	    	try {
	    	  final FileInputStream fis = new FileInputStream(fFileUsers);
	    	  final BufferedInputStream bis = new BufferedInputStream(fis);
	          final DataInputStream dis = new DataInputStream(bis);
	          if(fFileUsers.length() != 0) {
		          final int numberUsers = dis.readInt();
		          for(int index = 0; index < numberUsers; index++) {
		              final String userName = dis.readUTF();
		              final String password = dis.readUTF();
		              final String lastName = dis.readUTF();
		              final String firstName = dis.readUTF();
		              final String editDate = dis.readUTF();
		              final User user = new User(); 
		              user.setUserName(userName);
		              user.setPass(password);
		              user.setLastName(lastName);
		              user.setFirstName(firstName);
		                user.setLastEditDate(editDate);
		              final int numberBuddies = dis.readInt();		             
		              for(int index2= 0; index2 < numberBuddies; index2++) {
		                  user.addBuddy(dis.readUTF());
		              }   
		              final int numberLogs = dis.readInt();
		              for(int index3 = 0; index3 < numberLogs; index3++) {
					        final String date = dis.readUTF();
					        final String activity = dis.readUTF();
					        final String ip = dis.readUTF();
					        user.addUserLog(date, activity, ip);
		              }
		              put(userName, user);
		          }
	          }   
	          dis.close();
	    	} catch(IOException ioe) {
	    		createErrorMessage("Error Reading File " + fFileUsers.getName());
	    		System.exit(1);
	    	}
	    }
	    
	    private final void createErrorMessage(String aMessage) {
	    	     JOptionPane.showMessageDialog(null,
	                                           aMessage, 
	                                           "Server Error!",
	                                           JOptionPane.ERROR_MESSAGE);
	    }
}

