package org.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

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
 * A JSONTokener takes a source string and extracts characters and tokens from
 * it. 
 */
public class JSONTokener 
{
    /* Flag to indicate if the end of the input has been found. */
    private boolean aEndOfInputReached = false;
    
    /* Current read index of the input. */
    private long aPosition = 0;
    
    /* Previous character read from the input. */
    private char aPreviouslyReadCharacter = 0;
    
    /* Reader for the input. */
    private final Reader aReader;
    
    /* Flag to indicate that a previous character was requested. */
    private boolean aUsePreviousCharacter = false;
    
    /**
     * Construct a JSONTokener from a string.
     *
     * @param s     A source string.
     */
    public JSONTokener(String s) 
    {
        aReader = new StringReader(s);
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
        if (aUsePreviousCharacter || aPosition <= 0) 
        {
            throw new JSONException("Stepping back two steps is not supported");
        }
        aPosition--;
        aUsePreviousCharacter = true;
        aEndOfInputReached = false;
    }

    /**
     * Checks if the end of the input has been reached.
     *  
     * @return true if at the end of the file and we didn't step back
     */
    public boolean end() 
    {
        return aEndOfInputReached && !aUsePreviousCharacter;
    }

    /**
     * Get the next character in the source string.
     *
     * @return The next character, or 0 if past the end of the source string.
     * @throws JSONException Thrown if there is an error reading the source string.
     */
    public char next()
    {
        int c;
        if(aUsePreviousCharacter) 
        {
            aUsePreviousCharacter = false;
            c = aPreviouslyReadCharacter;
        } 
        else 
        {
            try 
            {
                c = aReader.read();
            } 
            catch(IOException exception) 
            {
                throw new JSONException(exception);
            }
        }
        if (c <= 0) 
        { // End of stream
            aEndOfInputReached = true;
            return 0;
        }
        if(c > 0) 
        {
            aPosition++;
        }
        aPreviouslyReadCharacter = (char) c;
        return aPreviouslyReadCharacter;
    }

    /**
     * Get the next n characters.
     *
     * @param n     The number of characters to take.
     * @return      A string of n characters.
     * @throws JSONException
     *   Substring bounds error if there are not
     *   n characters remaining in the source string.
     */
    public String next(int n)
    {
        if (n == 0)
        {
            return "";
        }

        char[] chars = new char[n];
        int pos = 0;

        while (pos < n) 
        {
            chars[pos] = next();
            if (this.end()) 
            {
                throw this.syntaxError("Substring bounds error");
            }
            pos += 1;
        }
        return new String(chars);
    }

    /**
     * Get the next char in the string, skipping whitespace.
     * @throws JSONException Thrown if there is an error reading the source string.
     * @return  A character, or 0 if there are no more characters.
     */
    public char nextClean()
    {
        for (;;)
        {
            char c = this.next();
            if (c == 0 || c > ' ') 
            {
                return c;
            }
        }
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
                c = this.next();
                switch (c) {
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
