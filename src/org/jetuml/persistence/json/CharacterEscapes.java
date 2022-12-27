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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A class for handling the eight characters that can be escaped wit
 * another character in a JSON string: \", \\, \/, \b, \f, \n, \r, \t Note that
 * this does not include 'u', used for any unicode character.
 * 
 * In the documentation for this class, "symbol" refers to the character
 * following the escape (e.g., 'b' in "\b"), and "code point" refers to the code
 * point for that escape, in this case \b (backspace, U+0008)
 * 
 * This instance is stateless so it can be shared, but this is not enforced
 * (e.g., via a Singleton) in case it is more convenient to have multiple copies.
 */
final class CharacterEscapes
{
	private static final char CHAR_BACKSLASH = '\\';

	private final Map<Character, Character> aSymbolToCodePoint = new HashMap<>();
	private final Map<Character, String> aCodePointToSymbol = new HashMap<>();

	CharacterEscapes()
	{
		// The first five are re-escaped
		aSymbolToCodePoint.put('b', '\b');
		aSymbolToCodePoint.put('t', '\t');
		aSymbolToCodePoint.put('n', '\n');
		aSymbolToCodePoint.put('f', '\f');
		aSymbolToCodePoint.put('r', '\r');
		// The last three remain unescaped
		aSymbolToCodePoint.put('"', '"');
		aSymbolToCodePoint.put('\\', '\\');
		aSymbolToCodePoint.put('/', '/');
		// Create a reverse map
		for( Entry<Character, Character> entry : aSymbolToCodePoint.entrySet() )
		{
			aCodePointToSymbol.put(entry.getValue(), new String(new char[] { CHAR_BACKSLASH, entry.getKey() }));
		}
	}

	/**
	 * @param pSymbol A character to check
	 * @return True iif pSymbol is one of the eight symbols used in
	 * character escapes.
	 */
	boolean isSymbol(char pSymbol)
	{
		return aSymbolToCodePoint.containsKey(pSymbol);
	}

	/**
	 * @param pSymbol A symbol using in an escape to represent a code point
	 * @return The code point that represents this escape
	 */
	char getCodePoint(char pSymbol)
	{
		assert isSymbol(pSymbol);
		return aSymbolToCodePoint.get(pSymbol);
	}
	
	boolean isEscapableCodePoint(char pCharacter)
	{
		return aCodePointToSymbol.containsKey(pCharacter);
	}
	
	String getEscape(char pCharacter)
	{
		assert isEscapableCodePoint(pCharacter);
		return aCodePointToSymbol.get(pCharacter);
	}
}
