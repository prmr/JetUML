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
import java.util.List;
import java.util.Stack;

import ca.mcgill.cs.jetuml.application.UndoManager;
import ca.mcgill.cs.jetuml.commands.AddEdgeCommand;
import ca.mcgill.cs.jetuml.commands.AddNodeCommand;
import ca.mcgill.cs.jetuml.commands.ChangePropertyCommand;
import ca.mcgill.cs.jetuml.commands.DeleteNodeCommand;
import ca.mcgill.cs.jetuml.commands.RemoveEdgeCommand;
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
	private UndoManager aUndoManager;
	
	public DiagramBuilder( Diagram pDiagram )
	{
		aDiagram = pDiagram;
	}
	
	public void setUndoManager(UndoManager pManager)
	{
		aUndoManager = pManager;
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
			notifyEdgeRemoved(pEdge);
			removePointNodeIfApplicable(pEdge);
			aDiagram.requestLayout();
		}
	}
	
	/**
	 * Removes all edges in the graph that have pNode as a start
	 * or end node. The edges are removed in an order that is the 
	 * reverse of the one in which they were added, so that this
	 * method can directly support the undo functionality. 
	 * Note that layout() needs to be called before
	 * the change has effect.
	 * 
	 * @param pNode The target node.
	 */
	public void removeAllEdgesConnectedTo(Node pNode)
	{
		assert pNode != null;
		ArrayList<Edge> toRemove = new ArrayList<Edge>();
		for(Edge edge : aDiagram.getEdges())
		{
			if ((edge.getStart() == pNode || edge.getEnd() == pNode) && !aDiagram.getEdgesToBeRemoved().contains(edge))
			{
				toRemove.add(edge);
			}
		}
		Collections.reverse(toRemove);
		for(Edge edge : toRemove)
		{
			removeEdge(edge);
		}
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
	
	/**
	 * Removes a node and all edges that start or end with that node.
	 * @param pNode the node to remove
	 */
	public final void removeNode(Node pNode)
	{
		if(aDiagram.getNodesToBeRemoved().contains(pNode))
		{
			return;
		}
		notifyStartingCompoundOperation();
		aDiagram.getNodesToBeRemoved().add(pNode);
		
		if(pNode instanceof ParentNode)
		{
			ArrayList<ChildNode> children = new ArrayList<ChildNode>(((ParentNode) pNode).getChildren());
			//We create a shallow clone so deleting children does not affect the loop
			for(Node childNode: children)
			{
				removeNode(childNode);
			}
		}
		
		// Remove all edges connected to this node
		removeAllEdgesConnectedTo(pNode);

		// Notify all nodes that pNode is being removed.
		for(Node node : aDiagram.getRootNodes())
		{
			removeFromParent( node, pNode );
		}
		
		// Notify all edges that pNode is being removed.
		for(Edge edge : aDiagram.getEdges())
		{
			if(edge.getStart() == pNode || edge.getEnd() == pNode)
			{
				removeEdge(edge);
			}
		}
		notifyNodeRemoved(pNode);
		notifyEndingCompoundOperation();
		aDiagram.requestLayout();
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
					removeNode(pEdge.getEnd());
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
		notifyStartingCompoundOperation();
		completeEdgeAddition(node1, pEdge, pPoint1, pPoint2);
		aDiagram.getEdges().add(pEdge);
		notifyEdgeAdded(pEdge);
		if(!aDiagram.getRootNodes().contains(pEdge.getEnd()) && pEdge.getEnd() instanceof PointNode)
		{
			aDiagram.getRootNodes().add(pEdge.getEnd());
		}
		notifyEndingCompoundOperation();
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
	 * Adds a newly created node to the graph so that the top left corner of
	 * the bounding rectangle is at the given point. This method
	 * is intended to be used to add nodes that were never part
	 * of the graph, from the GUI. To add nodes recovered from
	 * deserialization, use restoreNode. To add nodes recovered
	 * from in-application operations such as undoing and pasting,
	 * use insertNode. This method assumes the node does not
	 * have a parent of a child.
	 * 
	 * @param pNode the node to add
	 * @param pPoint the desired location
	 * @param pMaxWidth the maximum width of the panel
	 * @param pMaxHeight the maximum height of the panel
	 * @return True if the node was added.
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
		notifyNodeAdded(pNode);
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
	 * Creates an operation that removes all the elements in pElements.
	 * 
	 * @param pElements The elements to remove.
	 * @return The requested operation.
	 * @pre pElements != null.
	 */
	public final DiagramOperation createRemoveElementsOperation(Iterable<DiagramElement> pElements)
	{
		assert pElements != null;
		Stack<Node> nodes = new Stack<>();
		CompoundOperation result = new CompoundOperation();
		for(DiagramElement element : pElements)
		{
			if(element instanceof Node)
			{
				nodes.add((Node) element);
			}
			else if(element instanceof Edge)
			{
				result.add(createRemoveEdgeOperation((Edge) element));
			}
		}
		while(!nodes.empty())
		{
			result.add(createRemoveNodeOperation(nodes.pop()));
		}
		return result;
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
				()-> ((ChildNode)pNode).getParent().addChild((ChildNode)pNode)));
		}
		else
		{
			result.add(new SimpleOperation( ()-> aDiagram.atomicRemoveRootNode(pNode),
					()-> aDiagram.atomicRemoveRootNode(pNode)));
		}
		return result;
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
	protected boolean hasParent(Node pNode)
	{
		return (pNode instanceof ChildNode) && ((ChildNode)pNode).getParent() != null;
	}
	
	/**
	 * Notifies the listener, if applicable, of a change to a property
	 * of one of the graph's elements.
	 * 
	 * @param pElement The element whose property changed.
	 * @param pProperty The name of the changed property.
	 * @param pOldValue The value of the property before the change.
	 */
	protected final void notifyPropertyChanged(DiagramElement pElement, String pProperty, Object pOldValue)
	{
		assert aUndoManager != null;
		Property property = pElement.properties().get(pProperty);
		aUndoManager.add(new ChangePropertyCommand(property, pOldValue, property.get()));
	}
	
	public final void notifyNodeAdded(Node pNode)
	{
		if( aUndoManager != null )
		{
			aUndoManager.add(new AddNodeCommand(aDiagram, pNode));
		}
	}
	
	private void notifyNodeRemoved(Node pNode)
	{
		if(aUndoManager != null)
		{
			aUndoManager.add(new DeleteNodeCommand(aDiagram, pNode));
		}
	}
	
	public final void notifyEdgeAdded(Edge pEdge)
	{
		if(aUndoManager != null)
		{
			aUndoManager.add(new AddEdgeCommand(aDiagram, pEdge));
		}
	}
	
	private void notifyEdgeRemoved(Edge pEdge)
	{
		if(aUndoManager != null)
		{
			aUndoManager.add(new RemoveEdgeCommand(aDiagram, pEdge));
		}
	}
	
	private final void notifyStartingCompoundOperation()
	{
		if(aUndoManager != null)
		{
			aUndoManager.startTracking();
		}
	}
	
	private final void notifyEndingCompoundOperation()
	{
		if(aUndoManager != null)
		{
			aUndoManager.endTracking();
		}
	}
}
