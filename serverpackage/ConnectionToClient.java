package serverpackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.net.Socket;

import javax.swing.JOptionPane;


public final class ConnectionToClient 
                         implements Runnable {
    private BufferedReader br;
    private BufferedWriter bw;
    
    private String replyClient;
    private String userNameClient;
    private String userPasswordClient;
    private final Socket socket;
    private User userOnline;
    byte[] buffer;
    //private User fUserInChatWith;
    private final UserList fUserList;
    
    public ConnectionToClient(Socket socket, UserList aUserList)     {
        this.socket = socket;
        this.fUserList = aUserList;
        this.userNameClient = "";
        this.buffer = new byte[8192];
    }
    
    @Override
    public final void run()    {
    	try {
    		final InputStream is = socket.getInputStream();
    		final OutputStream os = socket.getOutputStream();
    		bw = new BufferedWriter(new OutputStreamWriter(os));
    		br = new BufferedReader(new InputStreamReader(is));
    		socket.setSoTimeout(0);	        
	        while(!(Thread.currentThread().isInterrupted())) {
	           String ipClient = "";	
	           User user = null;
	           String name = "";
	           String message = "";
	           String formattedDate = "";
	           replyClient = readMessage();
	        if(replyClient.startsWith("SIGN_OFF")) { 
	        	replyClient = readMessage();
	        	if(replyClient.startsWith("BUDDIES")) {
	        		userOnline.clearBuddyList();
	        		while(!((name = readMessage()).startsWith("END"))) {
	        			userOnline.addBuddy(name);
	        		}	        		
	        	}
	        	if(!userNameClient.equals(""))	{
	        	   userOnline.setOnline(false);
	        	   userOnline.setInChat(false);
      		       userOnline.processNotifyOnline(fUserList);
	            }
	        	fUserList.store();
	        	Date curDate = new Date();
	        	final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh:mm");
		    	formattedDate = formatter.format(curDate);
	        	ipClient = socket.getRemoteSocketAddress().toString();
	        	userOnline.addUserLog(formattedDate, "Sign Off", ipClient);
	        	Thread.currentThread().interrupt();
	        } else if(replyClient.startsWith("SIGN_IN")){
	           	   replyClient = replyClient.substring(7).trim();
	                  parseUsername();
	                  parsePassword();
		                  if(validUsername(userNameClient) && validPass()) {
		                	    userOnline = fUserList.getUser(userNameClient);
		                	    if(userOnline.changePassword()) {
		                	    	sendMessage("CHANGE_PASSWORD");		                	    	   
		                	       final String passStatus = readMessage();
		                	       if(passStatus.startsWith("CHANGED")) {
		                	    	   final int wsIndex = passStatus.indexOf(" ");
		                	    	   final String pass = passStatus.substring(0, wsIndex).trim();
		                	    	   userOnline.setPass(pass);
		                	       } else if(passStatus.startsWith("UNCHANGED")) {
		                	    	   Thread.currentThread().interrupt();
		                	       }
		                	    }
			                	    ipClient = socket.getRemoteSocketAddress().toString();
			                	    Date curDate = new Date();
			        	        	final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh:mm");
			        		    	formattedDate = formatter.format(curDate);
			                	    userOnline.addUserLog(formattedDate, "Signed In", ipClient);
			                	    if(!(userOnline.isOnline())) {
				                	    userOnline.setOnline(true);
				                        userOnline.setConnectionClient(this);
				                        final List<String> buddyList = userOnline.getBuddyList();
				                        sendMessage("BUDDIES");
				                        for(String username : buddyList) {
				                            user = fUserList.getUser(username);
				                            String status = (user.isOnline()) ? "ONLINE " : "OFFLINE ";
				                            sendMessage(status + username);	
				                        }
				                        sendMessage("END");
				                        userOnline.processBuddyRequests();
				                        userOnline.processNotifyOnline(fUserList);
				                        sendMessage("OK_SIGNED_IN");
					      	        	
			                	    } else {
			                        	sendMessage("FAIL_ALREADY_SIGNED_IN");
			                        }
		                  } else {
		                      sendMessage("FAIL_SIGN_IN");
		                  }
	        } else if(replyClient.startsWith("INVITE_ACCEPTED")) {
         	             name = replyClient.substring(15).trim();
         	             user = fUserList.getUser(name);
         	             user.setInChat(true);
         	             userOnline.setInChatUser(user);
         	             userOnline.setInChat(true);
         	             user.setInChatUser(userOnline);
         	             user.setInChat(true);
                         user.sendMessage("START_CHAT " + userNameClient);
	        } else if (replyClient.startsWith("INVITE")) {
	            	  name = replyClient.substring(6).trim();
	            	  user = fUserList.get(name);
	            		  if(user.inChat()) {
	            			  sendMessage("IN_CHAT " + name);
	            		  } else {
	            			  user.sendMessage("INVITE_REQUEST " + userNameClient );
	            		  }
	        
	           	   replyClient = replyClient.substring(14).trim();
	                  parseUsername();
	                  if(!validUsername(userNameClient)) {
	                	   parsePassword();
	                	   user = new User();
	                	   user.setUserName(userNameClient);
	                	   user.setPass(userPasswordClient);
	                       user.setOnline(true);
	                	   fUserList.addUser(userNameClient, user);
	                       fUserList.store();
	                       sendMessage("OK_ACCOUNT_CREATED");                       
	                   }
	        } else if (replyClient.startsWith("REQUEST_BUDDY")) {
	            	  final String  buddyRequestedClient = replyClient.substring(13).trim();
	           	      if(validUsername(buddyRequestedClient))  {
	                      final User requestedUser = fUserList.getUser(buddyRequestedClient);	                        
	                      if(requestedUser.isOnline()) {
	                        	 requestedUser.sendMessage("USER_REQUESTING_BUDDY " + userNameClient);
	                         }  else {
	                        	 final User requestingUser = fUserList.getUser(userNameClient);
	                        	 requestedUser.addRequestedforBuddy(requestingUser);
	                         }
	                 } else {
	                     sendMessage("FAIL_NOT_A_USER " + buddyRequestedClient);
	                 }
	        } else if (replyClient.startsWith("MESSAGE")) {
	                  replyClient = replyClient.substring(7).trim();
	                  final int indexWS = replyClient.indexOf(" ");
	                  user = userOnline.getInChatUser();
	                  message = replyClient.substring(indexWS).trim();
	                  user.sendMessage("MESSAGE " + message);
	        } else if (replyClient.startsWith("REMOVE_BUDDY")) {
	        		      final String buddyName = replyClient.substring(11).trim();
	                      user = (User)fUserList.getUser(userNameClient);
	                      final List<String> buddyList = user.getBuddyList();
	                      buddyList.remove(buddyName);	               
	        } else if (replyClient.startsWith("ACCEPTED_BUDDY")) {	
	            	    processBuddyRequest("ACCEPTED ");
	        } else if (replyClient.startsWith("DECLINED_BUDDY")) {
	            	   processBuddyRequest("DECLINED ");	              
	        } else if (replyClient.startsWith("NOTIFY_DOWNLOAD")) {
	            	   user = userOnline.getInChatUser();
	            	   user.sendMessage(replyClient);
	        } else if (replyClient.startsWith("SEND_IMAGE")) {
	            	   name = replyClient.substring(10).trim();
	            	   user = fUserList.getUser(name);
	            	   user.sendMessage("CREATE_STREAM_IMAGE");
	        } else if (replyClient.startsWith("DOWNLOAD_FILE_LIST")) {
	            	   user = userOnline.getInChatUser();	            	   
	            	   user.sendMessage("DOWNLOAD_FILE_LIST");
	            	   String fileName = "";
	            	   while(!((fileName = readMessage()).startsWith("END_FILE_NAME_LIST"))) {
	            		   user.sendMessage(fileName);
	            	   }
	            	   user.sendMessage("END_FILE_NAME_LIST");
	        } else if (replyClient.startsWith("VERIFY_DOWNLOAD_PERMISSION")) {
	            	   name = replyClient.substring(26).trim();
	            	   message = (fUserList.isAUser(name) ? "PERMISSION_DOWNLOAD_SUCCESS" : 
	            		                                                 "PERMISSION_DOWNLOAD_FAIL");
	            	   sendMessage(message);
	        } else if (replyClient.startsWith("VERIFY_SENDING_PERMISSION")) {
	            	   name = replyClient.substring(25).trim();
	            	   message = (fUserList.isAUser(name) ? "PERMISSION_SENDING_SUCCESS" : 
	            			                                             "PERMISSION_SENDING_FAIL");
	            	   sendMessage(message);	    
	        } else if (replyClient.startsWith("VERIFY_SENDING_IMAGE_PERMISSION")) {
	            	   name = replyClient.substring(31).trim();
	            	   message = (fUserList.isAUser(name) ? "PERMISSION_SENDING_IMAGE_SUCCESS" :
	            		                                                 "PERMISSION_SENDING_IMAGE_FAIL");
	            	   sendMessage(message);	                 
	        } else if (replyClient.startsWith("SENDING_FILE_STATS")) {
                       final String fileSpecs = replyClient;
	            	   replyClient = replyClient.substring(9).trim();
	            	   user = userOnline.getInChatUser();
	            	   user.sendMessage(fileSpecs);
	        } else if (replyClient.startsWith("END_FILE_SENT")) {
	            	   user = userOnline.getInChatUser();
	            	   user.sendMessage(replyClient);
	        } else if (replyClient.startsWith("CHATTING_WITH")) {
	            	   name = replyClient.substring(13).trim();
	            	   user = fUserList.getUser(name);
	            	   userOnline.setInChatUser(user);
	        } else if (replyClient.startsWith("IMAGE_SEND_EXECUTE")) {
	            	   name = replyClient.substring(18).trim();
	            	   user = fUserList.getUser(name);
	            	   user.sendMessage("IMAGE_SEND_EXECUTE");
	        } else if (replyClient.startsWith("IMAGE_LIST")) {
	            	   name = replyClient.substring(10).trim();
	            	   user = fUserList.getUser(name);
	            	   user.sendMessage("IMAGE_LIST");
	            	   String imageName = "";
	            	   while(!((imageName = readMessage()).startsWith("END_IMAGE_LIST"))) {
	            		   user.sendMessage(imageName);
	            	   }
	            	   user.sendMessage("END_IMAGE_LIST");

	               }
	        }
    	} catch(IOException ioe) {
    		createErrorMessage("Network I/O Error!");
	    } finally {
	    	try {
	    	   socket.close();
	    	} catch(IOException ioe) {
	    		createErrorMessage("Error Closing Socket!");
	    	}
	    }
    }
    
    private final void processBuddyRequest(String command){
    	final String requestingName = replyClient.substring(14).trim();
	    final User requestingUser = fUserList.getUser(requestingName);
	    if(requestingUser.isOnline()) {
	    	requestingUser.sendMessage(command + userNameClient);	
	    } else {
	    	requestingUser.addBuddy(userNameClient);
	    }	    
    }
    
        public final void sendMessage(String mes) {
              try {
            	bw.write(mes + '\n');
				bw.flush();
			} catch (IOException ioe) {
				ioe.printStackTrace();
				createErrorMessage("Error Sending Client Message!");
			}
              System.out.println("Server Send>>" + mes + "<<");
        }
   
        private final String readMessage()  {
               String readString = "";
			try {
				   readString = br.readLine();
			} catch (IOException ioe) {
				createErrorMessage("Error Reading Client Message!");
			}
              System.out.println("Server Rece >>" + readString + "<<");
              return readString;
        }
	
	    private final boolean validUsername(String checkUserName)  {
	        return fUserList.isAUser(checkUserName); 
	    }
	        
	    private final boolean validPass() {
	        final User user = (User)fUserList.get(userNameClient);
	        final String userValidPass = user.getPassword();
	        return (userValidPass.equals(userPasswordClient)); 	                
	    }
	        
	   private final void parseUsername()  {
		   replyClient = replyClient.substring(8).trim();
		   final int indexSpace = replyClient.indexOf(" ");
		   userNameClient = replyClient.substring(0, indexSpace).trim();
		   replyClient = replyClient.substring(indexSpace).trim();
	   }
	
	   private final void parsePassword()   {
		      userPasswordClient = replyClient.substring(8).trim();
	   }
	  
	  private final void createErrorMessage(String aMessage) {
 	     JOptionPane.showMessageDialog(null,
                                        aMessage, 
                                        "Server Error!",
                                        JOptionPane.ERROR_MESSAGE);
 }
	  
}