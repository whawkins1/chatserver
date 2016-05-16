package serverpackage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public final class DownloadServer extends  Thread {
	private final UserList fUserList;
    private ServerSocket fServerSocket;
	public DownloadServer(UserList aUserList) {
		this.fUserList = aUserList;
		fServerSocket = null;
		try {
		   fServerSocket = new ServerSocket();
		   fServerSocket.setReuseAddress(true);
		   fServerSocket.bind(new InetSocketAddress(8081));
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	@Override
	public final void run() {
		System.out.println("Download Server Started.....");
			try {
				while(true) {
					final Socket DownloadSocket = fServerSocket.accept();
					final InputStream is = DownloadSocket.getInputStream();
					final DataInputStream dis = new DataInputStream(is);
					final String name = dis.readUTF();
					final User user = fUserList.getUser(name);
					user.setDownloadFileSocket(DownloadSocket);
				}
			}catch(IOException ioe) {
			} 
	}
	
	@Override
	public final void interrupt() {
	    try {
            	fServerSocket.close();
	    } catch (IOException ignored) {	    	
	    } finally {
				super.interrupt();
			
		}
	}
}