/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.diagram.builder;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;

public class DiagramBuilder
{
	protected final Diagram aDiagram;
	
	public DiagramBuilder( Diagram pDiagram )
	{
		aDiagram = pDiagram;
	}
	
	/**
	 * True by default. Override to provide cases where this should be false.
	 * 
	 * @param pNode The node to add if possible. 
	 * @param pRequestedPosition The requested position for the node.
	 * @return True if it is possible to add pNode at position pPoint.
	 */
	public boolean canAdd(Node pNode, Point pRequestedPosition)
	{
		return true;
	}
	
	/**
	 * Adds a newly created node to the diagram, if it does not have a parent. If
	 * the node has a parent, does not do anything.
	 * 
	 * @param pNode The node to add. Not null.
	 * @param pRequestedPosition The desired position of the node in the diagram.
	 * @param pMaxWidth the maximum width of the diagram.
	 * @param pMaxHeight the maximum height of the diagram.
	 */
	public void addNode(Node pNode, Point pRequestedPosition, int pMaxWidth, int pMaxHeight)
	{
		assert pNode != null && pMaxWidth >= 0 && pMaxHeight >= 0;
		Rectangle bounds = pNode.view().getBounds();
		Point position = computePosition(bounds, pRequestedPosition, new Dimension(pMaxWidth, pMaxHeight));
		pNode.translate(position.getX() - bounds.getX(), position.getY() - bounds.getY());
		if(!hasParent(pNode))
		{
			aDiagram.restoreRootNode(pNode);
		}
	}
	
	private Point computePosition(Rectangle pBounds, Point pRequestedPosition, Dimension pDiagramSize)
	{
		int newX = pRequestedPosition.getX();
		int newY = pRequestedPosition.getY();
		if(newX + pBounds.getWidth() > pDiagramSize.getWidth())
		{
			newX = pDiagramSize.getWidth() - pBounds.getWidth();
		}
		if (newY + pBounds.getHeight() > pDiagramSize.getHeight())
		{
			newY = pDiagramSize.getHeight() - pBounds.getHeight();
		}
		return new Point(newX, newY);
	}
	
	/**
	 * @param pNode A node to check for parenthood.
	 * @return True iif pNode has a non-null parent.
	 */
	protected boolean hasParent(Node pNode)
	{
		return (pNode instanceof ChildNode) && ((ChildNode)pNode).getParent() != null;
	}
}
