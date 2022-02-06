/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
 *******************************************************************************/

package ca.mcgill.cs.jetuml.viewers;

import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.ControlFlow;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.nodes.ImplicitParameterNodeViewer;

/**
 * A specialized viewer strategy for a sequence diagram.
 */
public class SequenceDiagramViewer extends DiagramViewer
{
	private static final ImplicitParameterNodeViewer IMPLICIT_PARAMETER_NODE_VIEWER = new ImplicitParameterNodeViewer();
	
	@Override
	protected Optional<Node> deepFindNode(Diagram pDiagram, Node pNode, Point pPoint)
	{
		Optional<Node> result = Optional.empty();
		if( pNode.getClass() == CallNode.class )
		{
			result = new ControlFlow(pDiagram).getCallees(pNode).stream()
				.map(node -> deepFindNode(pDiagram, node, pPoint))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
		}
		return result.or(() -> super.deepFindNode(pDiagram, pNode, pPoint));
	}

	/*
	 * Used during pasting to determine whether the current selection bounds completely overlaps the new elements. For
	 * sequence diagrams the height between the selection bounds and the bounds of the new elements may vary, but the
	 * height is irrelevant to determining overlap.
	 * 
	 * @param pCurrentSelectionBounds The current selection bounds
	 * 
	 * @param pNewElements Elements to be pasted
	 * 
	 * @return Is the current selection bounds overlapping the new elements
	 */
	@Override
	public boolean isOverlapping(Rectangle pCurrentSelectionBounds, Iterable<DiagramElement> pNewElements)
	{
		Rectangle newElementBounds = null;
		for( DiagramElement element : pNewElements )
		{
			if( newElementBounds == null )
			{
				newElementBounds = DiagramViewer.getBounds(element);
			}
			newElementBounds = newElementBounds.add(DiagramViewer.getBounds(element));
		}
		if( newElementBounds == null )
		{
			return false;
		}
		if( pCurrentSelectionBounds.getX() == newElementBounds.getX() &&
				pCurrentSelectionBounds.getY() == newElementBounds.getY() &&
				pCurrentSelectionBounds.getWidth() == newElementBounds.getWidth() )
		{
			return true;
		}
		return false;
	}

	/*
	 * This specialized version supports selecting implicit parameter nodes only by 
	 * selecting their top rectangle.
	 */
	@Override
	public Optional<Node> selectableNodeAt(Diagram pDiagram, Point pPoint)
	{
		Optional<Node> topRectangleSelected = pDiagram.rootNodes().stream()
			.filter(node -> node.getClass() == ImplicitParameterNode.class)
			.filter(node -> IMPLICIT_PARAMETER_NODE_VIEWER.getTopRectangle(node).contains(pPoint))
			.findFirst();
		return topRectangleSelected.or(() -> super.selectableNodeAt(pDiagram, pPoint));				
	}
}
