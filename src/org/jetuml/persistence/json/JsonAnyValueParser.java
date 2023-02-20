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

import java.util.List;

/**
 * Parses any value in a JSON document according to the ECMA-404 2nd
 * edition December 2017.
 */
final class JsonAnyValueParser implements JsonValueParser
{
	private static final List<JsonValueParser> PARSERS = List.of(
			new JsonStringParser(),
			new JsonBooleanParser(),
			new JsonIntegerParser(),
			new JsonObjectParser(),
			new JsonArrayParser()
			);
	
	@Override
	public boolean isApplicable(ParsableCharacterBuffer pInput)
	{
		return PARSERS.stream()
			.anyMatch(parser -> parser.isApplicable(pInput));
    }
    
	@Override
	public Object parse(ParsableCharacterBuffer pInput)
	{
		for( JsonValueParser parser : PARSERS )
		{
			if( parser.isApplicable(pInput))
			{
				return parser.parse(pInput);
			}
		}
		throw new JsonParsingException(pInput.position());
	}
}
