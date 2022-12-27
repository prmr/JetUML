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

import java.util.StringJoiner;

/**
 * Parses objects in JSON document according to the ECMA-404 2nd
 * edition December 2017. Also provides support for writing JsonObjects 
 * in JSON text.
 */
final class JsonObjectParser implements JsonValueParser
{
	private static final JsonStringParser STRING_PARSER = new JsonStringParser();
	private static final JsonAnyValueParser VALUE_PARSER = new JsonAnyValueParser();
	
	private static final char CHAR_START_OBJECT = '{';
	private static final char CHAR_END_OBJECT = '}';
	private static final char CHAR_COMMA = ',';
	private static final char CHAR_COLON = ':';
	
	@Override
	public boolean isApplicable(ParsableCharacterBuffer pInput)
	{
		return pInput.isNext(CHAR_START_OBJECT);
	}

	@Override
	public JsonObject parse(ParsableCharacterBuffer pInput)
	{
		JsonObject object = new JsonObject();
        
		pInput.consume(CHAR_START_OBJECT);
			
        while(true)
		{
        	pInput.skipBlanks();
            if( pInput.isNext(CHAR_END_OBJECT))
            {
            	pInput.consume(CHAR_END_OBJECT);
            	return object;
            }
            
            if(object.numberOfProperties() > 0)
            {
            	pInput.consume(CHAR_COMMA);
            	pInput.skipBlanks();
            }
       
        	String key = STRING_PARSER.parse(pInput);
        	pInput.skipBlanks();
        	pInput.consume(CHAR_COLON);
        	pInput.skipBlanks();
			Object value = VALUE_PARSER.parse(pInput);
			if( object.hasProperty(key))
			{
				throw new JsonParsingException(pInput.position());
			}
			else
			{
				object.put(key, value);
			}
		}
	}
	
	/**
	 * Serializes this object into its standard JSON notation.
	 * 
	 * @param pObject The object to serialize.
	 * @return The serialized object.
	 * @throws JsonException if pObject is not an instance of JsonObject.
	 */
	static String writeJsonObject(Object pObject)
	{
		JsonObject jsonObject = JsonValueValidator.asJsonObject(pObject);
		StringJoiner result = new StringJoiner("" + CHAR_COMMA);
		for(String property : jsonObject.properties() )
		{
			result.add(JsonStringParser.writeJsonString(property) + CHAR_COLON + JsonWriter.write(jsonObject.get(property)));
		}
		return CHAR_START_OBJECT + result.toString() + CHAR_END_OBJECT;
	}
}
