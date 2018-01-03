package ca.mcgill.cs.jetuml.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A class to externalize the properties of an object into a separate
 * object. 
 * 
 * Adding a property that already exists will silently override the previous getter
 * and setter for this property.
 * 
 * This class provides support for storing properties in a meaningful order. 
 * By default, this is the order of insertion. However, use of the method <code>addAt</code>
 * allows client code to insert a property at a specific index. Keeping properties in order
 * allows for uses such as displaying properties in a predictable order, for instance 
 * in GUI forms.
 * 
 * @author Martin P. Robillard
 *
 */
public class Properties implements Iterable<Property>
{
	private final List<Property> aProperties = new ArrayList<>();
	
	/**
	 * Adds a visible property to the list. The property is added at the end of the list.
	 * 
	 * @param pName The name of the property.
	 * @param pGetter The getter for this property.
	 * @param pSetter The setter for this property.
	 * @pre pPropertyName != null && pGetter != null && pSetter != null
	 */
	public void add(String pName, Supplier<Object> pGetter, Consumer<Object> pSetter)
	{
		assert pName != null && pGetter != null & pSetter != null;
		if( !contains(pName) )
		{
			aProperties.add(new Property(pName, pGetter, pSetter, true));
		}
	}
	
	/**
	 * @param pName The name to check
	 * @return True if there is already a property with this name in the list.
	 */
	private boolean contains(String pName)
	{
		for( Property property : aProperties)
		{
			if( property.getName().equals(pName))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param pName The name of the property to get.
	 * @return The property with pName.
	 * @pre pName != null && contains(pName)
	 */
	public Property get(String pName)
	{
		assert pName != null && contains(pName);
		for( Property property : aProperties )
		{
			if( property.getName().equals(pName))
			{
				return property;
			}
		}
		assert false;
		return null;
	}
	
	/**
	 * Adds an invisible property to the list. The property is added at the end of the list.
	 * 
	 * @param pName The name of the property.
	 * @param pGetter The getter for this property.
	 * @param pSetter The setter for this property.
	 * @pre pPropertyName != null && pGetter != null && pSetter != null
	 */
	public void addInvisible(String pName, Supplier<Object> pGetter, Consumer<Object> pSetter)
	{
		assert pName != null && pGetter != null & pSetter != null;
		if( !contains(pName) )
		{
			aProperties.add(new Property(pName, pGetter, pSetter, false));
		}
	}
	
	/**
	 * Adds a visible property and inserts its key at the specified index, shifting
	 * all other properties down by one. If the property already exists, its previous index
	 * is unchanged and its getter and setter are silently overwritten.
	 * 
	 * @param pName The name of the property.
	 * @param pGetter The getter for this property.
	 * @param pSetter The setter for this property.
	 * @param pIndex Where to insert the property. Must be between 0 and size()-1, inclusive.
	 */
	public void addAt(String pName, Supplier<Object> pGetter, Consumer<Object> pSetter, int pIndex)
	{
		assert pName != null && pGetter != null;
		assert pIndex >=0 && pIndex <= aProperties.size();
		if( !contains(pName) )
		{
			aProperties.add(pIndex, new Property(pName, pGetter, pSetter, true));
		}
	}

	@Override
	public Iterator<Property> iterator()
	{
		return aProperties.iterator();
	}
}
