package org.jetuml.persistence.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A JsonArray is an ordered sequence of values. Its external text form is a
 * string wrapped in square brackets with commas separating the values.
 */
public class JsonArray implements Iterable<Object>
{
	private final ArrayList<Object> aElements = new ArrayList<>();

	/**
	 * Construct an empty JsonArray.
	 */
	public JsonArray() {}
	
	/**
	 * Append an object value. This increases the array's length by one.
	 *
	 * @param pValue A valid Json value.
 	 * @throws JsonException If value is null or not of a valid Json value.
	 */
	public void add(Object pValue)
	{
		JsonValueValidator.validateType(pValue);
		aElements.add(pValue);
	}

	/**
	 * Construct a JsonArray by adding each element from pList in sequence. 
	 * 
	 * @param pList A list of JSON values to store in the array.
	 * @pre pList != null
	 * @throws JsonException if any of the element in the list is not a valid Json value.
	 */
	public JsonArray(List<?> pList)
	{
		for( Object element : pList )
		{
			aElements.add(element);
		}
	}
	
	@Override
	public Iterator<Object> iterator()
	{
		return aElements.iterator();
	}
	
	private void validateIndex(int pIndex)
	{
		if(pIndex < 0 || pIndex > aElements.size() -1 )
		{
			throw new JsonException("JsonArray index out of bounds");
		}
	}
	
	/**
	 * Get the object value associated with an index.
	 *
	 * @param pIndex The index must be between 0 and length() - 1.
	 * @return The JSON value at this index.
	 * @throws JsonException If there is no value for the index.
	 */
	public Object get(int pIndex)
	{
		validateIndex(pIndex);
		return aElements.get(pIndex);
	}
	
	/**
	 * Get the int value associated with an index.
	 *
	 * @param pIndex The index must be between 0 and length() - 1.
	 * @return The int value at this index.
	 * @throws JsonException If there is no value for the index of if the 
	 * value is not an Integer
	 */
	public int getInt(int pIndex)
	{
		validateIndex(pIndex);
		return JsonValueValidator.asInt(get(pIndex));
	}
	
	/**
	 * Get the String value associated with an index.
	 *
	 * @param pIndex The index must be between 0 and length() - 1.
	 * @return The String value at this index.
	 * @throws JsonException If there is no value for the index of if the 
	 * value is not a String
	 */
	public String getString(int pIndex)
	{
		validateIndex(pIndex);
		return JsonValueValidator.asString(get(pIndex));
	}
	
	/**
	 * Get the boolean value associated with an index.
	 *
	 * @param pIndex The index must be between 0 and length() - 1.
	 * @return The boolean value at this index.
	 * @throws JsonException If there is no value for the index of if the 
	 * value is not a Boolean
	 */
	public boolean getBoolean(int pIndex)
	{
		validateIndex(pIndex);
		return JsonValueValidator.asBoolean(get(pIndex));
	}
	
	/**
	 * Get the JsonObject associated with an index.
	 *
	 * @param pIndex The index must be between 0 and length() - 1.
	 * @return The JsonObject value at this index.
	 * @throws JsonException If there is no value for the index of if the 
	 * value is not a JsonObject
	 */
	public JsonObject getJsonObject(int pIndex)
	{
		validateIndex(pIndex);
		return JsonValueValidator.asJsonObject(get(pIndex));
	}
	
	/**
	 * Get the JsonArray associated with an index.
	 *
	 * @param pIndex The index must be between 0 and length() - 1.
	 * @return The JsonArray value at this index.
	 * @throws JsonException If there is no value for the index of if the 
	 * value is not a JsonArray
	 */
	public JsonArray getJsonArray(int pIndex)
	{
		validateIndex(pIndex);
		return JsonValueValidator.asJsonArray(get(pIndex));
	}
	
	/**
	 * Get the number of elements in the JSONArray, included nulls.
	 *
	 * @return The length (or size).
	 */
	public int size()
	{
		return aElements.size();
	}
	
	/// Processed are above
	
	/**
	 * Make a JSON text of this JSONArray. For compactness, no unnecessary
	 * whitespace is added. If it is not possible to produce a syntactically
	 * correct JSON text then null will be returned instead. This could occur if
	 * the array contains an invalid number.
	 * <p>
	 * <b> Warning: This method assumes that the data structure is acyclical.
	 * </b>
	 *
	 * @return a printable, displayable, transmittable representation of the
	 * array.
	 */
	@Override
	public String toString()
	{
		try
		{
			return this.toString(0);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * Make a pretty-printed JSON text of this JSONArray.
	 * 
	 * <p>
	 * If <code>indentFactor > 0</code> and the {@link JsonArray} has only one
	 * element, then the array will be output on a single line:
	 * 
	 * <pre>{@code [1]}</pre>
	 * 
	 * <p>
	 * If an array has 2 or more elements, then it will be output across
	 * multiple lines:
	 * 
	 * <pre>{@code
	 * [
	 * 1,
	 * "value 2",
	 * 3
	 * ]
	 * }</pre>
	 * <p>
	 * <b> Warning: This method assumes that the data structure is acyclical.
	 * </b>
	 * 
	 * @param indentFactor The number of spaces to add to each level of
	 * indentation.
	 * @return a printable, displayable, transmittable representation of the
	 * object, beginning with <code>[</code>&nbsp;<small>(left bracket)</small>
	 * and ending with <code>]</code> &nbsp;<small>(right bracket)</small>.
	 * @throws JsonException
	 */
	public String toString(int indentFactor) throws JsonException
	{
		StringWriter sw = new StringWriter();
		synchronized (sw.getBuffer())
		{
			this.write(sw, indentFactor, 0);
			return sw.toString();
		}
	}

	/**
	 * Write the contents of the JSONArray as JSON text to a writer.
	 * 
	 * <p>
	 * If <code>indentFactor > 0</code> and the {@link JsonArray} has only one
	 * element, then the array will be output on a single line:
	 * 
	 * <pre>{@code [1]}</pre>
	 * 
	 * <p>
	 * If an array has 2 or more elements, then it will be output across
	 * multiple lines:
	 * 
	 * <pre>{@code
	 * [
	 * 1,
	 * "value 2",
	 * 3
	 * ]
	 * }</pre>
	 * <p>
	 * <b> Warning: This method assumes that the data structure is acyclical.
	 * </b>
	 *
	 * @param writer Writes the serialized JSON
	 * @param indentFactor The number of spaces to add to each level of
	 * indentation.
	 * @param indent The indentation of the top level.
	 * @return The writer.
	 * @throws JsonException
	 */
	public void write(final Writer writer, int indentFactor, int indent) throws JsonException
	{
		try
		{
			boolean commanate = false;
			int length = this.size();
			writer.write('[');

			if( length == 1 )
			{
				try
				{
					JsonObject.writeValue(writer, this.aElements.get(0), indentFactor, indent);
				}
				catch (Exception e)
				{
					throw new JsonException("Unable to write JSONArray value at index: 0", e);
				}
			}
			else if( length != 0 )
			{
				final int newindent = indent + indentFactor;

				for( int i = 0; i < length; i += 1 )
				{
					if( commanate )
					{
						writer.write(',');
					}
					if( indentFactor > 0 )
					{
						writer.write('\n');
					}
					JsonObject.indent(writer, newindent);
					try
					{
						JsonObject.writeValue(writer, this.aElements.get(i), indentFactor, newindent);
					}
					catch (Exception e)
					{
						throw new JsonException("Unable to write JSONArray value at index: " + i, e);
					}
					commanate = true;
				}
				if( indentFactor > 0 )
				{
					writer.write('\n');
				}
				JsonObject.indent(writer, indent);
			}
			writer.write(']');
		}
		catch (IOException e)
		{
			throw new JsonException(e);
		}
	}
}
