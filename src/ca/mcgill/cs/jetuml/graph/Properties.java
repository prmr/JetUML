package ca.mcgill.cs.jetuml.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A dictionary of key values pairs with string keys and values 
 * that are actually suppliers of values. Putting a value
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
	private final HashMap<String, Supplier<Object>> aGetters = new HashMap<>();
	private final HashMap<String, Consumer<Object>> aSetters = new HashMap<>();
	private final List<String> aKeys = new ArrayList<>();
	
	/**
	 * Adds a property.
	 * 
	 * @param pKey The key.
	 * @param pGetter The getter for this property.
	 * @param pSetter The setter for this property.
	 * @pre pKey != null && pValue != null
	 * @pre pValue is an int, boolean, String, or enum.
	 */
	public void put(String pKey, Supplier<Object> pGetter, Consumer<Object> pSetter)
	{
		assert pKey != null && pGetter != null & pSetter != null;
		aGetters.put(pKey, pGetter);
		aSetters.put(pKey, pSetter);
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
	 * @param pGetter The getter for this property.
	 * @param pSetter The setter for this property.
	 * @param pIndex Where to insert the key. Must be between 0 and size()-1, inclusive.
	 */
	public void put(String pKey, Supplier<Object> pGetter, Consumer<Object> pSetter, int pIndex)
	{
		assert pKey != null && pGetter != null;
		assert pIndex >=0 && pIndex <= aKeys.size();
		aGetters.put(pKey, pGetter);
		aSetters.put(pKey, pSetter);
		if( !aKeys.contains(pKey) )
		{
			aKeys.add(pIndex, pKey);
		}
	}
	
	/**
	 * @param pKey The required property.
	 * @return The value of this property.
	 * @pre containsKey(pKey);
	 */
	public Object get(String pKey)
	{
		assert pKey != null && aGetters.containsKey(pKey);
		return aGetters.get(pKey).get();
	}
	
	/**
	 * @param pProperty The key to check.
	 * @param pValue The value to set.
	 * @pre pKey != null;
	 * @pre containsKey(pKey);
	 */
	public void set(String pProperty, Object pValue)
	{
		assert pProperty != null && aSetters.containsKey(pProperty);
		aSetters.get(pProperty).accept(pValue);
	}

	@Override
	public Iterator<String> iterator()
	{
		return aKeys.iterator();
	}
}
