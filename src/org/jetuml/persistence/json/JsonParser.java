/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
 * 
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.
 * 
 * The code in this class was originally based on JSON.org.
 * 
 ******************************************************************************/
package org.jetuml.persistence.json;

import java.util.ArrayList;
import java.util.List;

/**
 * An object to step through a string character by characters, with
 * the possibility of going backward. The JsonParser is not meant to 
 * be reused to parse different strings.
 * 
 * Null values are not supported.
 */
public class JsonParser 
{
	private static final JsonStringParser STRING_PARSER = new JsonStringParser();
	private static final JsonBooleanParser BOOLEAN_PARSER = new JsonBooleanParser();
	private static final JsonIntegerParser INTEGER_PARSER = new JsonIntegerParser();
	
	private static final char CHAR_COMMA = ',';
	private static final char CHAR_START_OBJECT = '{';
	private static final char CHAR_END_OBJECT = '}';
	private static final char CHAR_START_ARRAY = '[';
	private static final char CHAR_END_ARRAY = ']';
	private static final char CHAR_COLON = ':';
	
	/* Complete input to traverse. */
	private final ParsableCharacterBuffer aInput;
	
    /**
     * Constructs a new JsonParser initialized at the beginning of the input.
     *
     * @param pInput A string to use as a complete source of characters.
     * @pre pInput != null;
     */
    public JsonParser(String pInput) 
    {
    	assert pInput != null;
        aInput = new ParsableCharacterBuffer(pInput.trim());
    }
    
    /**
     * Get the next value. The value can be a Boolean, Double, Integer,
     * JSONArray, JSONObject, Long, or String, or the JSONObject.NULL object.
     * However, this implementation only support integers number formats.
     * @throws JsonException If syntax error.
     *
     * @return An object.
     */
    private Object nextValue()
    {
    	if(STRING_PARSER.isApplicable(aInput))
        {
        	return STRING_PARSER.parse(aInput);
        }
    	else if(aInput.isNext(CHAR_START_OBJECT))
        {
        	return parseObject();
        }
        else if(aInput.isNext(CHAR_START_ARRAY))
        {
        	return parseArray();
        }
        else if(BOOLEAN_PARSER.isApplicable(aInput))
        {
        	return BOOLEAN_PARSER.parse(aInput);
        }
        else if(INTEGER_PARSER.isApplicable(aInput))
        {
        	return INTEGER_PARSER.parse(aInput);
        }
        else
        {
        	throw new JsonParsingException(aInput.position());
        }
    }
    
    private JsonObject parseObject() 
    {
        JsonObject object = new JsonObject();
        
        aInput.consume(CHAR_START_OBJECT);
			
        while(true)
		{
        	aInput.skipBlanks();
            if( aInput.isNext(CHAR_END_OBJECT))
            {
            	aInput.consume(CHAR_END_OBJECT);
            	return object;
            }
            
            if(object.entrySet().size() > 0)
            {
            	aInput.consume(CHAR_COMMA);
            	aInput.skipBlanks();
            }
       
        	String key = STRING_PARSER.parse(aInput);
        	aInput.skipBlanks();
			aInput.consume(CHAR_COLON);
			aInput.skipBlanks();
			Object value = nextValue();
			if( object.has(key))
			{
				throw new JsonException("Duplicate key");
			}
			else
			{
				object.put(key, value);
			}
		}
    }
    
    private JsonArray parseArray() 
    {
        List<Object> values = new ArrayList<>();
        
        aInput.consume(CHAR_START_ARRAY);
        
        while(true)
        {
        	aInput.skipBlanks();
        	if(aInput.isNext(CHAR_END_ARRAY))
        	{
        		aInput.consume(CHAR_END_ARRAY);
        		return new JsonArray(values);
        	}
        	if( values.size() > 0 )
        	{
        		aInput.consume(CHAR_COMMA);
        		aInput.skipBlanks();
        	}
        	values.add(nextValue());
        }
    }
    
    @Override
	public String toString()
    {
    	return aInput.toString();
    }
    
    public static JsonObject parse(String pInput)
    {
    	return new JsonParser(pInput).parseObject();
    }
}
