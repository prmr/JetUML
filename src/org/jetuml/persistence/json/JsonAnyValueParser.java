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
