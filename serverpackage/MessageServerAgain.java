package serverpackage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public final class MessageServerAgain extends Thread {
	  private ConnectionToClient ctc;
	    private final UserList fUserList;
	    private final List<Thread> fThreadList;
	    private ServerSocket fServerSocket;
	    public MessageServerAgain(UserList aUserList)    {
	    	this.fThreadList = new ArrayList<Thread>();
	    	this.fUserList = aUserList;
	    	fServerSocket = null;
			try {
			   fServerSocket = new ServerSocket();
			   fServerSocket.setReuseAddress(true);
			   fServerSocket.bind(new InetSocketAddress(8080));
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
	    }
	    
	    public final void  run() {
	    	try {
	    		       while(true) {		              
		    	    	  Socket messageSocket = fServerSocket.accept();
			              ctc = new ConnectionToClient(messageSocket, fUserList);
			              final Thread t = new Thread(ctc);
			              t.start();
			              fThreadList.add(t);
		    	       }  	    		       
		          } catch(IOException ioe) {
		        	  System.out.println("Server Stopped!");
		          } 	    	
	    }
	    
	    @Override
		public final void interrupt() {
		    try {
	            	fServerSocket.close();
		    } catch (IOException ignored) {
		    } finally {
		    	for(Thread t : fThreadList) {
 		    	   if(t.isAlive()) {
 		    		   t.interrupt();
 		    	   }
 		       }
					super.interrupt();
			}
		}
}