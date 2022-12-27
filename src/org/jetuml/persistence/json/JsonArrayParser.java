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

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Parses arrays in JSON document according to the ECMA-404 2nd edition December
 * 2017. Also provides support for writing strings in JSON standard.
 */
final class JsonArrayParser implements JsonValueParser
{
	private static final JsonAnyValueParser VALUE_PARSER = new JsonAnyValueParser();

	private static final char CHAR_START_ARRAY = '[';
	private static final char CHAR_END_ARRAY = ']';
	private static final char CHAR_COMMA = ',';

	@Override
	public boolean isApplicable(ParsableCharacterBuffer pInput)
	{
		return pInput.isNext(CHAR_START_ARRAY);
	}

	@Override
	public JsonArray parse(ParsableCharacterBuffer pInput)
	{
		List<Object> values = new ArrayList<>();

		pInput.consume(CHAR_START_ARRAY);

		while (true)
		{
			pInput.skipBlanks();
			if( pInput.isNext(CHAR_END_ARRAY) )
			{
				pInput.consume(CHAR_END_ARRAY);
				return new JsonArray(values);
			}
			if( values.size() > 0 )
			{
				pInput.consume(CHAR_COMMA);
				pInput.skipBlanks();
			}
			values.add(VALUE_PARSER.parse(pInput));
		}
	}

	/**
	 * Serializes this array into its standard JSON notation.
	 * 
	 * @param pArray The array to serialize.
	 * @return The serialized array.
	 * @throws JsonException if pArray is not an instance of JsonArray.
	 */
	static String writeJsonArray(Object pArray)
	{
		StringJoiner result = new StringJoiner("" + CHAR_COMMA);
		for( Object value : JsonValueValidator.asJsonArray(pArray) )
		{
			result.add(JsonWriter.write(value));
		}
		return CHAR_START_ARRAY + result.toString() + CHAR_END_ARRAY;
	}
}
