/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
 * 
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses.
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
