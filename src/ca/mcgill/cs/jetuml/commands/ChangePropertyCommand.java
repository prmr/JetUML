/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.commands;

import ca.mcgill.cs.jetuml.graph.Property;

/**
 * Represents a change to the property of a DiagramElement.
 */
public class ChangePropertyCommand implements Command
{
	private Property aProperty;
	private Object aOldValue; 
	private Object aNewValue;
	
	/**
	 * Create a new command.
	 * 
	 * @param pProperty The changed property.
	 * @param pOldValue The former value for the property.
	 * @param pNewValue The value the property should have after executing the command
	 * @pre pProperty != null && pOldValue != null && pNewValue != null
	 */
	public ChangePropertyCommand( Property pProperty, Object pOldValue, Object pNewValue)
	{
		assert pProperty != null && pOldValue != null && pNewValue != null;
		aProperty = pProperty;
		aOldValue = pOldValue;
		aNewValue = pNewValue;
	}
	
	@Override
	public void execute()
	{
		set(aNewValue);
	}

	@Override
	public void undo()
	{
		set(aOldValue);		
	}
	
	private void set(Object pValue)
	{
		if( pValue instanceof Enum )
		{
			aProperty.set(pValue.toString());		
		}
		else
		{
			aProperty.set(pValue);		
		}
	}
}