/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
 *     
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.persistence.json;

import static java.lang.Character.isWhitespace;

/**
 * A sequence of characters with a current position, to facilitate processing
 * character input. Client code is expected to only invoke valid operations, i.e., check
 * whether there are sufficient characters left to read before reading them.
 * 
 * The position of a character buffer corresponds to the position of the last character read. 
 * This position is initialized at -1. A character buffer is only expected to be traversed once.
 */
class CharacterBuffer
{
	private final String aCharacters;
	private int aPosition = -1;
	
	/**
	 * Creates a new CharacterBuffer with no character read.
	 * 
	 * @param pInput The string to use as character sequence.
	 * @pre pInput != null
	 */
	CharacterBuffer(String pInput)
	{
		assert pInput != null;
		aCharacters = pInput;
	}
	
	/**
	 * @return True iif there is at least one more character to read.
	 */
	boolean hasMore()
	{
		return aPosition + 1 < aCharacters.length();
	}
	
	/**
	 * @return The position of the last read character.
	 */
	int position()
	{
		return aPosition;
	}
	
	/**
	 * @param pNumberOfCharacters To check
	 * @return True iif there is at least pNumberOfCharacters more character to read.
	 */
	boolean hasMore(int pNumberOfCharacters)
	{
		assert pNumberOfCharacters > 0;
    	return aPosition + pNumberOfCharacters < aCharacters.length();
	}
	
	/**
	 * @return True iif there is at least one more non-blank character to read.
	 */
	boolean hasMoreNonBlank()
	{
		return aCharacters
    			.substring(aPosition + 1)
    			.replaceAll("\\s+", "")
    			.length() > 0;
	}
	
	/**
     * @return True if it is possible to back up one character. This is possible
     * iff the position is not at the beginning of the input, before the first character.
     */
    boolean canBackUp()
    {
    	return aPosition >= 0;
    }
    
    /**
     * More the position back by one.
     * @pre canBackUp()
     */
    void backUp()
    {
		assert canBackUp();
    	aPosition--;
    }
    
    /**
     * Move the position ahead until the first non-blank character
     * is found, or the end of the buffer is reached. Positions
     * the buffer before the next non-blank character, so that the 
     * next call to next() returns that character.
     */
    void skipBlanks()
    {
    	while(hasMore())
        {
            char character = next();
            if( !isWhitespace(character) ) 
            {
                backUp();
                return;
            }
        }
    }
    
    /**
     * Get the next character.
     *
     * @return The next character, assumed to exist.
     * @pre hasMore()
     */
    char next()
    {
    	assert hasMore();
    	aPosition++;
    	return aCharacters.charAt(aPosition);
    }
    
    /**
     * Get the next pNumberOfCharacters characters as a string.
     *
     * @return The next pNumberOfCharacters characters, assumed to exist.
     * @pre hasMore(pNumberOfCharacters)
     */
    String next(int pNumberOfCharacters)
    {
        assert pNumberOfCharacters > 0 && hasMore(pNumberOfCharacters);
        String result = aCharacters.substring(aPosition+1, aPosition+1 + pNumberOfCharacters);
        aPosition += pNumberOfCharacters;
        return result;
    }
    
    /**
     * Get the next char in the string, skipping whitespace.
     * @return  The next non-blank character, assumed to exist.
     * @pre hasMoreNonWhitespace()
     */
    char nextNonBlank()
    {
    	assert hasMoreNonBlank();
       	while(hasMore())
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
}
