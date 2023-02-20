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
