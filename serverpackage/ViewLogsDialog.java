package serverpackage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public final class ViewLogsDialog {
    
    public ViewLogsDialog(final JDialog aParent, final User aUser) {
    	JTable fTable = new JTable();
    	ViewLogsTableModel fTableModel = new ViewLogsTableModel(aUser);
	    fTable.setModel(fTableModel);
	    fTable.setFocusable(false);
	    fTable.setShowGrid(false);
	    fTable.setRowSelectionAllowed(false);
	    fTable.setCellSelectionEnabled(false);
	    fTable.setColumnSelectionAllowed(false);
	    fTable.setIntercellSpacing(new Dimension(0, 0));    	
	    final JScrollPane pane = new JScrollPane(fTable);
	    final JDialog dialog = new JDialog(aParent, "Logs", false);
	    dialog.setLayout(new BorderLayout());
	    final JPanel buttonPanel = new JPanel();
	    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
	    dialog.add(pane, BorderLayout.CENTER);
	    dialog.add(buttonPanel, BorderLayout.SOUTH);
	    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    dialog.pack();
	    dialog.setLocationRelativeTo(aParent);
	    dialog.setVisible(true);
    }
}