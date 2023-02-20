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
