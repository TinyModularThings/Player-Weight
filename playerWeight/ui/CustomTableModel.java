package playerWeight.ui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import playerWeight.ui.typeEntry.ITypeEntry;

public class CustomTableModel extends AbstractTableModel
{
	List<ITypeEntry> types;
	final int currentType;
	static final String[][] mapGroup = new String[][]{
		{"Name", "Weight", "Max Size", "Max Size Default"},
		{"Name", "Weight"},
		{"Name", "Weight"},
		{"Name", "Weight"},
	};
	
	public CustomTableModel(List<ITypeEntry> entries, int type)
	{
		types = entries;
		currentType = type;
	}
	
	public List<ITypeEntry> getList()
	{
		return types;
	}
	
	@Override
	public String getColumnName(int column)
	{
		return mapGroup[currentType][column].toString();
	}
	
	@Override
	public int getRowCount()
	{
		return types.size();
	}
	
	@Override
	public int getColumnCount()
	{
		return mapGroup[currentType].length;
	}
	
	@Override
	public Object getValueAt(int row, int col)
	{
		return types.get(row).getData(col);
	}
	
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}
	
	@Override
	public void setValueAt(Object value, int row, int col)
	{
		fireTableCellUpdated(row, col);
	}
	
}
