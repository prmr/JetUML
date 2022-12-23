package org.jetuml.persistence.json;

/**
 * An extension of CharacterBuffer that supports basic parsing operations.
 * In contrast to the CharacterBuffer, which has strict preconditions 
 * for state-changing methods, this class accepts any input and 
 * throws a JsonException if the operation is illegal.
 */
public class ParsableCharacterBuffer extends CharacterBuffer
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
			throw new JsonException("Attempting to read past the end of the buffer.");
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
			throw new JsonException(String.format("Expecting '%s' at position %d", pCharacter, position()));
		}
	}
}
