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
 * Represents an error while parsing JSON text.
 */
@SuppressWarnings("serial")
public class JsonParsingException extends JsonException 
{
	private final int aPosition;

    /**
     * Creates an exception that indicates a problem at a specific
     * position in a JSON input text.
     * 
     * @param pPosition The problematic character position.
     */
    public JsonParsingException(int pPosition) 
    {
        super(String.format("Invalid JSON text at character position: %d", pPosition));
        aPosition = pPosition;
    }
    
    /**
     * @return The problematic position.
     */
    public int position()
    {
    	return aPosition;
    }
}
