package org.jetuml.persistence.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
	 * @param pValue A non-null Json value.
 	 * @throws JsonException If the name is null or if the value is not of a valid Json value.
	 */
	public void add(Object pValue)
	{
		aElements.add(pValue);
	}
	
	/// Processed are above
	
	

	/**
	 * Construct a JSONArray from a Collection. Assumes all the values are legal
	 * Json values
	 *
	 * @param collection A Collection.
	 */
	public JsonArray(Collection<?> collection)
	{
		for( Object o : collection )
		{
			aElements.add(o);
		}
	}

	/**
	 * Construct a JSONArray from an array
	 *
	 * @throws JsonException If not an array.
	 */
	// TODO Remove
	public JsonArray(Object array) throws JsonException
	{
		this();
		if( array.getClass().isArray() )
		{
			int length = Array.getLength(array);
			this.aElements.ensureCapacity(length);
			for( int i = 0; i < length; i += 1 )
			{
				this.add(Array.get(array, i));
			}
		}
		else
		{
			throw new JsonException("JSONArray initial value should be a string or collection or array.");
		}
	}

	@Override
	public Iterator<Object> iterator()
	{
		return aElements.iterator();
	}

	/**
	 * Get the object value associated with an index.
	 *
	 * @param index The index must be between 0 and length() - 1.
	 * @return An object value.
	 * @throws JsonException If there is no value for the index.
	 */
	public Object get(int index) throws JsonException
	{
		Object object = this.opt(index);
		if( object == null )
		{
			throw new JsonException("JSONArray[" + index + "] not found.");
		}
		return object;
	}

	/**
	 * Get the int value associated with an index.
	 *
	 * @param index The index must be between 0 and length() - 1.
	 * @return The value.
	 * @throws JsonException If the key is not found or if the value is not a
	 * number.
	 */
	public int getInt(int index) throws JsonException
	{
		Object object = this.get(index);
		try
		{
			return object instanceof Number ? ((Number) object).intValue() : Integer.parseInt((String) object);
		}
		catch (Exception e)
		{
			throw new JsonException("JSONArray[" + index + "] is not a number.", e);
		}
	}

	/**
	 * Get the JSONObject associated with an index.
	 *
	 * @param index subscript
	 * @return A JSONObject value.
	 * @throws JsonException If there is no value for the index or if the value
	 * is not a JSONObject
	 */
	public JsonObject getJSONObject(int index) throws JsonException
	{
		Object object = this.get(index);
		if( object instanceof JsonObject )
		{
			return (JsonObject) object;
		}
		throw new JsonException("JSONArray[" + index + "] is not a JSONObject.");
	}

	/**
	 * Get the string associated with an index.
	 *
	 * @param index The index must be between 0 and length() - 1.
	 * @return A string value.
	 * @throws JsonException If there is no string value for the index.
	 */
	public String getString(int index) throws JsonException
	{
		Object object = this.get(index);
		if( object instanceof String )
		{
			return (String) object;
		}
		throw new JsonException("JSONArray[" + index + "] not a string.");
	}

	/**
	 * Get the number of elements in the JSONArray, included nulls.
	 *
	 * @return The length (or size).
	 */
	public int length()
	{
		return aElements.size();
	}

	/**
	 * Get the optional object value associated with an index.
	 *
	 * @param index The index must be between 0 and length() - 1. If not, null
	 * is returned.
	 * @return An object value, or null if there is no object at that index.
	 */
	public Object opt(int index)
	{
		return (index < 0 || index >= this.length()) ? null : this.aElements.get(index);
	}

	/**
	 * Append an int value. This increases the array's length by one.
	 *
	 * @param value An int value.
	 * @return this.
	 */
	public JsonArray put(int value)
	{
		this.add(Integer.valueOf(value));
		return this;
	}

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
			int length = this.length();
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
