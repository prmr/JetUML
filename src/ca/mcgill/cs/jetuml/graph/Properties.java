package ca.mcgill.cs.jetuml.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A class to externalize the properties of an object into a separate
 * object. For this object, a "property" is an association between a key
 * and a getter-setter pair. The key is the name of the property. The "getter"
 * is a Supplier implementation that can supply the current value of the property
 * in the original object. Likewise, the "setter" is an implementation of the 
 * Consumer interface that can set the value of the property in the original 
 * object. The class hides the delegation to the setter and getter by allowing
 * client code to directly get or set the value of a property.
 * 
 * It is very important that the type of a property, returned by the getter
 * and set by the setter, be immutable and non-null.
 * 
 * Adding a property that already exists will silently override the previous getter
 * and setter for this property.
 * 
 * Properties also have a "visibility" bit which can be used to distinguish between
 * inherent properties of an object that e.g., must be serialized, and properties that
 * should be visible and editable by a user. By default, properties are visible.
 * 
 * Iterable over keys (property names). This class provides support for storing properties
 * in a meaningful order. By default, this is the order of insertion. However,
 * use of the method put allows client code to insert a property at a specific index. 
 * Keeping properties in order allows for uses such as displaying properties in a predictable 
 * order, for instance in GUI forms.
 * 
 * @author Martin P. Robillard
 *
 */
public class Properties implements Iterable<String>
{
	private final HashMap<String, Supplier<Object>> aGetters = new HashMap<>();
	private final HashMap<String, Consumer<Object>> aSetters = new HashMap<>();
	private final HashMap<String, Boolean> aVisible = new HashMap<>();
	private final List<String> aKeys = new ArrayList<>();
	
	/**
	 * Adds a visible property to the list. The property is added at the end of the list.
	 * 
	 * @param pPropertyName The name of the property.
	 * @param pGetter The getter for this property.
	 * @param pSetter The setter for this property.
	 * @pre pPropertyName != null && pGetter != null && pSetter != null
	 */
	public void add(String pPropertyName, Supplier<Object> pGetter, Consumer<Object> pSetter)
	{
		assert pPropertyName != null && pGetter != null & pSetter != null;
		aGetters.put(pPropertyName, pGetter);
		aSetters.put(pPropertyName, pSetter);
		aVisible.put(pPropertyName, true);
		if( !aKeys.contains(pPropertyName) )
		{
			aKeys.add(pPropertyName);
		}
	}
	
	/**
	 * Adds an invisible property to the list. The property is added at the end of the list.
	 * 
	 * @param pPropertyName The name of the property.
	 * @param pGetter The getter for this property.
	 * @param pSetter The setter for this property.
	 * @pre pPropertyName != null && pGetter != null && pSetter != null
	 */
	public void addInvisible(String pPropertyName, Supplier<Object> pGetter, Consumer<Object> pSetter)
	{
		assert pPropertyName != null && pGetter != null & pSetter != null;
		aGetters.put(pPropertyName, pGetter);
		aSetters.put(pPropertyName, pSetter);
		aVisible.put(pPropertyName, false);
		if( !aKeys.contains(pPropertyName) )
		{
			aKeys.add(pPropertyName);
		}
	}
	
	/**
	 * Adds a visible property and inserts its key at the specified index, shifting
	 * all other properties down by one. If the property already exists, its previous index
	 * is unchanged and its getter and setter are silently overwritten.
	 * 
	 * @param pPropertyName The name of the property.
	 * @param pGetter The getter for this property.
	 * @param pSetter The setter for this property.
	 * @param pIndex Where to insert the property. Must be between 0 and size()-1, inclusive.
	 */
	public void addAt(String pPropertyName, Supplier<Object> pGetter, Consumer<Object> pSetter, int pIndex)
	{
		assert pPropertyName != null && pGetter != null;
		assert pIndex >=0 && pIndex <= aKeys.size();
		aGetters.put(pPropertyName, pGetter);
		aSetters.put(pPropertyName, pSetter);
		aVisible.put(pPropertyName, true);
		if( !aKeys.contains(pPropertyName) )
		{
			aKeys.add(pIndex, pPropertyName);
		}
	}
	
	/**
	 * @param pProperty The property to check for visibility. Must exist.
	 * @return True iff the property is visible.
	 * @pre pProperty != null && containsKey(pProperty)
	 */
	public boolean isVisible(String pProperty)
	{
		assert pProperty != null && aVisible.containsKey(pProperty);
		return aVisible.get(pProperty);
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
