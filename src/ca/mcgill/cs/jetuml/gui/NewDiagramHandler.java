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

import ca.mcgill.cs.jetuml.diagram.DiagramType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * An event handler whose callback has the effect of opening a new 
 * diagram in the EditorFrame.
 */
public class NewDiagramHandler implements EventHandler<ActionEvent>
{
	private final DiagramType aDiagramType;
	private final EventHandler<ActionEvent> aHandler;
	
	/**
	 * Creates a new handler.
	 * 
	 * @param pDiagramType The type of diagram to open. Must be a subtype of Diagram.
	 * @param pHandler The function that opens a new diagram of this type.
	 */
	public NewDiagramHandler( DiagramType pDiagramType, EventHandler<ActionEvent> pHandler)
	{
		assert pDiagramType != null && pHandler != null;
		aDiagramType = pDiagramType;
		aHandler = pHandler;
	}
	
	@Override
	public void handle(ActionEvent pEvent)
	{
		aHandler.handle(pEvent);
	}
	
	/**
	 * @return The diagram type.
	 */
	public DiagramType getDiagramType()
	{
		return aDiagramType;
	}
}
