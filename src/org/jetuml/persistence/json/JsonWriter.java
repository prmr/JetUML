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

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * An object able to write a JSON value to its String representation.
 */
public final class JsonWriter
{
	private static final Map<Class<?>, Function<Object, String>> WRITERS = new IdentityHashMap<>();
	
	static
	{
		WRITERS.put(Boolean.class, Object::toString);
		WRITERS.put(Integer.class, Object::toString);
		WRITERS.put(String.class, JsonStringParser::writeJsonString);
		WRITERS.put(JsonObject.class, JsonObjectParser::writeJsonObject);
		WRITERS.put(JsonArray.class, JsonArrayParser::writeJsonArray);
	}
	
	/**
	 * @param pJsonValue A value to serialize.
	 * @return A serialized version of the input.
	 * @throws JsonException if pJsonValue is null or not a reference 
	 * to a valid instance of a JSON value.
	 */
	public static String write(Object pJsonValue)
	{
		JsonValueValidator.validateType(pJsonValue);
		return WRITERS.get(pJsonValue.getClass()).apply(pJsonValue);
	}
}
