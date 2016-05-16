package serverpackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

public final class User  {
    private String fUserName;
    private String fPassWord;
    private boolean fIsOnline;
    private boolean fInChat;
    private boolean fChangePassword;
    private ConnectionToClient fCtc;
    private final List<String> fBuddyList;
    private final List<User> fRequestedForBuddyNotOnline; 
    private User fUserInChatWith;
    private Socket fDownloadFileSocket;
    private Socket fDownloadImageSocket;
    private String fAccountType;
    private String fFirstName;
    private String fLastName;
    private final List<UserLogModel> fLogModelList;    
    private String fEditDate;
    private String fLastEditBy;
    
	    public User()    {
	    	this.fUserName = "";
	    	this.fPassWord = "";
	    	this.fAccountType = "";
	    	this.fFirstName = "";
	    	this.fLastName = "";
	        this.fIsOnline = false;
	        this.fInChat = false;
	        this.fChangePassword = false;
	        this.fBuddyList = new ArrayList<String>();
	        fDownloadFileSocket = null;
	        fDownloadImageSocket = null;
	        fRequestedForBuddyNotOnline = new ArrayList<User>();
	        fEditDate = "";
	        fLogModelList = new ArrayList<UserLogModel>();
	    }
	    
	    public final void setUserName(String aName) {
	    	this.fUserName = aName;
	    }
	    
	    public final void setPass(String aPass) {
	    	this.fPassWord = aPass;
	    }
	    
	    public final void setAccountType(String aType) {
	    	this.fAccountType = aType;
	    }
	    
	    public final void setFirstName(String aFirstName) {
	    	this.fFirstName = aFirstName;
	    }
	    
	    public final void setLastName(String aLastName) {
	    	this.fLastName = aLastName;
	    }
	    
	    public final String getAccountType() {
	    	return this.fAccountType;
	    }
	    
	    public final String getFirstName() {
	    	return this.fFirstName;
	    }
	    
	    public final String getLastName() {
	    	return this.fLastName;
	    }
	    
	    public final void setLastEditDate(final String aDate) {
	    	this.fEditDate = aDate;
	    }
	    
	    public final String getLastEditedDate() {
	    	return this.fEditDate;
	    }
	
		    public final void store(DataOutputStream dos)     {
		    	try {
			        dos.writeUTF(fUserName);
			        dos.writeUTF(fPassWord);
			        dos.writeUTF(fLastName);
			        dos.writeUTF(fFirstName);
		        	dos.writeUTF(fEditDate.toString());	
			        final int buddyListSize = fBuddyList.size();
			        dos.writeInt(fBuddyList.size());
			        for(int index = 0; index < buddyListSize; index++){
			           dos.writeUTF(fBuddyList.get(index));
			        }
			        dos.writeInt(fLogModelList.size());
			        for(UserLogModel model : fLogModelList) {
			             dos.writeUTF(model.getDate());
			             dos.writeUTF(model.getActivity());
			             dos.writeUTF(model.getIP());
			        }
		    	} catch(IOException ioe) {
		    		JOptionPane.showMessageDialog(null,
		    				                     "Error Writing File.", 
		    				                     "Server Error!",
		    				                     JOptionPane.ERROR_MESSAGE);
		    		System.exit(1);
		    	}
		    }
		    
		    public final void processBuddyRequests() {
		    	final int size = fRequestedForBuddyNotOnline.size();
		    	for(int index = 0; index < size; index++) {
		    		final User user = fRequestedForBuddyNotOnline.get(index);
		    		if(index == 0) {
			    		sendMessage("REQUESTS_FOR_BUDDY");
			    	}	    		
			    	sendMessage(user.getUsername());
			    	if(index == (size - 1)) {
			    		sendMessage("END"); 	
			    	}
		    	}	    	
		    }
	 
		    public void processNotifyOnline(UserList aUserList) {
		    	final String status = (fIsOnline) ? "ONLINE " : "OFFLINE ";
			       Enumeration<String> namesKey = aUserList.keys();
			    	while(namesKey.hasMoreElements()) {
			    		final String name = namesKey.nextElement();
			    		final User user = aUserList.getUser(name);
			    		if((user.getUsername() != fUserName) && (user.isOnline())) {
			    			final List<String> buddyList = user.getBuddyList();
			    			for(String buddy: buddyList) {
			    				if(buddy.equals(fUserName)) {
			    					user.sendMessage(status + fUserName);
			    					break;
			    				}
			    			}
	                    }
			    	}
		    }    
		    
		    public final String getUsername() {
		    	return this.fUserName;
		    }
		    
		    public final void addRequestedforBuddy(User aUser) {
		    	fRequestedForBuddyNotOnline.add(aUser);
		    }    
		    
		    public final void addBuddy(String addBuddy)    {
		        fBuddyList.add(addBuddy);
		    }
		    
		    public final void clearBuddyList() {
		    	fBuddyList.clear();
		    }
		    
		    public final boolean isOnline() {
		    	return this.fIsOnline;
		    }
		    
		    public final void setOnline(boolean aOnline) {
		    	this.fIsOnline = aOnline;
		    }
		    
		    public final boolean inChat() {
		    	return this.fInChat;
		    }
		    
		    public final void setInChat(boolean aOnline) {
		    	this.fIsOnline = fInChat;
		    }
		    
		    public final void setChangePassword(boolean aChange) {
		    	this.fChangePassword = aChange;
		    }
		    
		    public final boolean changePassword() {
		    	return this.fChangePassword;
		    }
		    
		    public final void setInChatUser(User aUser) {
		    	this.fUserInChatWith = aUser;
		    }
		    
		    public final User getInChatUser() {
		    	return this.fUserInChatWith;
		    }
		    
		    public final List<String> getBuddyList() {
		    	return this.fBuddyList;
		    }
		    
		    public final void setDownloadFileSocket(Socket aSocket) {
		    	this.fDownloadFileSocket = aSocket;
		    }
		    
		    public final Socket getDownloadFileSocket() {
		    	return this.fDownloadFileSocket;
		    }
		    
		    public final void setDownloadImageSocket(Socket aSocket) {
		    	this.fDownloadImageSocket = aSocket;
		    }
		    
		    public final Socket getDownloadImageSocket() {
		    	return this.fDownloadImageSocket;
		    }
		    
		    public final void setConnectionClient(ConnectionToClient aCtc) {
		    	this.fCtc = aCtc;
		    }
		    
		    public final void sendMessage(String aMessage) {
		    	fCtc.sendMessage(aMessage);
		    }
		    
		    public final String getPassword() {
		    	return this.fPassWord;
		    }
		    
		    public final void addUserLog(String aDate, String aActivity, String aIP) {
		    	final UserLogModel model = new UserLogModel(aDate, aActivity, aIP);
		    	fLogModelList.add(model);
		    }
		    
		    public final String getLogDate(int aIndex) {
		    	UserLogModel model = fLogModelList.get(aIndex);
		        return model.getDate();
		    }
		    
		    public final String getLogActivity(int aIndex) {
		    	UserLogModel model = fLogModelList.get(aIndex);
		        return model.getActivity();
		    }
		    
		    public final String getLogIP(int aIndex) {
		    	UserLogModel model = fLogModelList.get(aIndex);
		        return model.getIP();
		    }
		    
		    public final int getModelSize() {
		    	return fLogModelList.size();
		    }
		    
		    public final void setLastEditBy(final String aName) {
		    	this.fLastEditBy = aName;
		    }
		    
		    public final String getLastEditBy() {
		    	return this.fLastEditBy;
		    }
		    
		    public final String getLastSignin() {
		    	String date = ""; 
		    	final List<String> tempModelList = new ArrayList<String>();
		    	for(UserLogModel model: fLogModelList) {
		    		final String activity = model.getActivity();
		    		if(activity.equals("Signed In")) {
		    			final String signInDate = model.getDate();
		    			tempModelList.add(signInDate);
		    		}
		    	}
		    	final int size = tempModelList.size();
		    	if(size > 1) {
			    	Collections.sort(tempModelList);
			    	date = tempModelList.get(size - 1);	
		    	} else {
		    		date = tempModelList.get(0);
		    	}
		    	return date;
		    }
		    
		    final class UserLogModel {
		    	private final String fDate;
		    	private final String fActivity;
		    	private final String fIP;
		    	public UserLogModel(final String aDate, 
		    			            final String aActivity,
		    			            final String aIP) {
		    		this.fDate = aDate;
		    		this.fActivity = aActivity;
		    		this.fIP = aIP;
		    	}
		    	
		    	public final String getDate() {
		    		return this.fDate;
		    	}
		    	
		    	public final String getActivity() {
		    		return this.fActivity;
		    	}
		    	
		    	public final String getIP() {
		    		return this.fIP;
		    	}		    	
		    }
}