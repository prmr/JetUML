/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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

package ca.mcgill.cs.jetuml.views;

import ca.mcgill.cs.jetuml.diagram.ControlFlow;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;

/**
 * A specialized viewer stategy for a sequence diagram.
 */
public class SequenceDiagramViewer extends DiagramViewer
{
	@Override
	protected Node deepFindNode(Diagram pDiagram, Node pNode, Point pPoint )
	{		
		ControlFlow flow = new ControlFlow(pDiagram);
		if( pNode instanceof CallNode )
		{
			for(Node child : flow.getCallees(pNode))
			{
				if ( child != null )
				{
					Node node = deepFindNode(pDiagram, child, pPoint);
					if ( node != null )
					{
						return node;
					}
				}
			}
		}
		return super.deepFindNode(pDiagram, pNode, pPoint);
	}
	
	/*
	 * Used during pasting to determine whether the current selection bounds completely overlaps the new elements.
	 * For sequence diagrams the height between the selection bounds and the bounds of the new elements may vary, but 
	 * the height is irrelevant to determining overlap.
	 * 
	 * @param pCurrentSelectionBounds The current selection bounds
	 * @param pNewElements Elements to be pasted
	 * @return Is the current selection bounds overlapping the new elements
	 */
	@Override
	public boolean isOverlapping(Rectangle pCurrentSelectionBounds, Iterable<DiagramElement> pNewElements) 
	{
		Rectangle newElementBounds = null;
		for (DiagramElement element : pNewElements) 
		{
			if (newElementBounds == null) 
			{
				newElementBounds = ViewerUtilities.getBounds(element);
			}
			newElementBounds = newElementBounds.add(ViewerUtilities.getBounds(element));
		}
		if (newElementBounds == null)
		{
			return false;
		}
		if (pCurrentSelectionBounds.getX() == newElementBounds.getX() && 
				pCurrentSelectionBounds.getY() == newElementBounds.getY() && 
				pCurrentSelectionBounds.getWidth() == newElementBounds.getWidth())
		{
			return true;
		}
		return false;
	}
}
