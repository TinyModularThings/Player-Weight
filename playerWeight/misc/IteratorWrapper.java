package playerWeight.misc;

import java.util.Iterator;

public class IteratorWrapper<T> implements Iterable<T>
{
	Iterator<T> iter;
	
	public IteratorWrapper(Iterator<T> data)
	{
		iter = data;
	}

	@Override
	public Iterator<T> iterator()
	{
		return iter;
	}
	
}
