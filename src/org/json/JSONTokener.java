package org.json;

import static java.lang.Character.isWhitespace;

import java.util.HashMap;
import java.util.Map;

/*
Copyright (c) 2002 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

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

    /**
     * Get the next value. The value can be a Boolean, Double, Integer,
     * JSONArray, JSONObject, Long, or String, or the JSONObject.NULL object.
     * @throws JSONException If syntax error.
     *
     * @return An object.
     */
    public Object nextValue()
    {
        char c = nextNonWhitespace();
        String string;

        switch (c) 
        {
        case '"':
            return nextString();
        case '{':
            backUp();
            return new JSONObject(this);
        case '[':
            backUp();
            return new JSONArray(this);
        }

        /*
         * Handle unquoted text. This could be the values true, false, or
         * null, or it can be a number. An implementation (such as this one)
         * is allowed to also accept non-standard forms.
         *
         * Accumulate characters until we reach the end of the text or a
         * formatting character.
         */

        StringBuilder sb = new StringBuilder();
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) 
        {
            sb.append(c);
            c = next();
        }
        backUp();

        string = sb.toString().trim();
        if ("".equals(string)) {
            throw syntaxError("Missing value");
        }
        return JSONObject.stringToValue(string);
    }

    /**
     * Make a JSONException to signal a syntax error.
     *
     * @param message The error message.
     * @return  A JSONException object, suitable for throwing
     */
    public JSONException syntaxError(String message) 
    {
        return new JSONException(message + toString());
    }

    /**
     * Make a JSONException to signal a syntax error.
     *
     * @param message The error message.
     * @param causedBy The throwable that caused the error.
     * @return  A JSONException object, suitable for throwing
     */
    public JSONException syntaxError(String message, Throwable causedBy) 
    {
        return new JSONException(message + toString(), causedBy);
    }
}
