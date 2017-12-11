package ca.mcgill.cs.jetuml.graph;

/**
 * Simple service to return a value of a specified type.
 * The type is limited to int, boolean, or String,
 * as specified by the enum type Type.
 * 
 * @author Martin P. Robillard
 */
public interface ValueExtractor
{
	/**
	 * The type of a requested value.
	 */
	enum Type
	{
		INT, BOOLEAN, STRING;
	}
	
	/**
	 * Returns a value for a key, where the value
	 * is of a requested type. The value is of type
	 * Integer, Boolean, or String.
	 * 
	 * @param pKey The key for the requested value.
	 * @param pType The type of value expected.
	 * @return The expected value.
	 */
	Object get(String pKey, Type pType);
}
