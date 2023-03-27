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
 * Attempts to parse an input text assumed to be in JSON notation
 * into a JsonObject. This implementation supports only a subset of the ECMA-404 2nd
 * edition (December 2017) standard. The standard is supported except for:
 * * Non-integer number values
 * * Null values
 */
public final class JsonParser 
{
	private static final JsonObjectParser PARSER = new JsonObjectParser();
	
	private JsonParser() {}
	
    /**
     * Parses a string into a JsonObject.
     * 
     * @param pInput The input string, in JSON.
     * @return The JsonObject represented by the string.
     */
    public static JsonObject parse(String pInput)
    {
    	return PARSER.parse(new ParsableCharacterBuffer(pInput));
    }
}
