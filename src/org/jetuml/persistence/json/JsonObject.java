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

import static org.jetuml.persistence.json.JsonValueValidator.validateType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
	/*
	 * HashMap is used on purpose to ensure that elements are unordered by the
	 * specification. JSON tends to be a portable transfer format to allows the
	 * container implementations to rearrange their items for a faster element
	 * retrieval based on associative access. Therefore, an implementation
	 * mustn't rely on the order of the item.
	 */
	private Map<String, Object> aProperties = new HashMap<>();

	/**
	 * Construct a JsonObject with no property.
	 */
	public JsonObject() {}
	
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
	 * @return A set of all property names in this object.
	 */
	public Set<String> properties()
	{
		return aProperties.keySet();
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
		if(pName == null)
		{
			throw new JsonException("Null property name");
		}
		validateType(pValue);
		aProperties.put(pName, pValue);
	}

	/**
	 * Make a JSON text of this object. For compactness, no whitespace is
	 * added. This method assumes that the data structure is acyclical.
	 *
	 * @return A serialized version of this object.
	 */
	@Override
	public String toString()
	{
		return JsonObjectParser.writeJsonObject(this);
	}
}
