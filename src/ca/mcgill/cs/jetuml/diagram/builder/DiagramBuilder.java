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
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;

public abstract class DiagramBuilder
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
	 * @param pEdge The requested edge
	 * @param pPoint1 A requested start point
	 * @param pPoint2 A requested end point
	 * @return True if it's possible to add an edge of this type given the requested points.
	 */
	public final boolean canAdd(Edge pEdge, Point pPoint1, Point pPoint2)
	{
		Node node1 = aDiagram.findNode(pPoint1);
		if(node1 == null)
		{
			return false;
		}
		
		Node node2 = aDiagram.findNode(pPoint2);
		if(node1 instanceof NoteNode && pEdge instanceof NoteEdge)
		{
			return true; // We can always create a point node.
		}
		return canConnect(pEdge, node1, node2, pPoint2);
	}
	
	/**
	 * Checks whether it is legal to connect pNode1 to pNode2 through
	 * pEdge based strictly on the type of nodes and edges. 
	 * This implementation only provides the logic valid across
	 * all diagram types. Override for diagram-specific rules.
	 * @param pEdge The edge to be added
	 * @param pNode1 The first node
	 * @param pNode2 The second node
	 * @param pPoint2 The point where the edge is supposed to be terminated
	 * @return True if the edge can legally connect node1 to node2
	 */
	public boolean canConnect(Edge pEdge, Node pNode1, Node pNode2, Point pPoint2)
	{
		if(pNode2 == null)
		{
			return false;
		}
		if(existsEdge(pEdge.getClass(), pNode1, pNode2))
		{
			return false;
		}
		if((pNode2 instanceof NoteNode || pNode1 instanceof NoteNode) && !(pEdge instanceof NoteEdge))
		{
			return false;
		}
		if(pEdge instanceof NoteEdge && !(pNode1 instanceof NoteNode || pNode2 instanceof NoteNode))
		{
			return false;
		}
		return true;
	}
	
	/**
	 * Returns true iif there exists an edge of type pType between
	 * nodes pStart and pEnd. The direction matter, and the type
	 * testing is for the exact type pType, without using polymorphism.
	 * @param pType The type of edge to check for.
	 * @param pStart The start node.
	 * @param pEnd The end node.
	 * @return True if and only if there is an edge of type pType that
	 * starts at node pStart and ends at node pEnd.
	 */
	protected final boolean existsEdge(Class<?> pType, Node pStart, Node pEnd)
	{
		assert pType !=null && pStart != null && pEnd != null;
		for(Edge edge : aDiagram.getEdges())
		{
			if (edge.getClass() == pType && edge.getStart() == pStart && edge.getEnd() == pEnd)
			{
				return true;
			}
		}
		return false;
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
