package serverpackage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public final class ConnectionToClientSend 
                                        implements Runnable {
     private final Socket fSendSocket;
     private final Socket fDownloadSocket;
     private BufferedInputStream fBis;
     private BufferedOutputStream fBos;

     public ConnectionToClientSend(Socket aSendSocket, Socket aDownloadSocket) {
		 this.fSendSocket = aSendSocket;
		 this.fDownloadSocket = aDownloadSocket;
		 try {
		     final InputStream is = fSendSocket.getInputStream();
		     final OutputStream os = fDownloadSocket.getOutputStream();
		     final DataInputStream dis = new DataInputStream(is);
		     final DataOutputStream dos = new DataOutputStream(os);
		     fBis = new BufferedInputStream(dis);
		     fBos = new BufferedOutputStream(dos);
		 } catch(IOException ioe) {
              ioe.printStackTrace(); 
		 }		 	     
     }
	 
	 public void run() {
		 try {
			 byte[] buffer = new byte[8192];
			 int read = 0;
			 while((read = fBis.read(buffer)) != -1) {
				 fBos.write(buffer, 0, read);
			 }			 			 
		 } catch(IOException ioe) {
			 ioe.printStackTrace();
		 } finally {
			 try {
				 fBos.flush();
				 fSendSocket.close();
			 } catch(IOException ioe) {
				 ioe.printStackTrace();
			 }
		 }
	}
}