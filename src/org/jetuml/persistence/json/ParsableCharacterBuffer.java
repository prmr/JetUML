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

/**
 * An extension of CharacterBuffer that supports basic parsing operations.
 * In contrast to the CharacterBuffer, which has strict preconditions 
 * for state-changing methods, this class accepts any input and 
 * throws a JsonParsingException if the operation is illegal.
 */
class ParsableCharacterBuffer extends CharacterBuffer
{
	/**
	 * Creates a new ParsableCharacterBuffer with no character read.
	 * 
	 * @param pInput The string to use as character sequence.
	 * @pre pInput != null
	 */
	ParsableCharacterBuffer(String pInput)
	{
		super(pInput);
	}
	
	/**
	 * Get the next character.
	 *
	 * @return The next character, assumed to exist.
	 * @throws JsonException if there is no more character to read.
	 */
	@Override
	char next()
	{
		if(!hasMore())
		{
			throw new JsonParsingException(position());
		}
		return super.next();
	}
	
	/**
	 * Get the next pNumberOfCharacters characters as a string.
	 *
	 * @return The next pNumberOfCharacters characters, assumed to exist.
	 * @pre pNumberOfCharacters >= 0;
	 */
	String next(int pNumberOfCharacters)
	{
		assert pNumberOfCharacters >= 0;
		StringBuffer result = new StringBuffer();
		for( int i = 0; i < pNumberOfCharacters; i++ )
		{
			result.append(next());
		}
		return result.toString();
	}
	
	/**
	 * Read the next character in the buffer and check that it is 
	 * the same as pCharacter. Throws a JsonException if it is not or
	 * if there are no more characters to read.
	 * 
	 * @param pCharacter The character to check.
	 */
	void consume(char pCharacter)
	{
		if( next() != pCharacter )
		{
			throw new JsonParsingException(position());
		}
	}
}
