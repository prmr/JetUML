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

import java.util.List;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
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
	
	public void removeEdge(Edge pEdge)
	{
		if(!aDiagram.getEdgesToBeRemoved().contains(pEdge))
		{
			aDiagram.getEdgesToBeRemoved().add(pEdge);
			aDiagram.notifyEdgeRemoved(pEdge);
			removePointNodeIfApplicable(pEdge);
			aDiagram.requestLayout();
		}
	}
	
	private PointNode createPointNodeIfAllowed(Node pNode1, Edge pEdge, Point pPoint2)
	{
		if (pNode1 instanceof NoteNode && pEdge instanceof NoteEdge)
		{
			PointNode lReturn = new PointNode();
			lReturn.translate(pPoint2.getX(), pPoint2.getY());
			return lReturn;
		}
		else
		{
			return null;
		}
	}
	
	private void removePointNodeIfApplicable(Edge pEdge)
	{
		List<Node> rootNodes = (List<Node>)aDiagram.getRootNodes();
		for(int i = rootNodes.size() - 1; i >= 0; i--)
		{
			Node node = rootNodes.get(i);
			if(node instanceof NoteNode)
			{
				if(pEdge.getStart() == node)
				{
					aDiagram.removeNode(pEdge.getEnd());
				}
			}
		}
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
	 * Adds an edge to the graph that joins the nodes containing
	 * the given points. If the points aren't both inside nodes,
	 * then no edge is added.
	 * @param pEdge the edge to add
	 * @param pPoint1 a point in the starting node
	 * @param pPoint2 a point in the ending node
	 * @return true if the edge was connected
	 */
	public final void addEdge(Edge pEdge, Point pPoint1, Point pPoint2)
	{
		assert canAdd(pEdge, pPoint1, pPoint2);
		Node node1 = aDiagram.findNode(pPoint1);
		Node node2 = aDiagram.findNode(pPoint2);
		if(node1 instanceof NoteNode)
		{
			node2 = createPointNodeIfAllowed(node1, pEdge, pPoint2);
		}
		pEdge.connect(node1, node2, aDiagram);
			
		// In case the down-call to addEdge introduces additional 
		// operations that should be compounded with the edge addition
		aDiagram.notifyStartingCompoundOperation();
		completeEdgeAddition(node1, pEdge, pPoint1, pPoint2);
		aDiagram.getEdges().add(pEdge);
		aDiagram.notifyEdgeAdded(pEdge);
		if(!aDiagram.getRootNodes().contains(pEdge.getEnd()) && pEdge.getEnd() instanceof PointNode)
		{
			aDiagram.getRootNodes().add(pEdge.getEnd());
		}
		aDiagram.notifyEndingCompoundOperation();
		aDiagram.requestLayout();
	}
	
	/**
	 * If certain types of diagrams require additional behavior
	 * following the addition of an edge to a graph, they can
	 * override this method to perform that behavior.
	 * @param pOrigin The origin node 
	 * @param pEdge The edge to add
	 * @param pPoint1 a point in the starting node
	 * @param pPoint2 a point in the end node.
	 */
	protected void completeEdgeAddition(Node pOrigin, Edge pEdge, Point pPoint1, Point pPoint2)
	{}

	
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
		aDiagram.requestLayout();
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
