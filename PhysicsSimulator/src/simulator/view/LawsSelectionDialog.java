package simulator.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.json.JSONObject;

import simulator.model.ForceLaws;

public class LawsSelectionDialog extends JDialog{
	
	private static final long serialVersionUID = 1L;

	private int _status;
	private List<JSONObject> listForce;
	
	private JComboBox<String> _laws;
	private DefaultComboBoxModel<String> _lawsModel;
	private JsonTableModel _dataTableModel;

	// This table model stores internally the content of the table. Use
	// getData() to get the content as JSON.
	//
	private class JsonTableModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private static final int NUMCOLUM = 3;
		private static final int NUMFIL = 1;
		private static final int VALUE_FIL_COL = 1;

		private String[] _header = { "Key", "Value", "Description" };
		private int numFil;
		String[][] _data;

		JsonTableModel() {
			numFil = NUMFIL;
			_data = new String[numFil][_header.length];
			refresh();
		}
		
		public void clear() {
			for (int i = 0; i < numFil; i++)
				for (int j = 0; j < NUMCOLUM; j++)
					_data[i][j] = "";
			fireTableStructureChanged();
		}

		public void refresh() {
			JSONObject forcelaw = listForce.get(_laws.getSelectedIndex());
			JSONObject data = forcelaw.getJSONObject("data");
			numFil = data.length();
			Set<String> keys = data.keySet();
			int i = 0;
			
			for(String k:keys) {
				_data[i][0] = k.toString();
				i++;
			}
			for (int j = 0; j < numFil; j++)
				_data[j][2] = data.getString(_data[j][0]);
			fireTableStructureChanged();
		}

		@Override
		public String getColumnName(int column) {
			return _header[column];
		}

		@Override
		public int getRowCount() {
			return _data.length;
		}

		@Override
		public int getColumnCount() {
			return _header.length;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if(columnIndex == VALUE_FIL_COL) {
				return true;
			}
			
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return _data[rowIndex][columnIndex];
		}

		@Override
		public void setValueAt(Object o, int rowIndex, int columnIndex) {
			_data[rowIndex][columnIndex] = o.toString();
		}

		// Method getData() returns a String corresponding to a JSON structure
		// with column 1 as keys and column 2 as values.

		// This method return the coIt is important to build it as a string, if
		// we create a corresponding JSONObject and use put(key,value), all values
		// will be added as string. This also means that if users want to add a
		// string value they should add the quotes as well as part of the
		// value (2nd column).
		//
		public String getData() {
			StringBuilder s = new StringBuilder();
			s.append('{');
			for (int i = 0; i < _data.length; i++) {
				if (!_data[i][0].isEmpty() && !_data[i][1].isEmpty()) {
					s.append('"');
					s.append(_data[i][0]);
					s.append('"');
					s.append(':');
					s.append(_data[i][1]);
					s.append(',');
				}
			}

			if (s.length() > 1)
				s.deleteCharAt(s.length() - 1);
			s.append('}');

			return s.toString();
		}
	}

	LawsSelectionDialog(Frame parent, List<JSONObject> forceLaws) {
		super(parent, true);
		listForce = new ArrayList<>(forceLaws);
		initGUI();
	}

	private void initGUI() {

		_status = 0;

		setTitle("Build JSON from Table");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);

		// help
		JLabel help1 = new JLabel("<html><p>Select a force law and provide values for the parametes in the Value column (default values are used for </p></html>");
		JLabel help2 = new JLabel("<html><p>parametes with no value).</p>");
		help1.setAlignmentX(CENTER_ALIGNMENT);
		help2.setAlignmentX(CENTER_ALIGNMENT);
		
		//JCombobox
		JPanel comboPanel = new JPanel();
		comboPanel.setAlignmentX(CENTER_ALIGNMENT);
		
		_lawsModel = new DefaultComboBoxModel<>();
		_laws = new JComboBox<>(_lawsModel);
		for(JSONObject f: listForce)
			_lawsModel.addElement(f.getString("desc"));
		comboPanel.add(_laws);

		// data table
		_dataTableModel = new JsonTableModel();
		JTable dataTable = new JTable(_dataTableModel) {
		private static final long serialVersionUID = 1L;

			// we override prepareRenderer to resized rows to fit to content
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				int rendererWidth = component.getPreferredSize().width;
				TableColumn tableColumn = getColumnModel().getColumn(column);
				tableColumn.setPreferredWidth(
						Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
				return component;
			}
		};
		JScrollPane tabelScroll = new JScrollPane(dataTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		

		// bottons
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setAlignmentX(CENTER_ALIGNMENT);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_status = 0;
				LawsSelectionDialog.this.setVisible(false);
			}
		});
		buttonsPanel.add(cancelButton);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_status = 1;
				LawsSelectionDialog.this.setVisible(false);
			}
		});
		buttonsPanel.add(okButton);
		
		mainPanel.add(help1);
		mainPanel.add(help2);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		
		mainPanel.add(tabelScroll);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		
		mainPanel.add(comboPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		
		mainPanel.add(buttonsPanel);

		setPreferredSize(new Dimension(400, 400));

		pack();
		setResizable(false); // change to 'true' if you want to allow resizing
		setVisible(false); // we will show it only whe open is called
	}

	public int open(List<JSONObject> law) {

		if (getParent() != null)
			setLocation(//
					getParent().getLocation().x + getParent().getWidth() / 2 - getWidth() / 2, //
					getParent().getLocation().y + getParent().getHeight() / 2 - getHeight() / 2);
		_lawsModel.removeAllElements();
		for(JSONObject f: law)
			_lawsModel.addElement(f.getString("desc"));
		pack();
		setVisible(true);
		return _status;
	}

	public String getJSON() {
		return _dataTableModel.getData();
	}
}
