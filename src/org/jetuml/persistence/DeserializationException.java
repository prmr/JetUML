/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020-2023 by McGill University.
 *     
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.persistence;

/**
 * Represents a problem retrieving a diagram from serialized form.
 */
@SuppressWarnings("serial")
public class DeserializationException extends RuntimeException
{
	/**
	 * A category for the error.
	 */
	public enum Category 
	{ 
		/**
		 * Syntactic errors are caused by malformed JSON text.
		 */
		SYNTACTIC, 
		
		
		/**
		 * Structural errors are caused by invalid arrangement of fields and values in the JSON.
		 */
		STRUCTURAL, 
		
		/**
		 * Semantic errors represent the violation of diagram validation rules.
		 */
		SEMANTIC }
	
	private final Category aCategory;
	
	/**
	 * Creates an exception with a category and a message.
	 * 
	 * @param pCategory The category or error.
	 * @param pMessage The message.
	 */
	public DeserializationException(Category pCategory, String pMessage)
	{
		super(pMessage);
		aCategory = pCategory;
	}
	
	/**
	 * Creates an exception with a category, a message, and a wrapped exception.
	 * 
	 * @param pCategory The category or error.
	 * @param pMessage The message.
	 * @param pException The wrapped exception.
	 */
	public DeserializationException(Category pCategory, String pMessage, Throwable pException)
	{
		super(pMessage, pException);
		aCategory = pCategory;
	}
	
	/**
	 * @return The error category.
	 */
	public Category category()
	{
		return aCategory;
	}
}
