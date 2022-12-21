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
 * Represents an error while processing JSON data.
 */
@SuppressWarnings("serial")
public class JsonException extends RuntimeException 
{
    /**
     * Constructs a JSONException with a message.
     *
     * @param pMessage Details about the reason for the exception.
     */
    public JsonException(String pMessage)
    {
        super(pMessage);
    }

    /**
     * Constructs a JSONException with a message and cause.
     * 
     * @param pMessage Details about the reason for the exception.
     * @param pCause The original exception.
     */
    public JsonException(String pMessage, Throwable pCause) 
    {
        super(pMessage, pCause);
    }

    /**
     * Constructs a new JSONException with the specified cause.
     * 
     * @param pCause The cause.
     */
    public JsonException(Throwable pCause) 
    {
        super(pCause.getMessage(), pCause);
    }
}
