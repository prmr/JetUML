/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
package org.jetuml.diagram;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Represents a property of an object as a tuple that 
 * consists of a name, a getter for a value, and a setter
 * for that value. The values managed by a property should only be of immutable types.
 */
public class Property
{
	private final PropertyName aName;
	private final Supplier<Object> aGetter;
	private final Consumer<Object> aSetter;
	
	/**
	 * Creates a new property.
	 * 
	 * @param pName The name of the property.
	 * @param pGetter The getter for the property.
	 * @param pSetter The setter for the property.
	 * @pre pName != null && pGetter != null && pSetter != null.
	 */
	public Property(PropertyName pName, Supplier<Object> pGetter, Consumer<Object> pSetter)
	{
		assert pName != null && pGetter != null && pSetter != null;
		aName = pName;
		aGetter = pGetter;
		aSetter = pSetter;
	}
	
	/**
	 * @return The name of this property.
	 */
	public PropertyName name()
	{
		return aName;
	}
	
	/**
	 * @return The value of this property.
	 */
	public Object get()
	{
		return aGetter.get();
	}
	
	/**
	 * Assigns pValue to this property.
	 * 
	 * @param pValue The value to assign.
	 * @pre pValue != null
	 */
	public void set(Object pValue)
	{
		assert pValue != null;
		aSetter.accept(pValue);
	}
}
