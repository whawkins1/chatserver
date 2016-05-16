package serverpackage;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

public class SignInDialog implements ActionListener {
		private final JTextField fUsernameTextField;
		private final JPasswordField fPasswordField;
		private final JCheckBox fRememberPassCB;
		private final JDialog fDialog;
		private final JButton fSignInButton;
		private final JButton fCancelButton;
		private final UserList fUserList;
		private final UserList fAdminList;
		public SignInDialog (final UserList aUserList, final UserList aAdminList) {
			this.fAdminList = aAdminList;
			this.fUserList = aUserList;
			JPanel signinPanel = new JPanel();
		    signinPanel.setLayout(new GridBagLayout());
		    final GridBagConstraints c1 = setConstraints(0,0);
		    final JLabel usernameLabel = new JLabel();
		    usernameLabel.setText("Username: ");
		    signinPanel.add(usernameLabel, c1);
		    final GridBagConstraints c2 = setConstraints(1,0);
		    fUsernameTextField = new JTextField(15);
		    signinPanel.add(fUsernameTextField, c2);
		    GridBagConstraints c3 = setConstraints(0, 1);
		    final JLabel userPasswordLabel = new JLabel();
		    userPasswordLabel.setText("Password: ");
		    signinPanel.add(userPasswordLabel, c3);
		    GridBagConstraints c4 = setConstraints(1, 1);
		    fPasswordField = new JPasswordField(15);
		    signinPanel.add(fPasswordField, c4);
		    GridBagConstraints c5 = setConstraints(0, 2);
		    c5.anchor = GridBagConstraints.WEST;
		    c5.gridwidth = 2;
		    fRememberPassCB = new JCheckBox();
		    fRememberPassCB.setText("Remember Password");
		    signinPanel.add(fRememberPassCB, c5);
		    fSignInButton  = createButton("Sign In", "SIGN_IN", "Sign To Chat Server");
		    fCancelButton = createButton("Cancel", "CANCEL", "Cancel Sign In");
		    final JPanel buttonPanel = new JPanel();
		    buttonPanel.add(fSignInButton);
		    buttonPanel.add(fCancelButton);
		    fDialog = new JDialog();
		    fDialog.setModal(true);
		    final JRootPane rp = fDialog.getRootPane();
		    rp.setDefaultButton(fSignInButton);
		    fDialog.setLayout(new BorderLayout());
		    fDialog.setTitle("Sign In");
		    final JRootPane rootPane = fDialog.getRootPane();
		    rootPane.setDefaultButton(fSignInButton);
		    fDialog.add(signinPanel, BorderLayout.NORTH);	
		    fDialog.add(buttonPanel, BorderLayout.SOUTH);
		    fDialog.pack();
		    fDialog.setLocationRelativeTo(null);
		    fDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		    fDialog.setVisible(true);
	}
	
	public final void actionPerformed(ActionEvent ae) {
	     final String command = ae.getActionCommand();		
	     if(command.equals("SIGN_IN")) {
	       		 final String username = fUsernameTextField.getText().trim();   
	       		 char[] charPass = fPasswordField.getPassword();
	       		 String pass = String.valueOf(charPass);
	       		 if(!(username.equals("") && !(pass.equals("")))) {
	                 final User user = fAdminList.getUser(username);
	                 if(user == null) {
	                	 SignInDialog.showErrorMessage("Username or Password is Not valid, Please Try Again.", 
	                			                       "Sign In Failed!");
	                	 Arrays.fill(charPass, '0');
	                   	 pass.equals("");
	                   	 resetSignin();
	                 } else {
	                	 final String passAdmin = user.getPassword();
	                	 final String signinUsername = user.getUsername();
	                	 if(pass.equals(passAdmin)) {
	                		 Date editDate = new Date();
	 						final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yy HH:mm");
	 			            final String convEditDate = formatter.format(editDate);
	 						InetAddress ip = null;
							try {
								ip = InetAddress.getLocalHost();
							} catch (UnknownHostException e) {
								SignInDialog.showErrorMessage("Could Not Find Host IP.", 
     			                       "Signin Error");
							}
							final String hostip = (ip == null) ? "Undefined" : ip.getHostAddress().trim();
		                	 user.addUserLog(convEditDate, "Signed In", hostip);
	 						JOptionPane.showMessageDialog(null, 
	                	    		                       "You Are Signed In as " + signinUsername + ".",
	                	    		                       "Success!",
	                	    		                       JOptionPane.INFORMATION_MESSAGE);	
	                		 fDialog.dispose();
	                		 final ServerFrame f = new ServerFrame(fUserList, fAdminList);
	                		 f.setCurAdmin(signinUsername);
	                	 }
	                 }
	       		 } else {
	       			SignInDialog.showErrorMessage("All Fields Must Be Complete to Process Sign In.",
	       					                      "Incomplete Sign In!");
	       			Arrays.fill(charPass, '0');
	       	   	    pass.equals("");
	       	   	    resetSignin();
	       		 }
	     } else if(command.equals("CANCEL")) {
   	    	    System.exit(0);
	     }   	
     }
	
	private final void resetSignin() {
		fUsernameTextField.selectAll();
   	    fUsernameTextField.requestFocus();
   	    fPasswordField.setText("");
	}
	
		public final JButton createButton(String label, String aAc, String toolTipText)    {
	            JButton b = new JButton(label);
	            b.setActionCommand(aAc);
	            b.addActionListener(this);
	            b.setToolTipText(toolTipText);
	           return b;
	    }
        
        public final static GridBagConstraints setConstraints(int x, int y) {
        	GridBagConstraints c = new GridBagConstraints();
        	c.anchor = GridBagConstraints.EAST;
        	c.gridx = x;
        	c.gridy = y;
        	c.insets = new Insets(2, 0 , 2, 0);
        	return c;
        }
        
        public final static void showErrorMessage(final String aMessage, final String aTitle) {
        	JOptionPane.showMessageDialog(null, 
                                         aMessage, 
                                         aTitle,
                                         JOptionPane.ERROR_MESSAGE);
        }
        
        public final static File openChooserFile(final String aExtension, 
        		                                 final Preferences aPrefs, 
        		                                 final String aPath) {
        	File fileToSave = null;
        	JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
        	final String type = aExtension.equals("user.dat") ? "User" : "Admin";
	  		  fileChooser.setDialogTitle("Choose " + type + " Directory");
	  		  fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	  		  fileChooser.setApproveButtonText("OK");
	  		  final int selection = fileChooser.showOpenDialog(null);	  		
	  		if(selection == JFileChooser.APPROVE_OPTION) {
	  	    		final File file = fileChooser.getSelectedFile();
	      			String filePathUsers = file.getAbsolutePath();
	      			filePathUsers = (filePathUsers + "\\" + aExtension);
	      			aPrefs.put(aPath, filePathUsers);
	      	    	fileToSave = new File(filePathUsers);
	      	    	try {
	      	    	    fileToSave.createNewFile();
	      	    	} catch (IOException ioe){
	      	    		  JOptionPane.showMessageDialog(null,
	      	    				                        "Error Creating File " + fileToSave.getName(),        
	      	    				                        "Server Error!",
	       	                                            JOptionPane.ERROR_MESSAGE);
	       	    	     System.exit(1);     
	      	    	}
	  		}else{
	  			System.exit(0);
	  		}
	  		return fileToSave;
        }
        
        public final static File getFile(final String aPath,
        		                         final String aExt, 
        		                         final Preferences aPrefs,
        		                         final String aPathVariable) {
        	
        	final File fileToSave = aPath.equals("") ? SignInDialog.openChooserFile(aExt, aPrefs, aPathVariable)
		        		                        : new File(aPath)    ;
            return fileToSave; 
        }
        
        public static void main(String[] args)      {
        	try	    {
			      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			    }	catch(UnsupportedLookAndFeelException ulfe)	 {
			    	ulfe.printStackTrace();
			    	System.out.println("Unsupported Look And Feel");		    	
			    }	catch(IllegalAccessException iae)	{
			    	iae.printStackTrace();
			    	System.out.println("Unsupported Look And Feel");
			    }	catch(InstantiationException ie)	{
			    	ie.printStackTrace();
			    	System.out.println("Unsupported Look And Feel");
			    }   catch(ClassNotFoundException cnfe)	{
			    	cnfe.printStackTrace();
			    	System.out.println("Unsupported Look And Feel");
			    }        	
        	Preferences prefs = Preferences.userNodeForPackage(ServerFrame.class);
	    	String USER_PATH_NAME = "user_path_name";
	    	String ADMIN_PATH_NAME = "admin_path_name";
	    	String filePathUsers = prefs.get(USER_PATH_NAME, "");
	    	String filePathAdmin = prefs.get(ADMIN_PATH_NAME, "");
	    	File fileToSaveUsers = null;
	    	File fileToSaveAdmin = null;
	    	fileToSaveUsers = getFile(filePathUsers, "users.dat", prefs, USER_PATH_NAME);
	    	fileToSaveAdmin = getFile(filePathAdmin, "admin.dat", prefs, ADMIN_PATH_NAME);
	  		UserList userList = new UserList(fileToSaveUsers);
	  		UserList adminList = new UserList(fileToSaveAdmin);
			userList.read();
			adminList.read();
		
        	final Runnable thread = (new Runnable()			{
    			public void run() {
    				if(adminList.size() == 0)  {
    					CreateAccountDialog d = new CreateAccountDialog("Admin", true, null);
    					d.setAdminMap(adminList);
    					d.setUserMap(userList);
    				} else {
    					new SignInDialog(userList, adminList);	
    				}    				    				
    			}				
    		});		
    		SwingUtilities.invokeLater(thread);
       }
}
