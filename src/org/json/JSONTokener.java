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
package org.json;

import static java.lang.Character.isWhitespace;

import java.util.HashMap;
import java.util.Map;

/**
 * An object to step through a string character by characters, with
 * the possibility of going backward. The JsonParser is not meant to 
 * be reused to parse different strings.
 */
public class JSONTokener 
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
	private static final char CHAR_ZERO = '0';
	private static final char CHAR_ONE = '1';
	private static final char CHAR_NINE = '9';
	private static final char CHAR_START_OBJECT = '{';
	private static final char CHAR_END_OBJECT = '}';
	private static final char CHAR_START_ARRAY = '[';
	private static final char CHAR_START_TRUE = 't';
	private static final char CHAR_START_FALSE = 'f';
	private static final char CHAR_START_NULL = 'n';
	private static final char CHAR_COLON = ':';
	
	private static final String VALUE_STRING_NULL = "null";
	
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
	private final String aInput;
	
	/* Current position in the input. Represents the position
	 * of the last character read. Initialized at -1. */
	private int aPosition = -1;
	
    /**
     * Constructs a new JsonParser initialized at the beginning of the input.
     *
     * @param pInput A string to use as a complete source of characters.
     * @pre pInput != null;
     */
    public JSONTokener(String pInput) 
    {
    	assert pInput != null;
        aInput = pInput;
    }
    
    /**
     * Get the next character.
     *
     * @return The next character, assumed to exist.
     * @pre hasNext()
     */
    public char next()
    {
    	assert hasNext();
    	aPosition++;
    	return aInput.charAt(aPosition);
    }
    
    /**
     * @return True iif there is at least one more character 
     * to read.
     */
    public boolean hasNext()
    {
    	return aPosition + 1 < aInput.length();
    }

    /**
     * Back up one character. 
     * @pre canBackUp()
     */
    public void backUp()
    {
    	aPosition--;
    }
    
    /**
     * @return True if it is possible to back up one character. This is possible
     * iff the position is not at the beginning of the input, before the first character.
     */
    public boolean canBackUp()
    {
    	return aPosition >= 0;
    }

    /* All methods below should be implemented using the methods above,
     * as opposed to direct field manipulation. */
    
    /*
     * Get the next pNumberOfCharacters characters as a string.
     *
     * @param pNumberOfCharacters The number of characters to take.
     * @return A string of pNumberOfCharacters characters.
     */
    private String next(int pNumberOfCharacters)
    {
        assert pNumberOfCharacters > 0 && hasMore(pNumberOfCharacters);
        String result = aInput.substring(aPosition+1, aPosition+1 + pNumberOfCharacters);
        aPosition += pNumberOfCharacters;
        return result;
    }
    
    /*
     * @return True if there are still pNumberOfCharacters or more to read.
     */
    private boolean hasMore(int pNumberOfCharacters)
    {
    	assert pNumberOfCharacters > 0;
    	return aPosition + pNumberOfCharacters < aInput.length();
    }

    /**
     * Get the next char in the string, skipping whitespace.
     * @return  The next non-whitespace character, assumed to exist.
     * @pre hasMoreNonWhitespace()
     */
    public char nextNonWhitespace()
    {
    	assert hasMoreNonWhitespace();
       	while(hasNext())
        {
            char character = next();
            if( !isWhitespace(character) ) 
            {
                return character;
            }
        }
        assert false; // Precondition violated
        return 0;
    }
    
    /**
     * @return True iif there is at least one non-whitespace 
     * character left to read, as defined by !Character#isWhitespace
     */
    private boolean hasMoreNonWhitespace()
    {
    	return aInput
    			.substring(aPosition + 1)
    			.replaceAll("\\s+", "")
    			.length() > 0;
    }

    /**
     * Return the characters up to the next quote character. For 
     * a given string, the first (opening) quote character should 
     * already have been read. This method is intended to properly 
     * process escapes. The formal JSON format does not allow strings
     * in single quotes, and they are not accepted by this method.
     * Strings are not allowed to span lines.
     * @return A string
     * @throws JSONException If there's is an unanticipated error processing the string.
     */
    private String nextString()
    {
    	if( !hasNext() )
    	{
    		throw new JSONException("Unterminated string");
    	}
    	
    	StringBuilder result = new StringBuilder();
    	while(hasNext())
    	{
    		char next = next();
    		if( next == CHAR_NEWLINE || next == CHAR_CARRIAGE_RETURN )
    		{
    			throw new JSONException("Newline in string");
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
    	throw new JSONException("Unterminated string");
    }
    
    /*
     * Call after the escaping character '\' is detected in 
     * the input, to complete the decoding of the escaped character.
     */
    private char nextEscaped()
    {
    	if( !hasNext() )
    	{
    		throw new JSONException("Invalid escape sequence found");
    	}
    	char next = next();
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
    		throw new JSONException("Invalid escape sequence found");
    	}
    }
    
    /*
     * Call after the escaping characters '\' and 'u' are detected in 
     * the input, to complete the decoding of the escaped unicode character.
     */
    private char nextUnicode()
    {
    	if( !hasMore(NUMBER_OF_UNICODE_DIGITS) )
    	{
    		throw new JSONException("Invalid escape sequence found");
    	}
    	try
    	{
    		return (char) Integer.parseInt(next(NUMBER_OF_UNICODE_DIGITS), RADIX_HEXADECIMAL);
    	}
    	catch( NumberFormatException exception )
    	{
    		throw new JSONException("Invalid unicode");
    	}
    }
    
    /*
     * Attempts to parse the next characters as a JSON boolean;
     */
    private Boolean nextBoolean()
    {
    	assert hasNext();
    	String valueString = "";
    	char next = next();
    	assert next == CHAR_START_TRUE || next == CHAR_START_FALSE;
    	if( next == Boolean.TRUE.toString().charAt(0) )
    	{
    		valueString = Boolean.TRUE.toString();
    	}
    	else
    	{
    		valueString = Boolean.FALSE.toString();
    	}
    	backUp();
    	if( !hasMore(valueString.length() ))
    	{
    		throw new JSONException("Cannot parse " + valueString + " value");
    	}
    	if(next(valueString.length()).equals(valueString))
    	{
    		return Boolean.valueOf(valueString);
    	}
    	else
    	{
    		throw new JSONException("Cannot parse " + valueString + " value");
    	}
    }
    
    private Object nextNull()
    {
    	if(!hasMore(VALUE_STRING_NULL.length()))
    	{
    		throw new JSONException("Cannot parse null");
    	}
    	else if(next(VALUE_STRING_NULL.length()).equals(VALUE_STRING_NULL))
    	{
    		return JSONObject.NULL;
    	}
    	else
    	{
    		throw new JSONException("Cannot parse null");
    	}
    }
    
    /*
     * @pre the next character is a valid integer start number
     */
    private Integer nextInteger()
    {
    	StringBuffer numberAsString = new StringBuffer();
    	char next = next();
    	assert startsInteger(next);
    	numberAsString.append(next);
    	while( hasNext() )
    	{
    		next = next();
    		if( isDigit(next) )
    		{
    			numberAsString.append(next);
    		}
    		else
    		{
    			backUp();
    			return parseInt(numberAsString.toString());
    		}
    	}
    	return parseInt(numberAsString.toString());
    }
    
    private static int parseInt(String pNumber)
    {
    	try
    	{
    		return Integer.parseInt(pNumber);
    	}
    	catch(NumberFormatException exception)
    	{
    		throw new JSONException("Illegal integer value: " + pNumber);
    	}
    }
    
    /*
     * @return True if the character can be the valid first
     * character of an integer, which is - or [1-9]
     */
    private static boolean startsInteger(char pCharacter)
    {
    	return pCharacter == CHAR_MINUS ||
    			(pCharacter >= CHAR_ONE && pCharacter <= CHAR_NINE);
    }
    
    private static boolean isDigit(char pCharacter)
    {
    	return pCharacter >= CHAR_ZERO && pCharacter <= CHAR_NINE;
    }

    /**
     * Get the next value. The value can be a Boolean, Double, Integer,
     * JSONArray, JSONObject, Long, or String, or the JSONObject.NULL object.
     * However, this implementation only support integers number formats.
     * @throws JSONException If syntax error.
     *
     * @return An object.
     */
    public Object nextValue()
    {
        char next = nextNonWhitespace();

        if(next == CHAR_QUOTE)
        {
        	return nextString();
        }
        else if(next == CHAR_START_OBJECT)
        {
        	backUp();
        	return new JSONObject(this);
        }
        else if(next ==  CHAR_START_ARRAY)
        {
        	backUp();
        	return new JSONArray(this);
        }
        else if(next == CHAR_START_TRUE || next == CHAR_START_FALSE)
        {
        	backUp();
        	return nextBoolean();
        }
        else if(next == CHAR_START_NULL )
        {
        	backUp();
        	return nextNull();
        }
        else if(startsInteger(next))
        {
        	backUp();
        	return nextInteger();
        }
        else
        {
        	throw new JSONException("Unsupported value");
        }
    }
    
    public JSONObject parseObject() 
    {
        JSONObject object = new JSONObject();
        
        if(nextNonWhitespace() != CHAR_START_OBJECT) 
        {
            throw new JSONException("A JSONObject text must begin with '{'");
        }
        while(true)
        {
        	if( !hasMoreNonWhitespace())
        	{
        		throw new JSONException("Incomplete object");
        	}
        	char next = nextNonWhitespace();
        	if( next == CHAR_END_OBJECT )
        	{
        		return object;
        	}
        	else
        	{
        		if( !object.keySet().isEmpty() )
        		{
        			if( next != ',' )
        			{
        				throw new JSONException("Missing comma");
        			}
        			else
        			{
        				next = nextNonWhitespace();
        			}
        		}
        		if( next != CHAR_QUOTE )
        		{
        			throw new JSONException("Invalid key");
        		}
        	}
        	String key = nextString();
        	if( !hasMoreNonWhitespace() )
        	{
        		throw new JSONException("Incomplete object");
        	}
        	next = nextNonWhitespace();
        	if( next != CHAR_COLON )
        	{
        		throw new JSONException("Expecting a key-value separator");
        	}
        	if( object.has(key))
        	{
        		throw new JSONException("Duplicate key");
        	}
        	if( !hasMoreNonWhitespace() )
        	{
        		throw new JSONException("Incomplete object");
        	}
        	Object value = nextValue();
        	object.put(key, value);
        }
    }
}
