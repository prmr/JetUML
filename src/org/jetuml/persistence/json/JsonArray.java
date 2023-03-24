/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
 * 
 * See: https://github.com/prmr/JetUML
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.jetuml.persistence.json;

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
	
	/**
	 * Make a JSON text of this JsonArray. For compactness, no unnecessary
	 * whitespace is added. This method assumes that the data structure is acyclical.

	 * @return A serialized version of this array.
	 */
	@Override
	public String toString()
	{
		return JsonArrayParser.writeJsonArray(this);
	}
}
