package ca.mcgill.cs.jetuml.graph;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Represents a property of an object as a tuple that 
 * consists of a name, a getter for a value, and a setter
 * for that value, and a flag indicating whether the property
 * is visible or not. The distinction between visible and invisible
 * properties can be used, e.g., when creating UI components.
 * 
 * The values managed by a property should only be of immutable types.
 * 
 * @author Martin P. Robillard
 */
public class Property
{
	private final String aName;
	private final Supplier<Object> aGetter;
	private final Consumer<Object> aSetter;
	private final boolean aVisible;
	
	/**
	 * Creates a new property.
	 * 
	 * @param pName The name of the property.
	 * @param pGetter The getter for the property.
	 * @param pSetter The setter for the property.
	 * @param pVisible True if this is a visible property.
	 * @pre pName != null && pGetter != null && pSetter != null.
	 */
	public Property(String pName, Supplier<Object> pGetter, Consumer<Object> pSetter, boolean pVisible)
	{
		assert pName != null && pGetter != null && pSetter != null;
		aName = pName;
		aGetter = pGetter;
		aSetter = pSetter;
		aVisible = pVisible;
	}
	
	/**
	 * @return The name of this property.
	 */
	public String getName()
	{
		return aName;
	}
	
	/**
	 * @return True if this is a visible property.
	 */
	public boolean isVisible()
	{
		return aVisible;
	}
	
	/**
	 * @return The value of this property.
	 */
	public Object get()
	{
		return aGetter.get();
	}
	
	/**
	 * Assigns pValue to this property.
	 * 
	 * @param pValue The value to assign.
	 * @pre pValue != null
	 */
	public void set(Object pValue)
	{
		assert pValue != null;
		aSetter.accept(pValue);
	}
}
