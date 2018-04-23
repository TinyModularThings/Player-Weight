package playerWeight.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;


public class XMLNode
{
	Map<String, String> values = new HashMap<String, String>();
	
	public XMLNode(String node)
	{
		loadValues(node.substring(node.indexOf("<") + 1, node.lastIndexOf(">")).split(" "));
	}
	
	private void loadValues(String[] toProcess)
	{
		for(String s : toProcess)
		{
			String[] data = s.split("=");
			if(data.length == 2)
			{
				values.put(data[0], data[1]);
			}
		}
	}
	
	public boolean hasEntry(String key)
	{
		return values.containsKey(key);
	}
	
	public boolean isType(String key, Predicate<String> check)
	{
		return check.test(values.get(key));
	}
	
	public String get(String key)
	{
		return values.get(key);
	}
	
	public int getAsInt(String key)
	{
		return Integer.parseInt(values.getOrDefault(key, "0"));
	}
	
	public int[] getAsIntArray(String key)
	{
		String[] data = values.getOrDefault(key, "").split(":");
		int[] result = new int[data.length];
		for(int i = 0;i<data.length;i++)
		{
			result[i] = Integer.parseInt(data[i]);
		}
		return result;
	}
	
	public double getAsDouble(String key)
	{
		return Double.parseDouble(values.getOrDefault(key, "0"));
	}
}
