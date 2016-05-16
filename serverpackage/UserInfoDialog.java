package serverpackage;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public final class UserInfoDialog implements ActionListener{
   final int TEXT_FIELD_SIZE = 15;
   private String fAccountType;
   private UserTableModel fTableModel;
   private final JDialog fDialog;
   private final JButton fSaveButton;
   private JButton fRemoveButton;
   private JButton fResetPasswordButton;
   private JButton fViewLogsButton;
   private final JTextField fFirstNameField;
   private final JTextField fLastNameField;
   private final JTextField fUserNameField;
   private User fUser;
   private final boolean fEdit;
   private final JPanel fButtonPanel;
   private UserList fUserMap;
   private String fUserEdit;
   private final List<JTextField> fTFList;
   
	public UserInfoDialog(final JFrame aParent, final User aUser,
			              final boolean aEdit) {
		this.fUser = aUser;
		this.fEdit = aEdit;
		fTFList = new ArrayList<JTextField>();
		fFirstNameField  = createTextField(fUser.getFirstName());
		fFirstNameField.setEnabled(fEdit);
	    fLastNameField = createTextField(fUser.getLastName());
	    fLastNameField.setEnabled(fEdit);
	    fUserNameField = createTextField(fUser.getUsername());
	    fUserNameField.setEnabled(false);
	    final JLabel firstNameLabel = new JLabel("First");
	    final JLabel lastNameLabel = new JLabel("Last");
	    final JLabel userNameLabel = new JLabel("Username");
	    final JPanel accountPanel = new JPanel();
	    accountPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
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
	    fButtonPanel = new JPanel();
        fButtonPanel.setLayout(new GridLayout(4, 1, 4, 4)); 
	    fSaveButton = createButton("Save", "SAVE", "Save" + " Profile");
	   	final JLabel lastEditedLabel = new JLabel();
	    final Font font = new Font("Times New Roman", Font.ITALIC, 12);
	    lastEditedLabel.setFont(font);
	    final String editDate = fUser.getLastEditedDate();
	    final String editUser = fUser.getLastEditBy();	
	    String concMessage = (editDate == null) ? "-------" :
        "Last Edited by " + editUser + " at " + editDate.toString();
        lastEditedLabel.setText(concMessage);
        final GridBagConstraints c12 = setConstraints(0,5);
        accountPanel.add(lastEditedLabel, c12);
        fResetPasswordButton = createButton("Reset Password", "RESET_PASSWORD", "Reset Users Password");
        if(fUser.changePassword()) {
        	fResetPasswordButton.setEnabled(false);
        	fResetPasswordButton.setToolTipText("Password is Flagged to be Reset");
        }
        fRemoveButton = createButton("Remove", "REMOVE", "Remove Profile");
        fViewLogsButton = createButton("View Logs", "VIEW_LOGS", "View Logs of Account Activity");
   		fDialog = new JDialog(aParent, false);
   		fDialog.addWindowListener(new WindowAdapter() {
   			@Override
   		   public final void windowClosing(WindowEvent we) {
   			if(fEdit) {
	   			final int answer = JOptionPane.showConfirmDialog(fDialog, 
	                                                             "Account Info Not Complete Close Anyway?",
	                                                              "Closing", JOptionPane.YES_NO_OPTION);
	                      if(answer == JOptionPane.YES_OPTION) {
		                     fDialog.dispose();
		             }     
	           }        
   		  }
        });
   		fDialog.setLayout(new BorderLayout());
		fDialog.add(accountPanel, BorderLayout.EAST);
		fDialog.add(new JSeparator(JSeparator.VERTICAL), BorderLayout.CENTER);
		fDialog.add(fButtonPanel, BorderLayout.WEST);
		fDialog.pack();
		fDialog.setLocationRelativeTo(aParent);
		fDialog.setVisible(true);
   }
	@Override
	public final void actionPerformed(ActionEvent ae) {
		final String command = ae.getActionCommand();
		switch(command) {
		case "SAVE":
			boolean validEntries = true;
			for(JTextField tf : fTFList) {
				if(tf.getText().trim().equals("")) {
					validEntries = false;
					break;
				}
			}
			if(validEntries) {
				final String firstName = fFirstNameField.getText().trim(); 
				fUser.setFirstName(firstName);
				final String lastName = fLastNameField.getText().trim();
				fUser.setLastName(lastName);
				fUser.setAccountType(fAccountType);
				Date editDate = new Date();
				final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yy:HH:mm");
	            final String convEditDate = formatter.format(editDate);
			    fUser.setLastEditDate(convEditDate);
			    fUser.setLastEditBy(fUserEdit);
			    fTableModel.addUser(fUser);
			    fDialog.dispose();
			} else {
				SignInDialog.showErrorMessage("All Account Info Must Be Complete, "
	                      + "Please Try Again.", "Account Error!");		
			}
		case "REMOVE":
			final String userName = fUser.getUsername().trim();
			if(userName != "") {
				fUserMap.remove(userName);
			}
			fDialog.dispose();
		case "RESET_PASSWORD":
			fUser.setChangePassword(true);
		case "VIEW_LOGS":
			new ViewLogsDialog(fDialog, fUser);
		}
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
			fButtonPanel.add(b);
			return b;
		}
		
		private final JTextField createTextField(String aContent) {
			final JTextField tf = new JTextField(TEXT_FIELD_SIZE);
			tf.setText(aContent);
			tf.setEditable(true);
			fTFList.add(tf);
			return tf;
		}
		
		public final void setTableModel(UserTableModel aModel) {
			this.fTableModel = aModel;
		}
		
		public final void setAccountType(final String aType) {
		    this.fAccountType = aType;	
		}
		
		public final void setTitle(final String aTitle) {
			fDialog.setTitle(aTitle);
		}
		
		public final void setUserEdit(final String aUserEdit) {
			this.fUserEdit = aUserEdit;
		}
		
		public final void setUserMap(UserList aMap) {
			this.fUserMap = aMap;
		}
}
