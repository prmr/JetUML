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
