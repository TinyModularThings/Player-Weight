package playerWeight.ui;

import java.util.Comparator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import playerWeight.ui.typeEntry.ITypeEntry;
import playerWeight.ui.typeEntry.ITypeEntry.SorterType;

public class CustomTableModel extends AbstractTableModel
{
	List<ITypeEntry> types;
	final int currentType;
	static final String[][] mapGroup = new String[][]{
		{"Name", "Mod", "Weight", "Max Size", "Max Size Default"},
		{"Name", "Mod", "Weight"},
		{"Name", "Weight"},
		{"Name", "Weight/Bucket", "Mod"},
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
	
	public void sort(final SorterType type, boolean inverted)
	{
		Comparator<ITypeEntry> sorter = new Comparator<ITypeEntry>(){
			@Override
			public int compare(ITypeEntry o1, ITypeEntry o2)
			{
				return o1.sort(o2, type);
			}
		};
		if(inverted)
		{
			sorter = sorter.reversed();
		}
		types.sort(sorter);
		fireTableDataChanged();
	}
	
}
