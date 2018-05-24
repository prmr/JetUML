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
package ca.mcgill.cs.jetuml.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * An event handler with a name.
 */
public class NamedHandler implements EventHandler<ActionEvent>
{
	private final String aName;
	private final EventHandler<ActionEvent> aHandler;
	
	/**
	 * Creates a new named handler.
	 * 
	 * @param pName The name of the handler.
	 * @param pHandler The action event handler.
	 */
	public NamedHandler( String pName, EventHandler<ActionEvent> pHandler)
	{
		assert pName != null && pHandler != null;
		aName = pName;
		aHandler = pHandler;
	}

	@Override
	public void handle(ActionEvent pEvent)
	{
		aHandler.handle(pEvent);
	}
	
	/**
	 * @return The name of this handler.
	 */
	public String getName()
	{
		return aName;
	}
	
	/**
	 * @param pNewName The name for the renamed handler.
	 * @return A handler for the same event, but the the name specified.
	 */
	public NamedHandler renamed(String pNewName)
	{
		return new NamedHandler( pNewName, aHandler);
	}
}
