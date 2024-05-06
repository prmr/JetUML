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

import static java.lang.Character.isISOControl;

/**
 * Parses strings in JSON document according to the ECMA-404 2nd
 * edition December 2017. Also provides support for writing strings 
 * in JSON standard.
 */
final class JsonStringParser implements JsonValueParser
{
	private static final CharacterEscapes CHARACTER_ESCAPES = new CharacterEscapes();
	private static final int NUMBER_OF_UNICODE_DIGITS = 4;
	private static final int RADIX_HEXADECIMAL = 16;
	
	private static final char CHAR_QUOTE = '"';
	private static final char CHAR_ESCAPE = '\\';
	private static final char CHAR_UNICODE_ESCAPE = 'u';

	/*
	 * Attempts to retrieve a valid JSON escaped character by reading characters
	 * starting at the next available position, or throws an exception. This
	 * method has a side effect on the character buffer. For this method to
	 * succeed, the first character in the buffer must be the escape character
	 * '\\' (\u005C). The buffer will be left at the position of the last
	 * character in the escape.
	 * 
	 * @param pInput A buffer from which to read an escape.
	 * @return A character that corresponds to the next escaped character read
	 * from pInput
	 * @pre pInput != null
	 * @throws JsonException if it is not possible to parse an escape from
	 * pInput.
	 */
	private static char parseEscape(ParsableCharacterBuffer pInput)
	{
		assert pInput != null;

		pInput.consume(CHAR_ESCAPE);
		char next = pInput.next();
		if( CHARACTER_ESCAPES.isSymbol(next) )
		{
			return CHARACTER_ESCAPES.getCodePoint(next);
		}
		else if( next == CHAR_UNICODE_ESCAPE )
		{
			return parseUnicode(pInput);
		}
		else
		{
			throw new JsonParsingException(pInput.position());
		}
	}

	/*
	 * Attempts to retrieve a valid unicode value by reading characters starting
	 * at the next available position, or throws an exception. This method has a
	 * side effect on the character buffer. For this method to succeed, the
	 * first character in the buffer must be a hexadecimal digit (0-A) in either
	 * lower or upper case. The buffer will be left at the position of the last
	 * character in the escape.
	 * 
	 * @param pInput A buffer from which to read a four-digit hexadecimal value.
	 * 
	 * @return A character that corresponds to the unicode code point represented
	 * by the escape.
	 * 
	 * @pre pInput != null
	 * 
	 * @throws JsonException if it is not possible to parse an escape from
	 * pInput.
	 */
	private static char parseUnicode(ParsableCharacterBuffer pInput)
	{
		if(!pInput.hasMore(NUMBER_OF_UNICODE_DIGITS))
		{
			throw new JsonParsingException(pInput.position());
		}
		try
		{
			return (char) Integer.parseInt(pInput.next(NUMBER_OF_UNICODE_DIGITS), RADIX_HEXADECIMAL);
		}
		catch (NumberFormatException exception)
		{
			throw new JsonParsingException(pInput.position());
		}
	}

	/**
	 * To be applicable the next character must be a quote, i.e., the opening
	 * quote for the string.
	 */
	@Override
	public boolean isApplicable(ParsableCharacterBuffer pInput)
	{
		return pInput.isNext(CHAR_QUOTE);
	}

	/**
	 * Attempts to retrieve a valid JSON string from pBuffer by reading
	 * characters starting at the next available position, or throws an
	 * exception. This method has a side effect on the character buffer. For
	 * this method to succeed, the first character in the buffer must be the
	 * quote character '"' (\u0022), but it is not necessary for the last
	 * character to also be a quote. Characters beyond the closing quote will
	 * simply not be read. The buffer will be left at the position of the
	 * closing quote.
	 * 
	 * @param pInput A buffer from which to read a string.
	 * @return A valid string
	 * @pre pInput != null
	 * @throws JsonException if it is not possible to parse a string from
	 * pInput.
	 */
	@Override
	public String parse(ParsableCharacterBuffer pInput)
	{
		assert pInput != null;
		pInput.consume(CHAR_QUOTE);

		StringBuilder result = new StringBuilder();
		while (pInput.hasMore())
		{
			char next = pInput.next();
			if(isISOControl(next))
			{
				throw new JsonParsingException(pInput.position());
			}
			else if(next == CHAR_ESCAPE)
			{
				pInput.backUp();
				result.append(parseEscape(pInput));
			}
			else if(next == CHAR_QUOTE)
			{
				return result.toString();
			}
			else
			{
				result.append(next);
			}
		}
		throw new JsonParsingException(pInput.position());
	}
	
	/**
	 * Escapes characters that need to be escaped so that the string
	 * is a proper JSON format string.
	 * 
	 * @param pString The string to escape.
	 * @return The escaped string.
	 * @throws JsonException if pString is not an instance of String.
	 */
	static String writeJsonString(Object pString)
	{
		StringBuilder result = new StringBuilder();
		result.append(CHAR_QUOTE);
		for( char character : JsonValueValidator.asString(pString).toCharArray())
		{
			if( CHARACTER_ESCAPES.isEscapableCodePoint(character))
			{
				result.append(CHARACTER_ESCAPES.getEscape(character));
			}
			else if( Character.isISOControl(character))
			{
				result.append(toUnicodeString(character));
			}
			else
			{
				result.append(character);
			}
		}
		result.append(CHAR_QUOTE);
		return result.toString();
	}
	
	private static String toUnicodeString(char pCharacter)
	{
		return "\\u" + String.format("%04x", (int)pCharacter);
	}
}
