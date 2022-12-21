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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An object to step through a string character by characters, with
 * the possibility of going backward. The JsonParser is not meant to 
 * be reused to parse different strings.
 * 
 * Null values are not supported.
 */
public class JsonParser 
{
	/*
	 * Maps a character in an escape (after the slash) to the escaped character,
	 * e.g., b -> \b
	 */
	private static final Map<Character,Character> ESCAPE_CHARACTERS = new HashMap<>();
	
	private static final int NUMBER_OF_UNICODE_DIGITS = 4;
	private static final int RADIX_HEXADECIMAL = 16;
	
	private static final char CHAR_UNICODE_ESCAPE = 'u';
	private static final char CHAR_NEWLINE = '\n';
	private static final char CHAR_CARRIAGE_RETURN = '\r';
	private static final char CHAR_ESCAPE = '\\';
	private static final char CHAR_QUOTE = '"';
	private static final char CHAR_MINUS = '-';
	private static final char CHAR_COMMA = ',';
	private static final char CHAR_ZERO = '0';
	private static final char CHAR_NINE = '9';
	private static final char CHAR_START_OBJECT = '{';
	private static final char CHAR_END_OBJECT = '}';
	private static final char CHAR_START_ARRAY = '[';
	private static final char CHAR_END_ARRAY = ']';
	private static final char CHAR_START_TRUE = 't';
	private static final char CHAR_START_FALSE = 'f';
	private static final char CHAR_COLON = ':';
	
	static
	{
		// The first five are re-escaped
		ESCAPE_CHARACTERS.put('b','\b');
		ESCAPE_CHARACTERS.put('t','\t');
		ESCAPE_CHARACTERS.put('n','\n');
		ESCAPE_CHARACTERS.put('f','\f');
		ESCAPE_CHARACTERS.put('r','\r');
		// The last three remain unescaped
		ESCAPE_CHARACTERS.put('"','"');
		ESCAPE_CHARACTERS.put('\\','\\');
		ESCAPE_CHARACTERS.put('/','/');
	}
	
	/* Complete input to traverse. */
	private final CharacterBuffer aInput;
	
    /**
     * Constructs a new JsonParser initialized at the beginning of the input.
     *
     * @param pInput A string to use as a complete source of characters.
     * @pre pInput != null;
     */
    public JsonParser(String pInput) 
    {
    	assert pInput != null;
        aInput = new CharacterBuffer(pInput.trim());
    }
    
    /*
     * Check that the next non-blank character is pSymbol, and read it.
     */
    private void consume(char pSymbol)
    {
    	if(!aInput.hasMore() || aInput.next() != pSymbol)
    	{
    		throw new JsonException(String.format("Expecting '%s' at position %d", pSymbol, aInput.position()));
    	}
    }
    
    /**
     * Return the characters up to the next quote character. For 
     * a given string, the first (opening) quote character should 
     * already have been read. This method is intended to properly 
     * process escapes. The formal JSON format does not allow strings
     * in single quotes, and they are not accepted by this method.
     * Strings are not allowed to span lines.
     * @return A string
     * @throws JsonException If there's is an unanticipated error processing the string.
     */
    private String parseString()
    {
    	consume(CHAR_QUOTE);
    	
    	StringBuilder result = new StringBuilder();
    	while(aInput.hasMore())
    	{
    		char next = aInput.next();
    		if( next == CHAR_NEWLINE || next == CHAR_CARRIAGE_RETURN )
    		{
    			throw new JsonException("Newline in string");
    		}
    		else if(next == CHAR_ESCAPE )
    		{
    			result.append(nextEscaped());
    		}
    		else if(next == CHAR_QUOTE )
    		{
    			return result.toString();
    		}
    		else
    		{
    			result.append(next);
    		}
    	}
    	throw new JsonException("Unterminated string");
    }
    
    /*
     * Call after the escaping character '\' is detected in 
     * the input, to complete the decoding of the escaped character.
     */
    private char nextEscaped()
    {
    	if( !aInput.hasMore() )
    	{
    		throw new JsonException("Invalid escape sequence found");
    	}
    	char next = aInput.next();
    	if( ESCAPE_CHARACTERS.containsKey(next))
    	{
    		return ESCAPE_CHARACTERS.get(next);
    	}
    	else if( next == CHAR_UNICODE_ESCAPE ) 
    	{
    		return nextUnicode();
    	}
    	else
    	{
    		throw new JsonException("Invalid escape sequence found");
    	}
    }
    
    /*
     * Call after the escaping characters '\' and 'u' are detected in 
     * the input, to complete the decoding of the escaped unicode character.
     */
    private char nextUnicode()
    {
    	if( !aInput.hasMore(NUMBER_OF_UNICODE_DIGITS) )
    	{
    		throw new JsonException("Invalid escape sequence found");
    	}
    	try
    	{
    		return (char) Integer.parseInt(aInput.next(NUMBER_OF_UNICODE_DIGITS), RADIX_HEXADECIMAL);
    	}
    	catch( NumberFormatException exception )
    	{
    		throw new JsonException("Invalid unicode");
    	}
    }
    
    /*
     * Attempts to parse the next characters as a JSON boolean;
     */
    private Boolean nextBoolean()
    {
    	assert aInput.hasMore();
    	String valueString = "";
    	char next = aInput.next();
    	assert next == CHAR_START_TRUE || next == CHAR_START_FALSE;
    	if( next == Boolean.TRUE.toString().charAt(0) )
    	{
    		valueString = Boolean.TRUE.toString();
    	}
    	else
    	{
    		valueString = Boolean.FALSE.toString();
    	}
    	aInput.backUp();
    	if( !aInput.hasMore(valueString.length() ))
    	{
    		throw new JsonException("Cannot parse " + valueString + " value");
    	}
    	if(aInput.next(valueString.length()).equals(valueString))
    	{
    		return Boolean.valueOf(valueString);
    	}
    	else
    	{
    		throw new JsonException("Cannot parse " + valueString + " value");
    	}
    }
    
    /*
     * @pre the next character is a valid integer start number
     */
    private Integer nextInteger()
    {
    	StringBuffer numberAsString = new StringBuffer();
    	char next = aInput.next();
    	numberAsString.append(next);
    	while( aInput.hasMore() )
    	{
    		next = aInput.next();
    		if( isDigit(next) )
    		{
    			numberAsString.append(next);
    		}
    		else
    		{
    			aInput.backUp();
    			return parseInt(numberAsString.toString());
    		}
    	}
    	return parseInt(numberAsString.toString());
    }
    
    private static int parseInt(String pNumber)
    {
    	try
    	{
    		if( illegalNumber(pNumber) )
    		{
    			throw new JsonException("Illegal integer value: " + pNumber);
    		}
    		return Integer.parseInt(pNumber);
    	}
    	catch(NumberFormatException exception)
    	{
    		throw new JsonException("Illegal integer value: " + pNumber);
    	}
    }
    
    private static boolean illegalNumber(String pNumber)
    {
    	if(pNumber.isBlank())
    	{
    		return true;
    	}
    	if(pNumber.startsWith("-0"))
    	{
    		return true;
    	}
    	if( pNumber.length() >=2 && pNumber.charAt(0) == CHAR_ZERO && isDigit(pNumber.charAt(1)))
    	{
    		return true;
    	}
    	return false;
    }
    
    /*
     * @return True if the next non-blank character can be the valid first
     * character of an integer, which is - or [1-9]
     */
    private boolean startsInteger()
    {
    	aInput.skipBlanks();
    	if (!aInput.hasMore() )
    	{
    		return false;
    	}
    	char next = aInput.next();
    	aInput.backUp();
		return next == CHAR_MINUS || isDigit(next);
    }
    
    private static boolean isDigit(char pCharacter)
    {
    	return pCharacter >= CHAR_ZERO && pCharacter <= CHAR_NINE;
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
    	if(aInput.isNext(CHAR_QUOTE))
        {
        	return parseString();
        }
    	else if(aInput.isNext(CHAR_START_OBJECT))
        {
        	return parseObject();
        }
        else if(aInput.isNext(CHAR_START_ARRAY))
        {
        	return parseArray();
        }
        else if(aInput.isNext(CHAR_START_TRUE) || aInput.isNext(CHAR_START_FALSE))
        {
        	return nextBoolean();
        }
        else if(startsInteger())
        {
        	return nextInteger();
        }
        else
        {
        	throw new JsonException("Unsupported value");
        }
    }
    
    private void whitespace()
    {
    	aInput.skipBlanks();
    }
    
    private JsonObject parseObject() 
    {
        JsonObject object = new JsonObject();
        
        consume(CHAR_START_OBJECT);
			
        while(true)
		{
        	whitespace();
            if( aInput.isNext(CHAR_END_OBJECT))
            {
            	consume(CHAR_END_OBJECT);
            	return object;
            }
            
            if(object.entrySet().size() > 0)
            {
            	consume(CHAR_COMMA);
            	whitespace();
            }
       
        	String key = parseString();
        	whitespace();
			consume(CHAR_COLON);
			whitespace();
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
        
        consume(CHAR_START_ARRAY);
        
        while(true)
        {
        	whitespace();
        	if(aInput.isNext(CHAR_END_ARRAY))
        	{
        		consume(CHAR_END_ARRAY);
        		return new JsonArray(values);
        	}
        	if( values.size() > 0 )
        	{
        		consume(CHAR_COMMA);
        		whitespace();
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
