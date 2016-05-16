package serverpackage;

import javax.swing.table.AbstractTableModel;

public class ViewLogsTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
	    private final User fUser;
		public ViewLogsTableModel(final User aUser) {
			this.fUser = aUser;
		}
		
		@Override
		public final Object getValueAt(int aRow , int aColumn) {
			switch(aColumn) {
			case 0:
				return fUser.getLogDate(aRow);
			case 1:
	            return fUser.getLogActivity(aRow);	            
			case 2:
				return fUser.getLogIP(aRow);
			default:
				return null;
			}			
		}
		
		@Override
		public final String getColumnName(final int aColumn) {
		    switch(aColumn){
		    case 0:
		    	return "Date and Time";
		    case 1:
		    	return "Activity";
		    case 2:
		    	return "IP Address";
		    default:
			return null;
		}
}
		
		@Override
		public final int getRowCount() {		
			return fUser.getModelSize();
		}
		
		@Override
		public final int getColumnCount() {
			return 3;		
		}
}
