package serverpackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.Timer;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.table.TableRowSorter;

public final class ServerFrame  implements ActionListener {
	private final UserList fUserList;
	private final UserList fAdminList;
	private final JFrame fFrame;
	private final JRadioButton fAdminRadButton;
	private final JRadioButton fUsersRadButton;
	private final UserTableModel fTableModel;
	private final JTable fTable;
	private final JMenuItem viewItem; 
    private final JMenuItem removeItem;
    private final JMenuItem editItem;
    private DownloadServer ds;	  
   	private MessageServerAgain ms;
   	private SendServer ss;
    private ImageDownloadServer ids;
    private ImageSendServer iss;
    private boolean fServerRunning;
    private final JPopupMenu fPopupMenu;
    private String fCurAdminName;
    private Timer fWaitTimer;
    private final JToggleButton fStartToggleButton;
    
	 public ServerFrame(final UserList aUserList, final UserList aAdminList) {
		 this.fAdminList = aAdminList;
		 this.fUserList = aUserList;
		 fServerRunning = false;		 
		 ms = null;
	    	ss = null;
	    	ids = null;
	    	iss = null;	  	
		 final ActionListener radioButtonListener = new ActionListener() {
			 @Override
			 public void actionPerformed(ActionEvent ae) {
				final JRadioButton button = (JRadioButton) ae.getSource();
				final String text = button.getText();
				UserList map = text.equals("Admin") ? fAdminList : 
					                                  fUserList;
				fTableModel.setMap(map);
			 }
		 };
		 fAdminRadButton = createRadioButton("Admin", radioButtonListener);
		 fUsersRadButton = createRadioButton("User", radioButtonListener);
		 fAdminRadButton.setSelected(true);
		 final ButtonGroup group = new ButtonGroup();
		 group.add(fAdminRadButton);
		 group.add(fUsersRadButton);
	     final JPanel radioButtonPanel = new JPanel();
         radioButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
         radioButtonPanel.add(fAdminRadButton);
         radioButtonPanel.add(fUsersRadButton);
         fStartToggleButton = new JToggleButton();
         final Font toggleFont = new Font("Arial", Font.BOLD, 16);
         fStartToggleButton.setFont(toggleFont);
         fStartToggleButton.setText("START");
         fStartToggleButton.addItemListener(new ItemListener() {
             @Override
        	 public void itemStateChanged(ItemEvent ie) {
        	     final int state = ie.getStateChange();
	        	 if(state == ItemEvent.SELECTED) {
	        		 fStartToggleButton.setText("STOP");;
	        		 fServerRunning = true;
	        		ds = new DownloadServer(fUserList);	  
	     	  	 	createThread(ds);
	     	      	System.out.println("Server Starting ......");
	     	      	ms = new MessageServerAgain(fUserList);
	     	      	createThread(ms);
	     	      	ss = new SendServer(fUserList);
	     	          createThread(ss);
	     	        ids = new ImageDownloadServer(fUserList);
	     	          createThread(ids);
	     	        iss = new ImageSendServer(fUserList);
	     	          createThread(iss);    	     	          
	        	 } else if (state == ItemEvent.DESELECTED) {
	        		 fStartToggleButton.setEnabled(false);
	        		 fWaitTimer = new Timer();
	        		 fWaitTimer.scheduleAtFixedRate(new WaitTask(), 0, (1 * 1000));
	        		 fServerRunning = false;	      
	        		 stopThreads();
	        		 JOptionPane.showMessageDialog(fFrame, "For Security Reasons a wait of 2 minutes" +
	        		                               " is required before server restart.", "Wait Time", 
	        		                                JOptionPane.WARNING_MESSAGE);
	        	 }
        	 }
	      });
         final JPanel buttonPanel = new JPanel();
         buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
         buttonPanel.add(fStartToggleButton);
         fTableModel = new UserTableModel(fAdminList, "ADMIN");
         fTable = new JTable();
         fTable.setModel(fTableModel);
         fTable.setPreferredScrollableViewportSize(fTable.getPreferredSize());
         final JScrollPane pane = new JScrollPane(fTable);
	     pane.setPreferredSize(new Dimension(400, 600));
         final JViewport vp = pane.getViewport();
         vp.setBackground(Color.WHITE);
         fTable.setFocusable(false);
         fTable.setShowGrid(false);
         fTable.setRowSelectionAllowed(true);
         fTable.setCellSelectionEnabled(true);
         fTable.setColumnSelectionAllowed(false);
         fTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         fTable.setIntercellSpacing(new Dimension(0, 0));
	     RowSorter<UserTableModel> sorter = new TableRowSorter<UserTableModel>(fTableModel);
         fTable.setRowSorter(sorter);
         final JPanel scrollPanel = new JPanel();
         scrollPanel.add(pane);
         fPopupMenu = new JPopupMenu();
	     viewItem = createMenuItem("View", "VIEW_PROFILE");
	     removeItem = createMenuItem("Remove", "REMOVE_PROFILE(S)");
	     editItem = createMenuItem("Edit", "EDIT_PROFILE");
	     fPopupMenu.add(removeItem);
	     fPopupMenu.add(viewItem);
	     fPopupMenu.add(editItem);
	     fTable.addMouseListener(new MouseAdapter() {	    	   
	    	   @Override
	    	   public final void mouseReleased(MouseEvent me) {
	    		   final JTable source = (JTable)me.getSource();
  		           final int row = source.rowAtPoint(me.getPoint());
	    		   if(me.isPopupTrigger()) {
	    			   final Point point = me.getPoint();
	    			   final int column = source.columnAtPoint(point);
	    			   if(!source.isRowSelected(row)) {
	    				   source.changeSelection(row, column, false, false);
	    			   }
	    			   final boolean enable = source.getSelectedRowCount() == 1 ? true : false;
	    			   viewItem.setEnabled(enable);
		    		   editItem.setEnabled(enable);   
	    			   fPopupMenu.show(me.getComponent(), me.getX(), me.getY());
	    		   }
	    	   }
	    	   @Override
	    	   public final void mousePressed(MouseEvent me) {
	    		   final JTable table = (JTable)me.getSource();
	    		   final Point point = me.getPoint();
	    		   final int row = table.rowAtPoint(point);
	    		   if(me.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(me)) {
	    			   final UserTableModel model = (UserTableModel)table.getModel();
	    			  final UserList map = model.getMap();   
	    				final List<User> listUsers = new ArrayList<User>(map.values());
	    				final User user = listUsers.get(row);
	    				final UserInfoDialog d = new UserInfoDialog(fFrame, user, false);
	    		        d.setTitle("Account " + user.getUsername());
	    		        d.setUserEdit(fCurAdminName);
	    		   }
	    	   }
	       });  
	     final JMenuBar menuBar = new JMenuBar();
	     final JMenu fileMenu = new JMenu("File");
	     final JMenuItem exitMenuItem = createMenuItem("Exit", "EXIT");
	     final KeyStroke ksExit = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK);
	     exitMenuItem.setAccelerator(ksExit);
	     fileMenu.add(exitMenuItem);
	     menuBar.add(fileMenu);
	     final JMenu userMenu = new JMenu("Account");
	     final JMenu addMenu = new JMenu("Add");
	     userMenu.add(addMenu);
	     final JMenuItem adminMenuItem = createMenuItem("Admin", "ADD_ADMIN");
	     addMenu.add(adminMenuItem);
	     final JMenuItem userMenuItem = createMenuItem("User", "ADD_USER");
	     addMenu.add(userMenuItem);
	     menuBar.add(userMenu);
	     final JMenu helpMenu = new JMenu("Help");
	     final JMenuItem aboutMenuItem = createMenuItem("About", "ABOUT");
	     final KeyStroke ksAbout = KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK);
	     aboutMenuItem.setAccelerator(ksAbout);
	     helpMenu.add(aboutMenuItem);
	     menuBar.add(helpMenu);
	     fFrame = new JFrame("JChat Server");
	     final ImageIcon frameIcon = new ImageIcon("C:\\Users\\Bill\\Desktop\\chatHeader.png");
	     final Image frameImage = frameIcon.getImage();
	     fFrame.setIconImage(frameImage);
	     fFrame.setJMenuBar(menuBar);
	     fFrame.setLayout(new BorderLayout());
	     fFrame.add(radioButtonPanel, BorderLayout.NORTH);
	     fFrame.add(buttonPanel, BorderLayout.SOUTH);
	     fFrame.add(scrollPanel, BorderLayout.CENTER);
	     fFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	     fFrame.addWindowListener( new WindowAdapter() {
	    	@Override
	    	public final void windowClosing(WindowEvent we) {
		    	exitOperation();
    		}		    		
	     });
	     fFrame.pack();
	     fFrame.setLocationRelativeTo(null);
	     fFrame.setVisible(true);
	 }    
	 	  
		 @Override
		 public final void actionPerformed(ActionEvent ae) {
		  	final String command = ae.getActionCommand();
		  	final int selectionCount = fTable.getSelectedRowCount();
		  	final Boolean enable = (selectionCount > 1) ? false : true;
		  		viewItem.setEnabled(enable);
		  		editItem.setEnabled(enable);
		  		switch(command) {
			  		case "ADD_ADMIN":
			  			openCreateDialog("Admin");
			  			break;
			  		case "ADD_USER":
			  			openCreateDialog("User");
			  			break;
			  		case "EXIT":
			  			exitOperation();
			  			break;
			  		case "ABOUT":
			  			new AboutDialog(fFrame);
			  			break;
			  		default:
			  			final UserList curMap = fTableModel.getMap();
						final List<User> listUsers = new ArrayList<User>(curMap.values());			  		
						final int rows[] = fTable.getSelectedRows();	
						User user = null;
						int row = -1;
	                    String userName = "";
						if(command.equals("VIEW_PROFILE") ||
				  				command.equals("EDIT_PROFILE") ) {
							final boolean edit = command.equals("EDIT_PROFILE") ? true : false;
				  				row = rows[0];
				  				user = listUsers.get(row);
				  				userName = user.getUsername();
				  				final UserInfoDialog d = new UserInfoDialog(fFrame, user, edit);
				  				d.setTitle("Account " + userName);
				  				if(edit) {
				  					d.setUserEdit(fCurAdminName);
				  					UserList map = fUserList;
				  					String type = "User";
				  					if(fAdminRadButton.isSelected()) {
				  						map = fAdminList;
				  						type = "Admin";
				  					}
				  				    d.setUserMap(map);
				  				    d.setAccountType(type);
				  				    d.setTableModel(fTableModel);
				  				}
						} else if(command.equals("REMOVE_PROFILE(S)")) {
							for(int index = 0; index < rows.length; index++) {
								row = rows[index];
								fTableModel.removeUser(row);
							}			  					  		
					  	} 
		  	    }		  		
		 }
		 
		 private final void exitOperation() {
			 if(fServerRunning) {	
	 		    final int reply = JOptionPane.showConfirmDialog(fFrame, 
		    				                                    "Stop Server Processed and Exit?", 
		    				                                    "Exiting Server!", JOptionPane.YES_NO_OPTION);
		    		if(reply == JOptionPane.YES_OPTION) {
		    			startThread(true);
			    	}
	    	} else {
	    		startThread(false);
	    	}			 
		 }
		 
		 private final void openCreateDialog(final String aType) {
			 final CreateAccountDialog d = new CreateAccountDialog(aType, false, fFrame);
				d.setServerFrame(this);
				d.setCurAdminName(fCurAdminName);
				switch(aType) {
				case "Admin":
					d.setAdminMap(fAdminList);
					break;
				case "User":
					d.setUserMap(fUserList);
					break;
				}				
		 }
         
		 
		 private final void startThread(final boolean aRunning) {
			 final StopThreadsRunnable stopRunnable = new StopThreadsRunnable(this, aRunning);
	    	  final Thread t = new Thread(stopRunnable);
	    	  t.setDaemon(true);
	    	  t.start(); 
		 }
		 
		 private final void stopThreads() {
			 ds.interrupt();
			 iss.interrupt();
			 ms.interrupt();
			 ss.interrupt();
			 ids.interrupt();
		 }
		 
		 public final void setRadButtonSelected(final String aType) {
			 if(aType.equals("Admin")) {
				 fAdminRadButton.setSelected(true);
				 fTableModel.setMap(fAdminList);
			 } else {
				 fUsersRadButton.setSelected(true);
				 fTableModel.setMap(fUserList);
			 }				
		 }
		 
		 public final JFrame getParent() {
			 return this.fFrame;
		 }
		 
		 public final void setCurAdmin(final String aName) {
			 this.fCurAdminName = aName;
		 }
		 
		 private final JMenuItem createMenuItem(String aTitle, String aCommand) {
		    	JMenuItem mi = new JMenuItem();
		    	mi.setText(aTitle);
		    	mi.setActionCommand(aCommand);
		    	mi.addActionListener(this);
		    	return mi;
		    }
		 
		  private final void createThread(Runnable aServer) {
	      	final Thread t = new Thread(aServer);
	      	t.start();
	      } 
	      
	      private final JRadioButton createRadioButton(String aTitle, ActionListener aListener) {
	    	  final JRadioButton r = new JRadioButton(aTitle);
	    	  r.addActionListener(aListener);
	    	  return r;    	  
	      }
	      
	      class StopThreadsRunnable implements Runnable {
	    	  private final ServerFrame fSf;
	    	  private final boolean fRunning;
	    	  public StopThreadsRunnable(final ServerFrame aSf, final boolean aRunning) {
	    		    this.fSf = aSf;
	    		    this.fRunning = aRunning;
	    	  }
	    	  @Override
	    	  public final void run() {
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
              	    final User signingOutUser = fAdminList.getUser(fCurAdminName);
					signingOutUser.addUserLog(convEditDate, "Signed Out", hostip);
	    		  if(fRunning) {
	    		       fSf.stopThreads();
	    		  }
	    		  fUserList.store();
	    		  fAdminList.store();
	              fFrame.dispose();
	              System.exit(0);
	    	  }
	      }
	      
	      class WaitTask extends TimerTask {
	    	  final int delay = 1000;
	    	  int count = 120;
	    	  @Override
	    	  public final void run() {
	    			 if(count > 0) {
	        			 count--;
	        			 fStartToggleButton.setText("WAIT" + "(" + count + ")");
	        		 } else {
	        			 fStartToggleButton.setEnabled(true);
	        			 fStartToggleButton.setText("START");
	        			 fWaitTimer.cancel();
	        			 fWaitTimer.purge();
	        		 }	    		  
	          }
	      }
  } 
