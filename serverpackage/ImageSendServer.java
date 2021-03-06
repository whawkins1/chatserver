package serverpackage;

	import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

	public final class ImageSendServer extends Thread {
		 private final UserList fUserList;
		 private final List<Thread>  fThreadList;
		 private ServerSocket fServerSocket;
		 public ImageSendServer(UserList aUserList) {
			 this.fUserList = aUserList;
			 this.fThreadList = new ArrayList<Thread>();
			 fServerSocket = null;
				try {
				   fServerSocket = new ServerSocket();
				   fServerSocket.setReuseAddress(true);
				   fServerSocket.bind(new InetSocketAddress(8084));
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
	     }     
		 
		 public final void run() {
			 System.out.println("Send Image Server Starting.....");
	    	 	 try {
	    			 while(!(Thread.currentThread().isInterrupted())) {
		    			 final Socket sendSocket = fServerSocket.accept();
		    			 final InputStream is = sendSocket.getInputStream();
		    			 final DataInputStream dis = new DataInputStream(is);
		    			 final String name = dis.readUTF();
		    			 final User user = fUserList.getUser(name);
		    			 final Socket downloadSocket = user.getDownloadImageSocket();
		    			 final ConnectionToClientSend cTCS = new ConnectionToClientSend(sendSocket, downloadSocket);
		    			 final Thread t = new Thread(cTCS);
		    			 t.start();
		    			 fThreadList.add(t);
	    			 }	    			 
	    		 } catch(IOException ioe) {
	    		 }
		 }
		 
		 @Override
			public final void interrupt() {
			    try {
		            	fServerSocket.close();
			    } catch (IOException ignored) {			    	
			    } finally {
						super.interrupt();
						for(Thread t : fThreadList) {
		    				 if(t.isAlive()) {
		    					 t.interrupt();	 
		    				 }	    				 
		    			 }
				}
			}
	}