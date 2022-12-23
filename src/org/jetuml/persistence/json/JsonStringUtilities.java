package org.jetuml.persistence.json;

import static java.lang.Character.isISOControl;

import java.util.HashMap;
import java.util.Map;

/**
 * Methods to work with strings in JSON document according to the ECMA-404 2nd
 * edition December 2017.
 */
public final class JsonStringUtilities
{
	/*
	 * Maps a character in an escape (after the slash) to the escaped character,
	 * e.g., b -> \b
	 */
	private static final Map<Character, Character> ESCAPE_CHARACTERS = new HashMap<>();

	private static final int NUMBER_OF_UNICODE_DIGITS = 4;
	private static final int RADIX_HEXADECIMAL = 16;
	
	private static final char CHAR_QUOTE = '"';
	private static final char CHAR_ESCAPE = '\\';
	private static final char CHAR_UNICODE_ESCAPE = 'u';

	static
	{
		// The first five are re-escaped
		ESCAPE_CHARACTERS.put('b', '\b');
		ESCAPE_CHARACTERS.put('t', '\t');
		ESCAPE_CHARACTERS.put('n', '\n');
		ESCAPE_CHARACTERS.put('f', '\f');
		ESCAPE_CHARACTERS.put('r', '\r');
		// The last three remain unescaped
		ESCAPE_CHARACTERS.put('"', '"');
		ESCAPE_CHARACTERS.put('\\', '\\');
		ESCAPE_CHARACTERS.put('/', '/');
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
	public static String parseString(ParsableCharacterBuffer pInput)
	{
		assert pInput != null;
		pInput.consume(CHAR_QUOTE);

		StringBuilder result = new StringBuilder();
		while (pInput.hasMore())
		{
			char next = pInput.next();
			if (isISOControl(next))
			{
				throw new JsonException("Control character found while parsing string.");
			}
			else if (next == CHAR_ESCAPE)
			{
				pInput.backUp();
				result.append(parseEscape(pInput));
			}
			else if (next == CHAR_QUOTE)
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

	/**
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
		if (ESCAPE_CHARACTERS.containsKey(next))
		{
			return ESCAPE_CHARACTERS.get(next);
		}
		else if (next == CHAR_UNICODE_ESCAPE)
		{
			return parseUnicode(pInput);
		}
		else
		{
			throw new JsonException("Invalid escape sequence found");
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
		if (!pInput.hasMore(NUMBER_OF_UNICODE_DIGITS))
		{
			throw new JsonException("Invalid unicode");
		}
		try
		{
			return (char) Integer.parseInt(pInput.next(NUMBER_OF_UNICODE_DIGITS), RADIX_HEXADECIMAL);
		}
		catch (NumberFormatException exception)
		{
			throw new JsonException("Invalid unicode");
		}
	}
}
