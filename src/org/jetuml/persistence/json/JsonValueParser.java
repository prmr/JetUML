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

interface JsonValueParser
{
	/**
	 * Determines if this parser is applicable to parse the next 
	 * token in the input.
	 * 
	 * @param pInput An input buffer, possible at the end.
	 * @return True iff this parser is applicable to the input
	 * given the next character. Always returns false if the input has no
	 * readable character.
	 * @pre pInput != null
	 */
	boolean isApplicable(ParsableCharacterBuffer pInput);
	
	/**
	 * Parses the input to return the next value. This method should 
	 * only be called on an input if isApplicable return true.
	 * 
	 * @param pInput The input to parse.
	 * @return A valid JSON value parsed from pInput.
	 * @throws JsonParsingException if there is a problem parsing
	 * a value from the input.
	 * @pre pInput != null
	 */
	Object parse(ParsableCharacterBuffer pInput);
}
