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
