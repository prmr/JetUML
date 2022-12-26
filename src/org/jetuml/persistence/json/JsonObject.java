package org.jetuml.persistence.json;

import static org.jetuml.persistence.json.JsonValueValidator.validateType;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A JSONObject is an unordered collection of name/value pairs referred to as
 * "properties". Its external form is a string wrapped in curly braces with
 * colons between the names and values, and commas between the values and names.
 * The values can be any of these types: Boolean, JsonArray, JsonObject,
 * Integer, String. Null values and non-integer number formats are not supported
 * by this implementation.
 * 
 * Any attempt at an illegal operation will raise a JsonException.
 * 
 * As an additional robustness feature, this class makes not type conversion.
 * This means that values can only be obtained in the same type as they were put
 * in. For example, putting a property "size" as a string will store it
 * internally as a string, and attempting to retrieve it as an int will throw a
 * JsonException.
 */
public class JsonObject
{
	/**
	 * JSONObject.NULL is equivalent to the value that JavaScript calls null,
	 * whilst Java's null is equivalent to the value that JavaScript calls
	 * undefined.
	 */
	private static final class Null
	{

		/**
		 * There is only intended to be a single instance of the NULL object, so
		 * the clone method returns itself.
		 *
		 * @return NULL.
		 */
		@Override
		protected final Object clone()
		{
			return this;
		}

		/**
		 * A Null object is equal to the null value and to itself.
		 *
		 * @param object An object to test for nullness.
		 * @return true if the object parameter is the JSONObject.NULL object or
		 * null.
		 */
		@Override
		public boolean equals(Object object)
		{
			return object == null || object == this;
		}

		/**
		 * A Null object is equal to the null value and to itself.
		 *
		 * @return always returns 0.
		 */
		@Override
		public int hashCode()
		{
			return 0;
		}

		/**
		 * Get the "null" string value.
		 *
		 * @return The string "null".
		 */
		@Override
		public String toString()
		{
			return "null";
		}
	}

	/*
	 * HashMap is used on purpose to ensure that elements are unordered by the
	 * specification. JSON tends to be a portable transfer format to allows the
	 * container implementations to rearrange their items for a faster element
	 * retrieval based on associative access. Therefore, an implementation
	 * mustn't rely on the order of the item.
	 */
	private Map<String, Object> aProperties = new HashMap<>();

	/**
	 * It is sometimes more convenient and less ambiguous to have a
	 * <code>NULL</code> object than to use Java's <code>null</code> value.
	 * <code>JSONObject.NULL.equals(null)</code> returns <code>true</code>.
	 * <code>JSONObject.NULL.toString()</code> returns <code>"null"</code>.
	 */
	public static final Object NULL = new Null();

	private void validateProperty(String pName)
	{
		if( pName == null )
		{
			throw new JsonException("Property name cannot be null");
		}
		if( !aProperties.containsKey(pName) )
		{
			throw new JsonException("Property " + pName + " not found");
		}
	}

	private void validateNotNull(String pName, Object pValue)
	{
		if(pName == null)
		{
			throw new JsonException("Null property name");
		}
		if(pValue == null)
		{
			throw new JsonException("Null property value");
		}
	}
	
	/**
	 * Construct a JsonObject with no property.
	 */
	public JsonObject()
	{
	}

	/**
	 * Get the value associated with a name.
	 *
	 * @param pName The property name
	 * @return The property value.
	 * @throws JsonException If the name is null or the property is not found.
	 */
	public Object get(String pName)
	{
		validateProperty(pName);
		return aProperties.get(pName);
	}

	/**
	 * Get a property value as an int.
	 *
	 * @param pName The property name
	 * @return The integer value.
	 * @throws JsonException if the key is null or not found or if the value was
	 * not originally stored as an integer.
	 */
	public int getInt(String pName)
	{
		validateProperty(pName);
		return JsonValueValidator.asInt(get(pName));
	}

	/**
	 * Get a property value as a JsonArray.
	 *
	 * @param pName The property name
	 * @return The JsonArray value.
	 * @throws JsonException if the key is null or not found or if the value was
	 * not originally stored as a JsonArray.
	 */
	public JsonArray getJsonArray(String pName)
	{
		validateProperty(pName);
		return JsonValueValidator.asJsonArray(get(pName));
	}
	
	/**
	 * Get a property value as a JsonObject.
	 *
	 * @param pName The property name
	 * @return The JsonObject value.
	 * @throws JsonException if the key is null or not found or if the value was
	 * not originally stored as a JsonObject.
	 */
	public JsonObject getJsonObject(String pName)
	{
		validateProperty(pName);
		return JsonValueValidator.asJsonObject(get(pName));
	}

	/**
	 * Get a property value as a String.
	 *
	 * @param pName The property name
	 * @return The String value.
	 * @throws JsonException if the key is null or not found or if the value was
	 * not originally stored as a String.
	 */
	public String getString(String pName)
	{
		validateProperty(pName);
		return JsonValueValidator.asString(get(pName));
	}
	
	/**
	 * Get a property value as a boolean.
	 *
	 * @param pName The property name
	 * @return The boolean value.
	 * @throws JsonException if the key is null or not found or if the value was
	 * not originally stored as a boolean.
	 */
	public boolean getBoolean(String pName)
	{
		validateProperty(pName);
		return JsonValueValidator.asBoolean(get(pName));
	}

	/**
	 * Determine if this object contains a given property.
	 *
	 * @param pName The property name. Null is accepted but 
	 * will always return false as it is not possible for
	 * an object of this class to store a property with a null name.
	 * @return true if the property exists in this object.
	 */
	public boolean hasProperty(String pName)
	{
		return aProperties.containsKey(pName);
	}

	/**
	 * Get the number of properties stored by this object.
	 *
	 * @return The number of properties in the object.
	 */
	public int numberOfProperties()
	{
		return aProperties.size();
	}

	/**
	 * Adds a property to this object. Overrides any previous value associated with
	 * the property name.
	 *
	 * @param pName The name of the property. Should not be null.
	 * @param pValue The value of the property. It should not be null and be of one of these
	 * types: boolean/Boolean, int/Integer, JsonArray, JsonObject, or String.
	 * @throws JsonException If the name is null or if the value is not of a valid Json value.
	 */
	public void put(String pName, Object pValue)
	{
		validateNotNull(pName, pValue);
		validateType(pValue);
		aProperties.put(pName, pValue);
	}

	/*
	 * Converts the input string to a version in double quotes with backslash
	 * sequences in all the right places. A backslash will be inserted within
	 * </, producing <\/, allowing JSON text to be delivered in HTML. In JSON
	 * text, a string cannot contain a control character or an unescaped quote
	 * or backslash.
	 *
	 * @param string A String
	 * 
	 * @return A String correctly formatted for insertion in a JSON text.
	 */
	private static String quote(String string)
	{
		StringWriter sw = new StringWriter();
		synchronized (sw.getBuffer())
		{
			try
			{
				quote(string, sw);
				return sw.toString();
			}
			catch (IOException ignored)
			{
				// will never happen - we are writing to a string writer
				return "";
			}
		}
	}

	private static void quote(String string, final Writer pWriter) throws IOException
	{
		if (string == null || string.length() == 0)
		{
			pWriter.write("\"\"");
			return;
		}

		char b;
		char c = 0;
		String hhhh;
		int i;
		int len = string.length();

		pWriter.write('"');
		for (i = 0; i < len; i += 1)
		{
			b = c;
			c = string.charAt(i);
			switch (c)
			{
			case '\\':
			case '"':
				pWriter.write('\\');
				pWriter.write(c);
				break;
			case '/':
				if (b == '<')
				{
					pWriter.write('\\');
				}
				pWriter.write(c);
				break;
			case '\b':
				pWriter.write("\\b");
				break;
			case '\t':
				pWriter.write("\\t");
				break;
			case '\n':
				pWriter.write("\\n");
				break;
			case '\f':
				pWriter.write("\\f");
				break;
			case '\r':
				pWriter.write("\\r");
				break;
			default:
				if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100'))
				{
					pWriter.write("\\u");
					hhhh = Integer.toHexString(c);
					pWriter.write("0000", 0, 4 - hhhh.length());
					pWriter.write(hhhh);
				}
				else
				{
					pWriter.write(c);
				}
			}
		}
		pWriter.write('"');
	}

	/**
	 * Throw an exception if the object is a NaN or infinite number.
	 *
	 * @param o The object to test.
	 * @throws JsonException If o is a non-finite number.
	 */
	private static void testValidity(Object o)
	{
		if (o != null)
		{
			if (o instanceof Double)
			{
				if (((Double) o).isInfinite() || ((Double) o).isNaN())
				{
					throw new JsonException("JSON does not allow non-finite numbers.");
				}
			}
			else if (o instanceof Float)
			{
				if (((Float) o).isInfinite() || ((Float) o).isNaN())
				{
					throw new JsonException("JSON does not allow non-finite numbers.");
				}
			}
		}
	}

	/**
	 * Make a JSON text of this JSONObject. For compactness, no whitespace is
	 * added. If this would not result in a syntactically correct JSON text,
	 * then null will be returned instead.
	 * <p>
	 * <b> Warning: This method assumes that the data structure is acyclical.
	 * </b>
	 * 
	 * @return a printable, displayable, portable, transmittable representation
	 * of the object, beginning with <code>{</code>&nbsp;<small>(left
	 * brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 * brace)</small>.
	 */
	@Override
	public String toString()
	{
		try
		{
			return toString(0);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * Make a pretty-printed JSON text of this JSONObject.
	 * 
	 * <p>
	 * If <code>indentFactor > 0</code> and the {@link JsonObject} has only one
	 * key, then the object will be output on a single line:
	 * 
	 * <pre>{@code {"key": 1}}</pre>
	 * 
	 * <p>
	 * If an object has 2 or more keys, then it will be output across multiple
	 * lines: <code><pre>{
	 *  "key1": 1,
	 *  "key2": "value 2",
	 *  "key3": 3
	 * }</pre></code>
	 * <p>
	 * <b> Warning: This method assumes that the data structure is acyclical.
	 * </b>
	 *
	 * @param indentFactor The number of spaces to add to each level of
	 * indentation.
	 * @return a printable, displayable, portable, transmittable representation
	 * of the object, beginning with <code>{</code>&nbsp;<small>(left
	 * brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 * brace)</small>.
	 * @throws JsonException If the object contains an invalid number.
	 */
	public String toString(int indentFactor)
	{
		StringWriter w = new StringWriter();
		synchronized (w.getBuffer())
		{
			this.write(w, indentFactor, 0);
			return w.toString();
		}
	}

	static final void writeValue(Writer writer, Object value, int indentFactor, int indent) throws IOException
	{
		if (value == null || value.equals(null))
		{
			writer.write("null");
		}
		else if (value instanceof Number)
		{
			// not all Numbers may match actual JSON Numbers. i.e. fractions or
			// Imaginary
			final String numberAsString = numberToString((Number) value);
			try
			{
				// Use the BigDecimal constructor for it's parser to validate
				// the format.
				@SuppressWarnings("unused")
				BigDecimal testNum = new BigDecimal(numberAsString);
				// Close enough to a JSON number that we will use it unquoted
				writer.write(numberAsString);
			}
			catch (NumberFormatException ex)
			{
				// The Number value is not a valid JSON number.
				// Instead we will quote it as a string
				quote(numberAsString, writer);
			}
		}
		else if (value instanceof Boolean)
		{
			writer.write(value.toString());
		}
		else if (value instanceof Enum<?>)
		{
			writer.write(quote(((Enum<?>) value).name()));
		}
		else if (value instanceof JsonObject)
		{
			((JsonObject) value).write(writer, indentFactor, indent);
		}
		else if (value instanceof JsonArray)
		{
			((JsonArray) value).write(writer, indentFactor, indent);
		}
		else if (value instanceof Collection)
		{
			Collection<?> coll = (Collection<?>) value;
			new JsonArray(coll).write(writer, indentFactor, indent);
		}
		else if (value.getClass().isArray())
		{
			new JsonArray(value).write(writer, indentFactor, indent);
		}
		else
		{
			quote(value.toString(), writer);
		}
	}

	static final void indent(Writer writer, int indent) throws IOException
	{
		for (int i = 0; i < indent; i += 1)
		{
			writer.write(' ');
		}
	}

	/**
	 * Write the contents of the JSONObject as JSON text to a writer.
	 * 
	 * <p>
	 * If <code>indentFactor > 0</code> and the {@link JsonObject} has only one
	 * key, then the object will be output on a single line:
	 * 
	 * <pre>{@code {"key": 1}}</pre>
	 * 
	 * <p>
	 * If an object has 2 or more keys, then it will be output across multiple
	 * lines: <code><pre>{
	 *  "key1": 1,
	 *  "key2": "value 2",
	 *  "key3": 3
	 * }</pre></code>
	 * <p>
	 * <b> Warning: This method assumes that the data structure is acyclical.
	 * </b>
	 *
	 * @param writer Writes the serialized JSON
	 * @param indentFactor The number of spaces to add to each level of
	 * indentation.
	 * @param indent The indentation of the top level.
	 * @throws JsonException
	 */
	public void write(Writer writer, int indentFactor, int indent)
	{
		try
		{
			boolean commanate = false;
			final int length = this.numberOfProperties();
			writer.write('{');

			if (length == 1)
			{
				final Entry<String, ?> entry = aProperties.entrySet().iterator().next();
				final String key = entry.getKey();
				writer.write(quote(key));
				writer.write(':');
				if (indentFactor > 0)
				{
					writer.write(' ');
				}
				try
				{
					writeValue(writer, entry.getValue(), indentFactor, indent);
				}
				catch (Exception e)
				{
					throw new JsonException("Unable to write JSONObject value for key: " + key, e);
				}
			}
			else if (length != 0)
			{
				final int newindent = indent + indentFactor;
				for (final Entry<String, ?> entry : aProperties.entrySet())
				{
					if (commanate)
					{
						writer.write(',');
					}
					if (indentFactor > 0)
					{
						writer.write('\n');
					}
					indent(writer, newindent);
					final String key = entry.getKey();
					writer.write(quote(key));
					writer.write(':');
					if (indentFactor > 0)
					{
						writer.write(' ');
					}
					try
					{
						writeValue(writer, entry.getValue(), indentFactor, newindent);
					}
					catch (Exception e)
					{
						throw new JsonException("Unable to write JSONObject value for key: " + key, e);
					}
					commanate = true;
				}
				if (indentFactor > 0)
				{
					writer.write('\n');
				}
				indent(writer, indent);
			}
			writer.write('}');
		}
		catch (IOException exception)
		{
			throw new JsonException(exception);
		}
	}

	/*
	 * Produce a string from a Number.
	 *
	 * @param number A Number
	 * @return A String.
	 * @throws JsonException If n is a non-finite number.
	 */
	private static String numberToString(Number number)
	{
		if (number == null)
		{
			throw new JsonException("Null pointer");
		}
		testValidity(number);

		// Shave off trailing zeros and decimal point, if possible.

		String string = number.toString();
		if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0)
		{
			while (string.endsWith("0"))
			{
				string = string.substring(0, string.length() - 1);
			}
			if (string.endsWith("."))
			{
				string = string.substring(0, string.length() - 1);
			}
		}
		return string;
	}

}
