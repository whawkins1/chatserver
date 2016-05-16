package serverpackage;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public final class UserTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
    private UserList fUserMap;
    private final String fType;
	public UserTableModel(UserList aUserMap,
                          final String aType) {
		this.fUserMap = aUserMap;
		this.fType = aType;
	}
	
		@Override
		public final Object getValueAt(int aRow , int aColumn) {
			final User user = convertGetUser(aRow);
			switch(aColumn) {
			case 0: 
				return user.getLastName();
			case 1:
				return user.getFirstName();
			case 2:
				final String signinDate = user.getLastSignin();
				if(signinDate == null) {
					return "-------";
				} 
				return signinDate;
			case 3:
				if(fType.equals("USER")) {
				   return (user.isOnline() ? "online" : "offline");
				}
			default:	
			 return null;
			}
		}
		
		@Override
		public final int getRowCount() {		
			return fUserMap.size();
		}
		
		@Override
		public final String getColumnName(int column) {
			switch(column) {
			case 0:
				return "Last";
			case 1: 
				return "First";
			case 2: 
				return "Last Sign In";
			case 3:
				return "Status";
			default:
				return null;
			}
		}
		
		@Override
		public final int getColumnCount() {
			switch(fType) {
			case "USER":
				return 4;
			case "ADMIN":
				return 3;
			default:
				return 0;
			}					
		}
		
		public final void addUser(final User aUser) {
			this.fUserMap.put(aUser.getUsername(), aUser);
			this.fireTableDataChanged();			
		}
		
		public final void removeUser(final int row) {
            final User user = convertGetUser(row);
            final String userName = user.getUsername();
		    this.fUserMap.remove(userName);
		    fireTableRowsDeleted(row, row);
		}
		
		private final User convertGetUser(final int row) {
			final List<User> listUsers = new ArrayList<User>(fUserMap.values());
            final User user = listUsers.get(row);
			return user;
		}
		
		public final void setMap(final UserList aMap) {
			this.fUserMap = aMap;
			fireTableDataChanged();
		}
		
		public final UserList getMap() {
			return this.fUserMap;
		}		
}