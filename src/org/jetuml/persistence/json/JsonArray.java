package org.jetuml.persistence.json;

/*
 Copyright (c) 2002 JSON.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 The Software shall be used for Good, not Evil.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A JSONArray is an ordered sequence of values. Its external text form is a
 * string wrapped in square brackets with commas separating the values. The
 * internal form is an object having <code>get</code> and <code>opt</code>
 * methods for accessing the values by index, and <code>put</code> methods for
 * adding or replacing values. The values can be any of these types:
 * <code>Boolean</code>, <code>JSONArray</code>, <code>JSONObject</code>,
 * <code>Number</code>, <code>String</code>, or the
 * <code>JSONObject.NULL object</code>.
 * <p>
 * The constructor can convert a JSON text into a Java object. The
 * <code>toString</code> method converts to JSON text.
 * <p>
 * A <code>get</code> method returns a value if one can be found, and throws an
 * exception if one cannot be found. An <code>opt</code> method returns a
 * default value instead of throwing an exception, and so is useful for
 * obtaining optional values.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return an
 * object which you can cast or query for type. There are also typed
 * <code>get</code> and <code>opt</code> methods that do type checking and type
 * coercion for you.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly conform to
 * JSON syntax rules. The constructors are more forgiving in the texts they will
 * accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just
 * before the closing bracket.</li>
 * <li>The <code>null</code> value will be inserted when there is <code>,</code>
 * &nbsp;<small>(comma)</small> elision.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single
 * quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a quote
 * or single quote, and if they do not contain leading or trailing spaces, and
 * if they do not contain any of these characters:
 * <code>{ } [ ] / \ : , #</code> and if they do not look like numbers and
 * if they are not the reserved words <code>true</code>, <code>false</code>, or
 * <code>null</code>.</li>
 * </ul>
 *
 * @author JSON.org
 * @version 2016-08/15
 */
public class JsonArray implements Iterable<Object>
{
    private final ArrayList<Object> aElements;

    /**
     * Construct an empty JSONArray.
     */
    public JsonArray() 
    {
        aElements = new ArrayList<>();
    }

    /**
     * Construct a JSONArray from a Collection.
     * Assumes all the values are legal Json values
     *
     * @param collection
     *            A Collection.
     */
    public JsonArray(Collection<?> collection) {
        if (collection == null) {
            this.aElements = new ArrayList<>();
        } else {
            this.aElements = new ArrayList<>(collection.size());
        	for (Object o: collection){
        		this.aElements.add(o);
        	}
        }
    }

    /**
     * Construct a JSONArray from an array
     *
     * @throws JsonException
     *             If not an array.
     */
    // TODO Remove
    public JsonArray(Object array) throws JsonException {
        this();
        if (array.getClass().isArray()) {
            int length = Array.getLength(array);
            this.aElements.ensureCapacity(length);
            for (int i = 0; i < length; i += 1) {
                this.put(Array.get(array, i));
            }
        } else {
            throw new JsonException(
                    "JSONArray initial value should be a string or collection or array.");
        }
    }

    @Override
    public Iterator<Object> iterator() {
        return this.aElements.iterator();
    }

    /**
     * Get the object value associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return An object value.
     * @throws JsonException
     *             If there is no value for the index.
     */
    public Object get(int index) throws JsonException {
        Object object = this.opt(index);
        if (object == null) {
            throw new JsonException("JSONArray[" + index + "] not found.");
        }
        return object;
    }

    /**
     * Get the int value associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JsonException
     *             If the key is not found or if the value is not a number.
     */
    public int getInt(int index) throws JsonException {
        Object object = this.get(index);
        try {
            return object instanceof Number ? ((Number) object).intValue()
                    : Integer.parseInt((String) object);
        } catch (Exception e) {
            throw new JsonException("JSONArray[" + index + "] is not a number.", e);
        }
    }

    /**
     * Get the JSONObject associated with an index.
     *
     * @param index
     *            subscript
     * @return A JSONObject value.
     * @throws JsonException
     *             If there is no value for the index or if the value is not a
     *             JSONObject
     */
    public JsonObject getJSONObject(int index) throws JsonException {
        Object object = this.get(index);
        if (object instanceof JsonObject) {
            return (JsonObject) object;
        }
        throw new JsonException("JSONArray[" + index + "] is not a JSONObject.");
    }

    /**
     * Get the string associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return A string value.
     * @throws JsonException
     *             If there is no string value for the index.
     */
    public String getString(int index) throws JsonException {
        Object object = this.get(index);
        if (object instanceof String) {
            return (String) object;
        }
        throw new JsonException("JSONArray[" + index + "] not a string.");
    }

    /**
     * Get the number of elements in the JSONArray, included nulls.
     *
     * @return The length (or size).
     */
    public int length() {
        return this.aElements.size();
    }

    /**
     * Get the optional object value associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1. If not, null is returned.
     * @return An object value, or null if there is no object at that index.
     */
    public Object opt(int index) {
        return (index < 0 || index >= this.length()) ? null : this.aElements
                .get(index);
    }

     /**
     * Append an int value. This increases the array's length by one.
     *
     * @param value
     *            An int value.
     * @return this.
     */
    public JsonArray put(int value) {
        this.put(Integer.valueOf(value));
        return this;
    }

    /**
     * Append an object value. This increases the array's length by one.
     *
     * @param value
     *            An object value. The value should be a Boolean, Double,
     *            Integer, JSONArray, JSONObject, Long, or String, or the
     *            JSONObject.NULL object.
     * @return this.
     */
    public JsonArray put(Object value) {
        this.aElements.add(value);
        return this;
    }

    /**
     * Make a JSON text of this JSONArray. For compactness, no unnecessary
     * whitespace is added. If it is not possible to produce a syntactically
     * correct JSON text then null will be returned instead. This could occur if
     * the array contains an invalid number.
     * <p><b>
     * Warning: This method assumes that the data structure is acyclical.
     * </b>
     *
     * @return a printable, displayable, transmittable representation of the
     *         array.
     */
    @Override
    public String toString() {
        try {
            return this.toString(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Make a pretty-printed JSON text of this JSONArray.
     * 
     * <p>If <code>indentFactor > 0</code> and the {@link JsonArray} has only
     * one element, then the array will be output on a single line:
     * <pre>{@code [1]}</pre>
     * 
     * <p>If an array has 2 or more elements, then it will be output across
     * multiple lines: <pre>{@code
     * [
     * 1,
     * "value 2",
     * 3
     * ]
     * }</pre>
     * <p><b>
     * Warning: This method assumes that the data structure is acyclical.
     * </b>
     * 
     * @param indentFactor
     *            The number of spaces to add to each level of indentation.
     * @return a printable, displayable, transmittable representation of the
     *         object, beginning with <code>[</code>&nbsp;<small>(left
     *         bracket)</small> and ending with <code>]</code>
     *         &nbsp;<small>(right bracket)</small>.
     * @throws JsonException
     */
    public String toString(int indentFactor) throws JsonException {
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
     * <p>If <code>indentFactor > 0</code> and the {@link JsonArray} has only
     * one element, then the array will be output on a single line:
     * <pre>{@code [1]}</pre>
     * 
     * <p>If an array has 2 or more elements, then it will be output across
     * multiple lines: <pre>{@code
     * [
     * 1,
     * "value 2",
     * 3
     * ]
     * }</pre>
     * <p><b>
     * Warning: This method assumes that the data structure is acyclical.
     * </b>
     *
     * @param writer
     *            Writes the serialized JSON
     * @param indentFactor
     *            The number of spaces to add to each level of indentation.
     * @param indent
     *            The indentation of the top level.
     * @return The writer.
     * @throws JsonException
     */
    public void write(final Writer writer, int indentFactor, int indent)
            throws JsonException {
        try {
            boolean commanate = false;
            int length = this.length();
            writer.write('[');

            if (length == 1) {
                try {
                    JsonObject.writeValue(writer, this.aElements.get(0),
                            indentFactor, indent);
                } catch (Exception e) {
                    throw new JsonException("Unable to write JSONArray value at index: 0", e);
                }
            } else if (length != 0) {
                final int newindent = indent + indentFactor;

                for (int i = 0; i < length; i += 1) {
                    if (commanate) {
                        writer.write(',');
                    }
                    if (indentFactor > 0) {
                        writer.write('\n');
                    }
                    JsonObject.indent(writer, newindent);
                    try {
                        JsonObject.writeValue(writer, this.aElements.get(i),
                                indentFactor, newindent);
                    } catch (Exception e) {
                        throw new JsonException("Unable to write JSONArray value at index: " + i, e);
                    }
                    commanate = true;
                }
                if (indentFactor > 0) {
                    writer.write('\n');
                }
                JsonObject.indent(writer, indent);
            }
            writer.write(']');
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }
}
