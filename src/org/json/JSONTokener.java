package org.json;

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
     * Back up one character. This provides a sort of lookahead capability,
     * so that you can test for a digit or letter before attempting to parse
     * the next number or identifier.
     * @throws JSONException Thrown if trying to step back more than 1 step
     *  or if already at the start of the string
     */
    public void back()
    {
    	if( aPosition < 0 )
    	{
    		throw new JSONException("Cannot step back: already at the beginning of the buffer.");
    	}
    	aPosition--;
    }

    /**
     * Checks if the end of the input has been reached.
     *  
     * @return true if at the end of the file and we didn't step back
     */
    public boolean end() 
    {
    	return !hasNext();
    }



    /**
     * Get the next n characters.
     *
     * @param pNumberOfCharacters     The number of characters to take.
     * @return      A string of n characters.
     * @throws JSONException
     *   Substring bounds error if there are not
     *   n characters remaining in the source string.
     */
    public String next(int pNumberOfCharacters)
    {
        assert pNumberOfCharacters > 0 && aPosition + pNumberOfCharacters < aInput.length();
        aPosition += pNumberOfCharacters;
        return aInput.substring(aPosition+1, aPosition+1 + pNumberOfCharacters);
    }

    /**
     * Get the next char in the string, skipping whitespace.
     * @throws JSONException Thrown if there is an error reading the source string.
     * @return  A character, or 0 if there are no more characters.
     */
    public char nextClean()
    {
    	assert !end();
        for(int i = aPosition; i < aInput.length(); i++ )
        {
            char c = next();
            if(c == 0 || c > ' ') 
            {
                return c;
            }
        }
        return 0;
    }

    /**
     * Return the characters up to the next close quote character.
     * Backslash processing is done. The formal JSON format does not
     * allow strings in single quotes, but an implementation is allowed to
     * accept them.
     * @param quote The quoting character, either
     *      <code>"</code>&nbsp;<small>(double quote)</small> or
     *      <code>'</code>&nbsp;<small>(single quote)</small>.
     * @return      A String.
     * @throws JSONException Unterminated string.
     */
    public String nextString(char quote)
    {
        char c;
        StringBuilder sb = new StringBuilder();
        for (;;) 
        {
            c = next();
            switch (c) 
            {
            case 0:
            case '\n':
            case '\r':
                throw syntaxError("Unterminated string");
            case '\\':
                c = next();
                switch (c)
                {
                case 'b':
                    sb.append('\b');
                    break;
                case 't':
                    sb.append('\t');
                    break;
                case 'n':
                    sb.append('\n');
                    break;
                case 'f':
                    sb.append('\f');
                    break;
                case 'r':
                    sb.append('\r');
                    break;
                case 'u':
                    try 
                    {
                        sb.append((char)Integer.parseInt(next(4), 16));
                    }
                    catch (NumberFormatException e)
                    {
                        throw syntaxError("Illegal escape.", e);
                    }
                    break;
                case '"':
                case '\'':
                case '\\':
                case '/':
                    sb.append(c);
                    break;
                default:
                    throw syntaxError("Illegal escape.");
                }
                break;
            default:
                if (c == quote) 
                {
                    return sb.toString();
                }
                sb.append(c);
            }
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
        char c = nextClean();
        String string;

        switch (c) 
        {
        case '"':
        case '\'':
            return nextString(c);
        case '{':
            back();
            return new JSONObject(this);
        case '[':
            back();
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
        back();

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
