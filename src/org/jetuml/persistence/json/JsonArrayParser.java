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
