/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
 * 
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.
 * 
 * The code in this class was originally based on JSON.org.
 * 
 ******************************************************************************/
package org.jetuml.persistence.json;

/**
 * Attempts to parse an input text assumed to be in JSON notation
 * into a JsonObject. This implementation supports only a subset of the ECMA-404 2nd
 * edition (December 2017) standard. The standard is supported except for:
 * * Non-integer number values
 * * Null values
 */
public final class JsonParser 
{
	private static final JsonObjectParser PARSER = new JsonObjectParser();
	
    public static JsonObject parse(String pInput)
    {
    	return PARSER.parse(new ParsableCharacterBuffer(pInput));
    }
}
