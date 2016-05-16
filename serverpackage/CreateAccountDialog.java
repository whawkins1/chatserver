package serverpackage;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class CreateAccountDialog implements ActionListener{
	final int TEXT_FIELD_SIZE = 15;
	   private String fUserName;
	   private String fFirstName;
	   private String fLastName;
	   private String fCurAdminName;
	   private char[] fUserPass;
	   private char[] fConfirmPass;
	   private final JDialog fDialog;
	   private final JButton fSaveButton;
	   private final JButton fCancelButton;
	   private final JTextField fFirstNameField;
	   private final JTextField fLastNameField;
	   private final JTextField fUserNameField;
	   private final JPasswordField fPassField;
	   private final JPasswordField fConfirmPassField;
	   private final String fType;
	   private UserList fUserMap;
	   private UserList fAdminMap;
	   private final List<JTextField> fTFList;
	   private final boolean fStartup;
	   private ServerFrame fServerFrame;
	   
		public CreateAccountDialog(final String aType,
				                   final boolean aStartup,
				                   final JFrame aParent) {
			this.fType = aType;
			this.fStartup = aStartup;
			fTFList = new ArrayList<JTextField>();
			fFirstNameField  = createTextField();
			fFirstNameField.requestFocus();
		    fLastNameField = createTextField();
		    fUserNameField = createTextField();
		    fPassField = new JPasswordField(TEXT_FIELD_SIZE);
		    fConfirmPassField = new JPasswordField(TEXT_FIELD_SIZE);		    
			final JLabel firstNameLabel = new JLabel("First:");
		    final JLabel lastNameLabel = new JLabel("Last:");
		    final JLabel userNameLabel = new JLabel("Username:");
		    final JLabel passwordLabel = new JLabel("Password:");
		    final JLabel confirmPasswordLabel = new JLabel("Confirm:");
		    final JPanel accountPanel = new JPanel();
		    accountPanel.setLayout(new GridBagLayout());
		    final GridBagConstraints c1 = setConstraints(0,0);
		    accountPanel.add(firstNameLabel, c1);
		    final GridBagConstraints c2 = setConstraints(1,0);
		    accountPanel.add(fFirstNameField, c2);
		    final GridBagConstraints c3 = setConstraints(0,1);
		    accountPanel.add(lastNameLabel, c3);
		    final GridBagConstraints c4 = setConstraints(1,1);
		    accountPanel.add(fLastNameField, c4);
		    final GridBagConstraints c5 = setConstraints(0,2);
		    accountPanel.add(userNameLabel, c5);
		    final GridBagConstraints c6 = setConstraints(1,2);
		    accountPanel.add(fUserNameField, c6);
            final GridBagConstraints c7 = setConstraints(0,3);
		    accountPanel.add(passwordLabel, c7);
		    final GridBagConstraints c8 = setConstraints(1,3);
		    accountPanel.add(fPassField, c8);
		    final GridBagConstraints c9 = setConstraints(0,4);
		    accountPanel.add(confirmPasswordLabel, c9);
		    final GridBagConstraints c10 = setConstraints(1, 4);
		    accountPanel.add(fConfirmPassField, c10);
		    fSaveButton = createButton("Save", "SAVE", "Save" + " Profile");
		    fCancelButton = createButton("Cancel", "CANCEL", "Cancel Information Dialog");
		    final JPanel buttonPanel = new JPanel();
		    buttonPanel.add(fSaveButton);
		    buttonPanel.add(fCancelButton);
		    fDialog = new JDialog();
		    fDialog.setTitle("Create Account");
		    fDialog.setModal(false);
	   		fDialog.setLayout(new BorderLayout());
			fDialog.add(accountPanel, BorderLayout.CENTER);
			fDialog.add(buttonPanel, BorderLayout.SOUTH);
			fDialog.pack();
			fDialog.setLocationRelativeTo(aParent);
			fDialog.setVisible(true);
	   }
		
		public final void actionPerformed(ActionEvent ae) {
			final String command = ae.getActionCommand();
		    switch(command) {	
		    case "SAVE":
				fFirstName = fFirstNameField.getText().trim();
				fLastName = fLastNameField.getText().trim();
				fUserName = fUserNameField.getText().trim();
				fUserPass = fPassField.getPassword();
				fConfirmPass = fConfirmPassField.getPassword();						
				if(validUserInfo()) {
					User user = new User();
					user.setFirstName(fFirstName);
					user.setLastName(fLastName);
					user.setUserName(fUserName);
					final String validPass = String.valueOf(fUserPass);
					user.setPass(validPass);	
					try {
						user.setLastEditBy(fCurAdminName);
						Date editDate = new Date();
						final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yy HH:mm");
			            final String convEditDate = formatter.format(editDate);
						final InetAddress ip = InetAddress.getLocalHost();
						final String hostip = ip.getHostAddress().trim();
						user.setLastEditDate(convEditDate);
						user.addUserLog(convEditDate, "Signed In", hostip);
						   if(fType.equals("Admin")) {
							   fAdminMap.addUser(fUserName, user); 
						   } else if(fType.equals("User")) {
							   fUserMap.addUser(fUserName, user);
						    }
						   if(fStartup) {
							   final ServerFrame f = new ServerFrame(fUserMap, fAdminMap);
							   f.setCurAdmin(fUserName);
						   } else {
							   fServerFrame.setRadButtonSelected(fType);
						   }						   
						   fDialog.dispose();
					} catch (UnknownHostException uhe) {
						uhe.printStackTrace();
					}
				} 		
				break;
		    case "CANCEL":
				    final int answer = JOptionPane.showConfirmDialog(fDialog, 
				    		                                         "Account Info Not Complete Close Anyway?",
				    		                                         "Closing", JOptionPane.YES_NO_OPTION);
				    if(answer == JOptionPane.YES_OPTION) {
				    	if(fStartup) {
				    		System.exit(0);
				    	} else {
				    		fDialog.dispose();
				    	}				    
				    }
			break;	    
			}
		}
		
		private final boolean validUserInfo() {
				boolean validEntries = true;
					for(JTextField tf : fTFList) {
						final String contentsTF = tf.getText().trim();
						if(contentsTF.equals("")) {
							validEntries = false;
							break;
						}
					}				
				if(validEntries){
					    for(String key : fUserMap.keySet()) {
					      if(key.equals(fUserName)) {
					    	  SignInDialog.showErrorMessage(fUserName + " is Already in Use, Please Try Again.", 
					    			                        "Account Error!");
					    	  fUserNameField.setText("");
					    	  fUserNameField.requestFocus();
					    	  return false;
					      }
						}
				} else {
					SignInDialog.showErrorMessage("All Account Info Must Be Complete, "
							                      + "Please Try Again.", "Account Error!");
					return false;
				} 
				
				if(!(Arrays.equals(fUserPass, fConfirmPass))) {
					SignInDialog.showErrorMessage("Passwords Do not Match, Please Try Again!", 
							                      "Account Error!");
					Arrays.fill(fUserPass, '0');
					Arrays.fill(fConfirmPass, '0');
					fConfirmPassField.setText("");					
					fPassField.setText("");
					fPassField.requestFocus();
					return false;
				}
			return true;
		}	
		
		public final void setUserMap(final UserList aUserMap) {
			this.fUserMap = aUserMap;
		}
		
		public final void setAdminMap(final UserList aAdminMap) {
			this.fAdminMap = aAdminMap;
		}
		
		public final void setCurAdminName(final String aName) {
			this.fCurAdminName = aName;
		}
		
		public final void setServerFrame(final ServerFrame aServerFrame) {
			this.fServerFrame = aServerFrame;
		}
			
			private final GridBagConstraints setConstraints(int x, int y) {
		    	GridBagConstraints c = new GridBagConstraints();
		    	c.anchor = GridBagConstraints.WEST;
		    	c.gridx = x;
		    	c.gridy = y;
		    	c.insets = new Insets(2, 2 , 2, 2);
		    	return c;
		    }
			
			private final JButton createButton(String aTitle, String aCommand, String aToolTipText) {
				final JButton b = new JButton();
				b.setText(aTitle);
				b.addActionListener(this);
				b.setActionCommand(aCommand);
				b.setToolTipText(aToolTipText);
				b.setAlignmentX(Component.CENTER_ALIGNMENT);
				return b;
			}
			
			private final JTextField createTextField() {
				final JTextField tf = new JTextField(TEXT_FIELD_SIZE);
				tf.setEditable(true);
				fTFList.add(tf);
				return tf;
			}
}