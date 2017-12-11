package ca.mcgill.cs.jetuml.persistence;

import java.util.HashMap;
import java.util.Iterator;

/**
 * A dictionary of key values pairs with string keys 
 * and values of types string, boolean, int, enum. Putting a value
 * if a key already exists will silently override the previous value
 * with the same key.
 * 
 * Iterable over keys.
 * 
 * @author Martin P. Robillard
 *
 */
public class Properties implements Iterable<String>
{
	private final HashMap<String, Object> aProperties = new HashMap<>();
	
	/**
	 * Adds a property.
	 * 
	 * @param pKey The key (not null).
	 * @param pValue The value (not null), of type String,
	 * Integer, Boolean, or enum.
	 */
	public void put(String pKey, Object pValue)
	{
		assert pKey != null && pValue != null;
		assert pValue instanceof String || pValue instanceof Integer || pValue instanceof Boolean || pValue instanceof Enum;
		aProperties.put(pKey, pValue);
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
		return aProperties.keySet().iterator();
	}
}
