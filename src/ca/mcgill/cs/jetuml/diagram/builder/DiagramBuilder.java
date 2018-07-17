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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.Property;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ParentNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;

public abstract class DiagramBuilder
{
	protected final Diagram aDiagram;
	
	protected DiagramBuilder( Diagram pDiagram )
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
	
	private CompoundOperation createRemoveAllEdgesConnectedToOperation(List<Node> pNodes)
	{
		assert pNodes != null;
		ArrayList<Edge> toRemove = new ArrayList<Edge>();
		for(Edge edge : aDiagram.getEdges())
		{
			if(pNodes.contains(edge.getStart() ) || pNodes.contains(edge.getEnd()))
			{
				toRemove.add(edge);
			}
		}
		Collections.reverse(toRemove);
		CompoundOperation result = new CompoundOperation();
		for(Edge edge : toRemove)
		{
			result.add(new SimpleOperation(()-> aDiagram.atomicRemoveEdge(edge), 
					()-> aDiagram.atomicAddEdge(edge)));
		}
		return result;
	}
	
	private static List<Node> getNodeAndAllChildren(Node pNode)
	{
		List<Node> result = new ArrayList<>();
		result.add(pNode);
		if( pNode instanceof ParentNode )
		{
			for( ChildNode child : ((ParentNode)pNode).getChildren() )
			{
				result.addAll(getNodeAndAllChildren(child));
			}
		}
		return result;
	}
	
	public static void removeFromParent(Node pParent, Node pToRemove)
	{
		if(pParent instanceof ParentNode)
		{
			if (pToRemove instanceof ChildNode && ((ChildNode) pToRemove).getParent() == pParent)
			{
				((ParentNode) pParent).getChildren().remove(pToRemove);
				// We don't reassing the parent of the child to null in case the operation
				// is undone, at which point we'll need to know who the parent was.
			}
			for (Node child : ((ParentNode) pParent).getChildren())
			{
				removeFromParent(child, pToRemove);
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
	 * The default behavior is to position the node so it entirely fits in the diagram, then 
	 * add it as a root node.
	 * @param pNode The node to add.
	 * @param pRequestedPosition A point that is the requested position of the node.
	 * @param pMaxWidth The maximum width, in pixels, of the diagram.
	 * @param pMaxHeight The maximum height, in pixles, of the diagram.
	 */
	public DiagramOperation createAddNodeOperation(Node pNode, Point pRequestedPosition, int pMaxWidth, int pMaxHeight)
	{
		assert pNode != null && pRequestedPosition != null && pMaxWidth >= 0 && pMaxHeight >= 0;
		Rectangle bounds = pNode.view().getBounds();
		Point position = computePosition(bounds, pRequestedPosition, new Dimension(pMaxWidth, pMaxHeight));
		pNode.translate(position.getX() - bounds.getX(), position.getY() - bounds.getY());
		return new SimpleOperation( ()-> aDiagram.atomicAddRootNode(pNode), 
				()-> aDiagram.atomicRemoveRootNode(pNode));
	}
	
	/**
	 * Creates an operation that adds all the elements in pElements. Assumes all nodes
	 * are root nodes and all edges are connected, and that there are no dangling references.
	 * 
	 * @param pElements The elements to add.
	 * @return The requested operation
	 * @pre pElements != null
	 */
	public final DiagramOperation createAddElementsOperation(Iterable<DiagramElement> pElements)
	{
		CompoundOperation operation = new CompoundOperation();
		for( DiagramElement element : pElements)
		{
			if( element instanceof Node )
			{
				operation.add(new SimpleOperation(
						()-> aDiagram.atomicAddRootNode((Node)element),
						()-> aDiagram.atomicRemoveRootNode((Node)element)));
			}
			else if( element instanceof Edge)
			{
				operation.add(new SimpleOperation(
						()-> aDiagram.atomicAddEdge((Edge)element),
						()-> aDiagram.atomicRemoveEdge((Edge)element)));
			}
		}
		
		return operation;
	}
	
	/**
	 * @param pElement The element to check.
	 * @return The list of elements that have to be deleted with pElement.
	 */
	protected List<DiagramElement> findCoDeletions(DiagramElement pElement)
	{
		ArrayList<DiagramElement> result = new ArrayList<>();
		if( pElement.getClass() == PointNode.class )
		{
			for( Edge edge : aDiagram.getEdges((Node)pElement))
			{
				result.add(edge);
			}
		}
		if( pElement.getClass() == NoteEdge.class )
		{
			Edge edge = (Edge)pElement;
			if( edge.getStart().getClass() == PointNode.class )
			{
				result.add(edge.getStart());
			}
			if( edge.getEnd().getClass() == PointNode.class )
			{
				result.add(edge.getEnd());
			}
		}
		if( pElement instanceof Node )
		{
			List<Node> descendents = getNodeAndAllChildren((Node)pElement);
			for(Edge edge : aDiagram.getEdges())
			{
				if(descendents.contains(edge.getStart() ) || descendents.contains(edge.getEnd()))
				{
					result.add(edge);
				}
			}
		}
		return result;
	}
	
	/**
	 * Creates an operation that removes all the elements in pElements.
	 * 
	 * @param pElements The elements to remove.
	 * @return The requested operation.
	 * @pre pElements != null.
	 */
	public final DiagramOperation createDeleteElementsOperation(Iterable<DiagramElement> pElements)
	{
		assert pElements != null;
		HashSet<DiagramElement> toDelete = new HashSet<>();
		for( DiagramElement element : pElements)
		{
			toDelete.add(element);
			toDelete.addAll(findCoDeletions(element));
		}
		CompoundOperation result = new CompoundOperation();
		for( DiagramElement element : toDelete)
		{
			if( element instanceof Edge )
			{
				result.add(new SimpleOperation(
						()-> aDiagram.atomicRemoveEdge((Edge)element),
						()-> aDiagram.atomicAddEdge((Edge)element)));
			}
			else if( element instanceof Node )
			{
				if(hasParent((Node) element))
				{
					result.add(new SimpleOperation(
						createDetachOperation((ChildNode)element),
						createReinsertOperation((ChildNode)element)));
				}
				else
				{
					result.add(new SimpleOperation(
						()-> aDiagram.atomicRemoveRootNode((Node)element),
						()-> aDiagram.atomicAddRootNode((Node)element)));
				}
			}
		}
		return result;
	}
	
	/**
	 * Creates an operation to change the value of a property on a diagram element.
	 * @param pProperty The property to change.
	 * @param pNewValue The value to change to.
	 * @return The requested operation.
	 * @pre pProperty != null.
	 * @pre pNewValue != null.
	 */
	public final DiagramOperation createPropertyChangeOperation(Property pProperty, Object pNewValue)
	{
		assert pProperty != null && pNewValue != null;
		Object oldValue = pProperty.get();
		return new SimpleOperation(()-> pProperty.set(pNewValue), ()-> pProperty.set(oldValue));
	}
	
	/**
	 * Create an operation to move a node.
	 * 
	 * @param pNode The node to move.
	 * @param pX The amount to move the node in the x-coordinate.
	 * @param pY The amount to move the node in the y-coordinate.
 	 * @return The requested operation.
 	 * @pre pNode != null.
	 */
	public final DiagramOperation createMoveNodeOperation(Node pNode, int pX, int pY)
	{
		return new SimpleOperation(
				()-> pNode.translate(pX, pY),
				()-> pNode.translate(-pX, -pY));
	}
	
	public DiagramOperation createAddEdgeOperation(Edge pEdge, Point pPoint1, Point pPoint2)
	{ 
		assert canAdd(pEdge, pPoint1, pPoint2);
		Node node1 = aDiagram.findNode(pPoint1);
		Node node2 = aDiagram.findNode(pPoint2);
		CompoundOperation result = new CompoundOperation();
		if(node1 instanceof NoteNode && pEdge instanceof NoteEdge)
		{
			node2 = new PointNode();
			node2.translate(pPoint2.getX(), pPoint2.getY());
			Node end = node2; // Effectively final to include in closure
			result.add(new SimpleOperation(()-> aDiagram.atomicAddRootNode(end),
					()-> aDiagram.atomicRemoveRootNode(end)));
		}
		assert node2 != null;
		pEdge.connect(node1, node2, aDiagram);
		addComplementaryEdgeAdditionOperations(result, pEdge, pPoint1, pPoint2);
		result.add(new SimpleOperation(()-> aDiagram.atomicAddEdge(pEdge),
				()-> aDiagram.atomicRemoveEdge(pEdge)));
		return result;
	}
	
	protected void addComplementaryEdgeAdditionOperations(CompoundOperation pOperation, Edge pEdge, Point pStart, Point pEnd)
	{}

	/**
	 * Creates an operation to remove pNode. If the node is a root node,
	 * then the node is removed from the diagram. If the node is a child
	 * node, then it is detached from the parent. All edges connected to
	 * pNode or one of its children are removed as a result.
	 * 
	 * @param pNode The node to remove.
	 * @return An operation to remove the node and all connected edges.
	 */
	public DiagramOperation createRemoveNodeOperation(Node pNode)
	{
		assert pNode != null;
		CompoundOperation result = createRemoveAllEdgesConnectedToOperation(getNodeAndAllChildren(pNode));
		if( isChild( pNode ))
		{
			result.add(new SimpleOperation(()-> ((ChildNode)pNode).getParent().removeChild((ChildNode)pNode),
				createReinsertOperation((ChildNode)pNode)));
		}
		else
		{
			result.add(new SimpleOperation( ()-> aDiagram.atomicRemoveRootNode(pNode),
					()-> aDiagram.atomicAddRootNode(pNode)));
		}
		return result;
	}
	
	private static Runnable createReinsertOperation(ChildNode pNode)
	{
		ParentNode parent = pNode.getParent();
		int index = parent.getChildren().indexOf(pNode);
		return ()-> parent.addChild(index, pNode);
	}
	
	private static Runnable createDetachOperation(ChildNode pNode)
	{
		ParentNode parent = pNode.getParent();
		return ()-> parent.removeChild(pNode);
	}
	
	public DiagramOperation createRemoveEdgeOperation(Edge pEdge)
	{
		SimpleOperation remove = new SimpleOperation( ()-> aDiagram.atomicRemoveEdge(pEdge),
				()-> aDiagram.atomicAddEdge(pEdge));
		if( pEdge.getEnd() instanceof PointNode )
		{
			CompoundOperation result = new CompoundOperation();
			final Node end = pEdge.getEnd();
			result.add( new SimpleOperation( ()-> aDiagram.atomicRemoveRootNode(end),
					()-> aDiagram.atomicAddRootNode(end)));
			result.add(remove);
			return result;
		}
		else
		{
			return remove;
		}
	}
	
	private static boolean isChild(Node pNode)
	{
		return pNode instanceof ChildNode && ((ChildNode)pNode).getParent() != null;
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
	protected static boolean hasParent(Node pNode)
	{
		return (pNode instanceof ChildNode) && ((ChildNode)pNode).getParent() != null;
	}
}
