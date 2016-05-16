package serverpackage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public final class ImageDownloadServer extends Thread{
	private final UserList fUserList;
	private ServerSocket fServerSocket;
	public ImageDownloadServer(UserList aUserList) {
		this.fUserList = aUserList;
		fServerSocket = null;
		try {
		   fServerSocket = new ServerSocket();
		   fServerSocket.setReuseAddress(true);
		   fServerSocket.bind(new InetSocketAddress(8083));
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public final void run() {
		System.out.println("Image Download Server Started.....");
		try {
			while(true) {
				final Socket DownloadSocket = fServerSocket.accept();
				final InputStream is = DownloadSocket.getInputStream();
				final DataInputStream dis = new DataInputStream(is);
				final String name = dis.readUTF();
				final User user = fUserList.getUser(name);
				user.setDownloadImageSocket(DownloadSocket);
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