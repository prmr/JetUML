package ca.mcgill.cs.jetuml.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A dictionary of key values pairs with string keys 
 * and values of types string, boolean, or int, enum. Putting a value
 * if a key already exists will silently override the previous value
 * with the same key. 
 * 
 * Iterable over keys. This class provides support for storing keys in a
 * meaninful order. By default, this is the order of insertion. However,
 * use of the method put(String, Object, int) allows client code to 
 * insert a property at a specific index. Keeping properties in order
 * allows for uses such as displaying properties in a predictable order,
 * for instance in GUI forms.
 * 
 * @author Martin P. Robillard
 *
 */
public class Properties implements Iterable<String>
{
	private final HashMap<String, Object> aProperties = new HashMap<>();
	private final List<String> aKeys = new ArrayList<>();
	
	/**
	 * Adds a property.
	 * 
	 * @param pKey The key.
	 * @param pValue The value, of type String,
	 * Integer, Boolean, or enum.
	 * @pre pKey != null && pValue != null
	 * @pre pValue is an int, boolean, String, or enum.
	 */
	public void put(String pKey, Object pValue)
	{
		assert pKey != null && pValue != null;
		assert pValue instanceof String || pValue instanceof Integer || pValue instanceof Boolean || pValue instanceof Enum;
		aProperties.put(pKey, pValue);
		if( !aKeys.contains(pKey) )
		{
			aKeys.add(pKey);
		}
	}
	
	/**
	 * Adds a property and inserts its key at the specified index, shifting
	 * all other keys down by one. If the key already exists, it's previous index
	 * is unchanged and its value is silently overwritten.
	 * 
	 * @param pKey The key.
	 * @param pValue The value, of type String,
	 * Integer, Boolean, or enum.
	 * @param pIndex Where to insert the key. Must be between 0 and size()-1, inclusive.
	 */
	public void put(String pKey, Object pValue, int pIndex)
	{
		assert pKey != null && pValue != null;
		assert pValue instanceof String || pValue instanceof Integer || pValue instanceof Boolean || pValue instanceof Enum;
		assert pIndex >=0 && pIndex <= aKeys.size();
		aProperties.put(pKey, pValue);
		if( !aKeys.contains(pKey) )
		{
			aKeys.add(pIndex, pKey);
		}
	}
	
	/**
	 * @param pKey The key to check.
	 * @return The value for this key.
	 * @pre pKey != null;
	 * @pre containsKey(pKey);
	 */
	public Object get(String pKey)
	{
		assert pKey != null && aProperties.containsKey(pKey);
		return aProperties.get(pKey);
	}

	@Override
	public Iterator<String> iterator()
	{
		return aKeys.iterator();
	}
}
