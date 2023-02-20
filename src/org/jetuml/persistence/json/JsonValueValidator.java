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

import java.util.Set;

/**
 * Utility methods to uniformly validate and access
 * Object instances as Json values.
 */
final class JsonValueValidator
{
	private static final Set<Class<?>> VALID_VALUE_TYPES = Set.of(
			String.class,
			Boolean.class,
			Integer.class,
			JsonObject.class,
			JsonArray.class);
	
	private JsonValueValidator() {}
	
	/**
	 * Validates that pValue is a non-null instance that represents a valid
	 * JSON value: String, Boolean, Integer, JsonObject, or JsonArray.
	 * 
	 * @param pValue The value to check. If null, throws a JsonException
	 * @throws JsonException is pValue is null or an instance of an object
	 * not recognized as a valid JSON value.
	 */
	static void validateType(Object pValue)
	{
		if( pValue == null )
		{
			throw new JsonException("Null value");
		}
		if( !VALID_VALUE_TYPES.contains(pValue.getClass()) )
		{
			throw new JsonException("Invalid value type: " + pValue.getClass().getSimpleName());
		}
	}
	
	/**
	 * @param pValue The value to obtain as an int.
	 * @return The value as an int
	 * @throws JsonException if pValue is null or not an Integer.
	 */
	static int asInt(Object pValue)
	{
		if( pValue.getClass() != Integer.class )
		{
			throw new JsonException(String.format("Attempting to retrieve an %s as an int",
					pValue.getClass().getSimpleName()));
		}
		return (int) pValue;
	}
	
	/**
	 * @param pValue The value to obtain as a String.
	 * @return The value as a String.
	 * @throws JsonException if pValue is null or not an String.
	 */
	static String asString(Object pValue)
	{
		if( pValue.getClass() != String.class )
		{
			throw new JsonException(String.format("Attempting to retrieve an %s as a String",
					pValue.getClass().getSimpleName()));
		}
		return (String) pValue;
	}
	
	/**
	 * @param pValue The value to obtain as a boolean.
	 * @return The value as a boolean.
	 * @throws JsonException if pValue is null or not a Boolean.
	 */
	static boolean asBoolean(Object pValue)
	{
		if( pValue.getClass() != Boolean.class )
		{
			throw new JsonException(String.format("Attempting to retrieve an %s as a boolean",
					pValue.getClass().getSimpleName()));
		}
		return (boolean) pValue;
	}
	
	/**
	 * @param pValue The value to obtain as a JsonObject.
	 * @return The value as a JsonObject.
	 * @throws JsonException if pValue is null or not a JsonObject.
	 */
	static JsonObject asJsonObject(Object pValue)
	{
		if( pValue.getClass() != JsonObject.class )
		{
			throw new JsonException(String.format("Attempting to retrieve an %s as a JsonObject",
					pValue.getClass().getSimpleName()));
		}
		return (JsonObject) pValue;
	}
	
	/**
	 * @param pValue The value to obtain as a JsonArray.
	 * @return The value as a JsonArray.
	 * @throws JsonException if pValue is null or not a JsonArray.
	 */
	static JsonArray asJsonArray(Object pValue)
	{
		if( pValue.getClass() != JsonArray.class )
		{
			throw new JsonException(String.format("Attempting to retrieve an %s as a JsonArray",
					pValue.getClass().getSimpleName()));
		}
		return (JsonArray) pValue;
	}
}
