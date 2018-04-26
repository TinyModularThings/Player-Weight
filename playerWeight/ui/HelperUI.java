package playerWeight.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import playerWeight.ui.typeEntry.ITypeEntry;

public class HelperUI extends JFrame
{
	JPanel contentPane;
	JTable table;
	CustomTableModel model;
	JScrollPane scrollPane;
	JButton setSizeButton;
	JButton resetButton;
	final ButtonGroup TypeGroup = new ButtonGroup();
	int currentType = -1;
	final List<ITypeEntry> currentList = new ArrayList<ITypeEntry>();
	private JButton giveItem;
	
	public HelperUI()
	{
		initUI();
		onListChange(0);
	}
	
	private void initUI()
	{
		setResizable(false);
		setTitle("CustomParser");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 844, 541);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(5, 5, 533, 486);
		contentPane.add(scrollPane);
		
		model = new CustomTableModel(currentList, 0);
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(table);
		
		JRadioButton items = new JRadioButton("Items", true);
		items.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				onListChange(0);
			}
		});
		TypeGroup.add(items);
		items.setBounds(544, 7, 58, 23);
		contentPane.add(items);
		
		JRadioButton itemstacks = new JRadioButton("ItemStacks");
		itemstacks.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				onListChange(1);
			}
		});
		TypeGroup.add(itemstacks);
		itemstacks.setBounds(544, 33, 92, 23);
		contentPane.add(itemstacks);
		
		JRadioButton oredict = new JRadioButton("OreDictionary");
		oredict.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				onListChange(2);
			}
		});
		TypeGroup.add(oredict);
		oredict.setBounds(544, 85, 109, 23);
		contentPane.add(oredict);
		
		JRadioButton fluids = new JRadioButton("Fluids");
		fluids.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				onListChange(3);
			}
		});
		TypeGroup.add(fluids);
		fluids.setBounds(544, 59, 73, 23);
		contentPane.add(fluids);
		
		JButton btnSetWeight = new JButton("Set Weight");
		btnSetWeight.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int[] selection = getTable().getSelectedRows();
				if(selection.length == 0)
				{
					JOptionPane.showConfirmDialog(null, "You need to select Entries to change!", "Setting Weight", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				}
				String value = JOptionPane.showInputDialog(null, "Please type in the Weight", "Setting Weight", JOptionPane.QUESTION_MESSAGE);
				try
				{
					double result = Double.parseDouble(value);
					List<ITypeEntry> type = getModel().getList();
					for(int i : selection)
					{
						removeChange(type.get(i));
						type.get(i).setWeight(result);
						addChange(type.get(i));
					}
					getModel().fireTableDataChanged();
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
					JOptionPane.showConfirmDialog(null, "Number can not be parsed", "Setting Weight", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
				}
			}
			
		});
		btnSetWeight.setBounds(548, 129, 128, 23);
		contentPane.add(btnSetWeight);
		
		setSizeButton = new JButton("Set Max Size");
		setSizeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] selection = getTable().getSelectedRows();
				if(selection.length == 0)
				{
					JOptionPane.showConfirmDialog(null, "You need to select Entries to change!", "Setting Max Size", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				}
				String value = JOptionPane.showInputDialog(null, "Please type in the Max StackSize", "Setting Max Size", JOptionPane.QUESTION_MESSAGE);
				try
				{
					int result = Integer.parseInt(value);
					if(result < 1 || result > 64)
					{
						JOptionPane.showConfirmDialog(null, "Value needs to be between 1-64", "Setting Max Size", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
						return;
					}
					List<ITypeEntry> type = getModel().getList();
					for(int i : selection)
					{
						removeSize(type.get(i));
						type.get(i).setSize(result);
						addSize(type.get(i));
					}
					getModel().fireTableDataChanged();
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
					JOptionPane.showConfirmDialog(null, "Number can not be parsed", "Setting Weight", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		setSizeButton.setBounds(548, 163, 128, 23);
		contentPane.add(setSizeButton);
		
		JButton btnNewButton = new JButton("Clear Weight");
		btnNewButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] selection = getTable().getSelectedRows();
				if(selection.length == 0)
				{
					JOptionPane.showConfirmDialog(null, "You need to select Entries to change!", "Clear Weight", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				}
				List<ITypeEntry> type = getModel().getList();
				for(int i : selection)
				{
					removeChange(type.get(i));
					type.get(i).setWeight(0D);
				}
				getModel().fireTableDataChanged();
			}
		});
		btnNewButton.setBounds(686, 129, 128, 23);
		contentPane.add(btnNewButton);
		
		resetButton = new JButton("Reset Max Size");
		resetButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] selection = getTable().getSelectedRows();
				if(selection.length == 0)
				{
					JOptionPane.showConfirmDialog(null, "You need to select Entries to change!", "Reseting Max Size", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				}
				List<ITypeEntry> type = getModel().getList();
				for(int i : selection)
				{
					removeSize(type.get(i));
					type.get(i).setSize(0);
				}
				getModel().fireTableDataChanged();
			}
		});
		resetButton.setBounds(686, 163, 128, 23);
		contentPane.add(resetButton);
		
		giveItem = new JButton("Give Item");
		giveItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] selection = getTable().getSelectedRows();
				if(selection.length == 0)
				{
					JOptionPane.showConfirmDialog(null, "You need to select Entries to change!", "Give Item", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				}
				List<ItemStack> list = new ArrayList<ItemStack>();
				List<ITypeEntry> type = getModel().getList();
				for(int i : selection)
				{
					ItemStack stack = type.get(i).makeStack();
					if(stack.isEmpty())
					{
						continue;
					}
					list.add(stack.copy());
				}
				givePlayerItems(list);
			}
		});
		giveItem.setBounds(548, 197, 128, 23);
		contentPane.add(giveItem);
		
		JButton btnNewButton_1 = new JButton("Export Changes");
		btnNewButton_1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String name = JOptionPane.showInputDialog(null, "Please Type in the File name to Export to (It gets overriden)", "Export Changes", JOptionPane.QUESTION_MESSAGE);
				if(name == null || name.isEmpty())
				{
					return;
				}
				int delete = JOptionPane.showConfirmDialog(null, "Do you want clear all Cached changes after export? (Effects only next Change Export)", "Export Changes", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				ChangeRegistry.INSTANCE.exportChanges(name + ".xml", delete == 0);
			}
		});
		btnNewButton_1.setBounds(625, 468, 132, 23);
		contentPane.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Load loaded Changes");
		btnNewButton_2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int total = ChangeRegistry.INSTANCE.loadChanges();
				JOptionPane.showConfirmDialog(null, "Loaded " + total + " Changes from Existing Files", "Load Changes", JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnNewButton_2.setBounds(612, 434, 155, 23);
		contentPane.add(btnNewButton_2);
	}
	
	public void onListChange(int type)
	{
		if(currentType != type)
		{
			currentType = type;
			currentList.clear();
			ChangeRegistry.INSTANCE.addToList(currentType, currentList);
			model = new CustomTableModel(currentList, type);
			table.setModel(model);
			setSizeButton.setEnabled(type == 0);
			resetButton.setEnabled(type == 0);
			giveItem.setEnabled(type == 0 || type == 1);
		}
	}
	
	final JTable getTable()
	{
		return table;
	}
	
	final CustomTableModel getModel()
	{
		return model;
	}
	
	void removeChange(ITypeEntry entry)
	{
		if(entry.isChanged(false))
		{
			ChangeRegistry.INSTANCE.removeChange(entry.makeChange(false));
		}
	}
	
	void addChange(ITypeEntry entry)
	{
		if(entry.isChanged(false))
		{
			ChangeRegistry.INSTANCE.addChange(entry.makeChange(false));
		}
	}
	
	void removeSize(ITypeEntry entry)
	{
		if(entry.isChanged(true))
		{
			ChangeRegistry.INSTANCE.removeChange(entry.makeChange(false));
		}
	}
	
	void addSize(ITypeEntry entry)
	{
		if(entry.isChanged(true))
		{
			ChangeRegistry.INSTANCE.addChange(entry.makeChange(false));
		}
	}
	
	@SideOnly(Side.CLIENT)
	void givePlayerItems(List<ItemStack> list)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(player == null)
		{
			JOptionPane.showConfirmDialog(null, "Player needs to exist!", "Give Item", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		}
		player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(player.getUniqueID());
		if(player == null)
		{
			JOptionPane.showConfirmDialog(null, "Player needs to exist!", "Give Item", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		}
		for(ItemStack stack : list)
		{
			if(!player.inventory.addItemStackToInventory(stack))
			{
				player.dropItem(stack, false);
			}
		}
	}
}
