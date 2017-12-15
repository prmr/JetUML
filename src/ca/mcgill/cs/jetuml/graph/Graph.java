/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2017 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.graph;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import ca.mcgill.cs.jetuml.application.GraphModificationListener;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.edges.NoteEdge;
import ca.mcgill.cs.jetuml.graph.nodes.ChildNode;
import ca.mcgill.cs.jetuml.graph.nodes.NoteNode;
import ca.mcgill.cs.jetuml.graph.nodes.ParentNode;
import ca.mcgill.cs.jetuml.graph.nodes.PointNode;
import ca.mcgill.cs.jetuml.views.nodes.AbstractNodeView;

/**
 *  A graph consisting of nodes and edges.
 *  
 *  There are three modes for creating a graph:
 *  
 *  - Restoring nodes or edges. This mode is used for loading serialized elements
 *    and does not trigger any notifications. It is intended to be used for deserialization
 *    only. See methods restore{Node|Edge}
 *    
 *  - Inserting nodes or edges. This mode is used for inserting elements that had been
 *    previously created by were temporarily removed from a diagram. This is indented to be used
 *    for functionality such as undoing and copy/pasting. See methods insert{Node|Edge},
 *    which trigger notifications.
 *    
 *  - Adding nodes or edges. This mode is used for adding completely new elements, typically
 *    through UI actions. See methods add{Node|Edge}, which trigger notifications.
 */
public abstract class Graph
{
	protected GraphModificationListener aModificationListener; // Only access from notify* methods and setter
	protected ArrayList<Node> aRootNodes; // Only nodes without a parent are tracked by the graph.
	protected ArrayList<Edge> aEdges;
	protected transient ArrayList<Node> aNodesToBeRemoved;
	protected transient ArrayList<Edge> aEdgesToBeRemoved;
	private transient boolean aNeedsLayout;

	/**
	 * Constructs a graph with no nodes or edges.
	 */
	public Graph()
	{
		aRootNodes = new ArrayList<>();
		aEdges = new ArrayList<>();
		aNodesToBeRemoved = new ArrayList<>();
		aEdgesToBeRemoved = new ArrayList<>();
		aNeedsLayout = true;
	}
	
	/**
	 * Notifies the listener, if applicable, of a change to a property
	 * of one of the graph's elements.
	 * 
	 * @param pElement The element whose property changed.
	 * @param pProperty The name of the changed property.
	 * @param pOldValue The value of the property before the change.
	 * @param pNewValue The value of the property after the change.
	 */
	protected void notifyPropertyChanged(GraphElement pElement, String pProperty, Object pOldValue, Object pNewValue)
	{
		if( aModificationListener != null )
		{
			aModificationListener.propertyChanged(this, pElement, pProperty, pOldValue, pNewValue);
		}
	}
	
	private void notifyNodeAdded(Node pNode)
	{
		if( aModificationListener != null )
		{
			aModificationListener.nodeAdded(this, pNode);
		}
	}
	
	private void notifyNodeRemoved(Node pNode)
	{
		if( aModificationListener != null )
		{
			aModificationListener.nodeRemoved(this, pNode);
		}
	}
	
	private void notifyEdgeAdded(Edge pEdge)
	{
		if( aModificationListener != null )
		{
			aModificationListener.edgeAdded(this, pEdge);
		}
	}
	
	private void notifyEdgeRemoved(Edge pEdge)
	{
		if( aModificationListener != null )
		{
			aModificationListener.edgeRemoved(this, pEdge);
		}
	}
	
	private void notifyStartingCompoundOperation()
	{
		if( aModificationListener != null )
		{
			aModificationListener.startingCompoundOperation();
		}
	}
	
	private void notifyEndingCompoundOperation()
	{
		if( aModificationListener != null )
		{
			aModificationListener.finishingCompoundOperation();
		}
	}
	
	/**
	 * Sets the modification listener.
	 * @param pListener the single GraphModificationListener for this Graph.
	 */
	public void setGraphModificationListener(GraphModificationListener pListener)
	{
		aModificationListener = pListener;
	}

	/**
	 * @return The file extension (including the dot) corresponding
	 * to files of this diagram type.
	 */
	public abstract String getFileExtension();

	/**
	 * @return A short description of this diagram, usually
	 * ending in "Diagram", e.g., "State Diagram".
	 */
	public abstract String getDescription();
	
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

	private PointNode createPointNodeIfAllowed(Node pNode1, Edge pEdge, Point pPoint2)
	{
		if(pNode1 instanceof NoteNode && pEdge instanceof NoteEdge)
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
	
	/**
	 * Adds an edge to the graph that joins the nodes containing
	 * the given points. If the points aren't both inside nodes,
	 * then no edge is added.
	 * @param pEdge the edge to add
	 * @param pPoint1 a point in the starting node
	 * @param pPoint2 a point in the ending node
	 * @return true if the edge was connected
	 */
	public final boolean addEdge(Edge pEdge, Point pPoint1, Point pPoint2)
	{
		Node node1 = findNode(pPoint1);
		if( node1 == null )
		{
			return false;
		}
		
		Node node2 = findNode(pPoint2);
		if( node1 instanceof NoteNode )
		{
			node2 = createPointNodeIfAllowed(node1, pEdge, pPoint2);
		}
		
		if(!canConnect(pEdge, node1, node2, pPoint2))
		{
			return false;
		}

		pEdge.connect(node1, node2, this);
			
		// In case the down-call to addEdge introduces additional 
		// operations that should be compounded with the edge addition
		notifyStartingCompoundOperation();
		completeEdgeAddition(node1, pEdge, pPoint1, pPoint2);
		aEdges.add(pEdge);
		notifyEdgeAdded(pEdge);
		
		if(!aRootNodes.contains(pEdge.getEnd()) && pEdge.getEnd() instanceof PointNode )
		{
			aRootNodes.add(pEdge.getEnd());
		}
		aNeedsLayout = true;
		notifyEndingCompoundOperation();
		return true;
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
	 * @return True if the node was added.
	 */
	public boolean addNode(Node pNode, Point pPoint)
	{
		Rectangle bounds = pNode.view().getBounds();
		pNode.translate((int)(pPoint.getX() - bounds.getX()), (int)(pPoint.getY() - bounds.getY())); 
		if( !(pNode instanceof ChildNode) || ((ChildNode)pNode).getParent() == null )
		{
			aRootNodes.add(pNode);
		}
		notifyNodeAdded( pNode );
		aNeedsLayout = true;
		return true;
	}
	
	/**
	 * Inserts a previously-created node back into a diagram.
	 * The node is expected to have a position and possibly a parent. 
	 * Node insertion is intended to support operations such as undo/redo
	 * and copy and paste. Calling this method results in a node
	 * addition notification.
	 * 
	 * @param pNode The node to insert into the graph.
	 */
	public final void insertNode(Node pNode)
	{	
		if( !(pNode instanceof ChildNode && ((ChildNode)pNode).getParent() != null) )
		{	// The node does not have a parent, insert it as a root node
			aRootNodes.add(pNode);
		}
		else
		{	// Re-insert the node as a child of its parent
			((ChildNode)pNode).getParent().addChild((ChildNode)pNode);
		}
		aNeedsLayout = true;
		notifyNodeAdded( pNode );
	}

	/**
      * Finds a node containing the given point. Always returns
      * the deepest child and the last one in a list.
      * @param pPoint a point
      * @return a node containing pPoint or null if no nodes contain pPoint
      */
	public Node findNode(Point pPoint)
	{
		Node result = null;
		for( Node node : aRootNodes )
		{
			Node temp = deepFindNode(node, pPoint);
			if( temp != null )
			{
				result = temp;
			}
		}
		return result;
	}
	
	/**
	 * Find the "deepest" child that contains pPoint,
	 * where depth in measure in terms of distance from
	 * pNode along the parent-child relation.
	 * @param pNode The starting node for the search.
	 * @param pPoint The point to test for.
	 * @return The deepest child containing pPoint,
	 * or null if pPoint is not contained by pNode or 
	 * any of its children.
	 */
	protected Node deepFindNode( Node pNode, Point pPoint )
	{
		Node node = null;
		if( pNode instanceof ParentNode )
		{
			for( Node child : ((ParentNode) pNode).getChildren())
			{
				node = deepFindNode(child, pPoint);
				if( node != null )
				{
					return node;
				}
			}
		}
		if( pNode.view().contains(pPoint))
		{
			return pNode;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Finds an edge containing the given point.
	 * 
	 * @param pPoint a point
	 * @return an edge containing p or null if no edges contain p
	 */
	public Edge findEdge(Point pPoint)
	{
		for(Edge edge : aEdges)
		{
			if(edge.view().contains(pPoint))
			{
				return edge;
			}
		}
		return null;
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
		for(Edge edge : aEdges)
		{
			if((edge.getStart() == pNode || edge.getEnd() == pNode) && !aEdgesToBeRemoved.contains(edge))
			{
				toRemove.add(edge);
			}
		}
		Collections.reverse(toRemove);
		for(Edge edge : toRemove )
		{
			removeEdge(edge);
		}
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
	protected boolean existsEdge(Class<?> pType, Node pStart, Node pEnd)
	{
		assert pType !=null && pStart != null && pEnd != null;
		for( Edge edge : getEdges() )
		{
			if( edge.getClass() == pType && edge.getStart() == pStart && edge.getEnd() == pEnd )
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Draws the graph.
	 * @param pGraphics2D the graphics context
	 */
	public void draw(Graphics2D pGraphics2D)
	{
		layout();
		
		for( Node node : aRootNodes )
		{
			drawNode(node, pGraphics2D);
		}
		
		for( Edge edge : aEdges )
		{
			edge.view().draw(pGraphics2D);
		}
	}
	
	private void drawNode(Node pNode, Graphics2D pGraphics2D)
	{
		pNode.view().draw(pGraphics2D);
		if( pNode instanceof ParentNode )
		{
			for( Node node : ((ParentNode) pNode).getChildren())
			{
				drawNode(node, pGraphics2D);
			}
		}
	}

	/**
	 * Removes a node and all edges that start or end with that node.
	 * @param pNode the node to remove
	 */
	public void removeNode(Node pNode)
	{
		if(aNodesToBeRemoved.contains(pNode))
		{
			return;
		}
		notifyStartingCompoundOperation();
		aNodesToBeRemoved.add(pNode);
		
		if(pNode instanceof ParentNode)
		{
			ArrayList<ChildNode> children = new ArrayList<ChildNode>(((ParentNode) pNode).getChildren());
			//We create a shallow clone so deleting children does not affect the loop
			for(Node childNode: children)
			{
				removeNode(childNode);
			}
		}

		// Notify all nodes that pNode is being removed.
		for(Node node : aRootNodes)
		{
			removeFromParent( node, pNode );
		}
		
		// Notify all edges that pNode is being removed.
		for(Edge edge : aEdges)
		{
			if(edge.getStart() == pNode || edge.getEnd() == pNode)
			{
				removeEdge(edge);
			}
		}
		notifyNodeRemoved(pNode);
		notifyEndingCompoundOperation();
		aNeedsLayout = true;
	}
	
	private static void removeFromParent(Node pParent, Node pToRemove)
	{
		if( pParent instanceof ParentNode )
		{
			if( pToRemove instanceof ChildNode && ((ChildNode) pToRemove).getParent() == pParent )
			{
				((ParentNode) pParent).getChildren().remove(pToRemove);
				// We don't reassing the parent of the child to null in case the operation
				// is undone, at which point we'll need to know who the parent was.
			}
			for( Node child : ((ParentNode) pParent).getChildren() )
			{
				removeFromParent(child, pToRemove );
			}
		}
	}

	/**
	 * @param pElement The element we want to check is in the graph.
	 * @return True if pElement is a node or edge in this graph.
	 */
	public boolean contains( GraphElement pElement )
	{	
		if( aEdges.contains( pElement ))
		{
			return true;
		}
		for( Node node : aRootNodes )
		{
			if( containsNode( node, pElement))
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean containsNode(Node pTest, GraphElement pTarget)
	{
		if( pTest == pTarget )
		{
			return true;
		}
		else if( pTest instanceof ParentNode )
		{
			for( Node node : ((ParentNode) pTest).getChildren())
			{
				if( containsNode(node, pTarget))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Removes an edge from the graph.
	 * @param pEdge the edge to remove
	 */
	public void removeEdge(Edge pEdge)
	{
		if (aEdgesToBeRemoved.contains(pEdge))
		{
			return;
		}
		aEdgesToBeRemoved.add(pEdge);
		notifyEdgeRemoved(pEdge);
		for(int i = aRootNodes.size() - 1; i >= 0; i--)
		{
			Node n = aRootNodes.get(i);
			if( n instanceof NoteEdge )
			{
				if(pEdge.getStart() == n)
				{
					removeNode(pEdge.getEnd());
				}
			}
		}
		aNeedsLayout = true;
	}

	/**
	 * Causes the layout of the graph to be recomputed.
	 */
	public void requestLayout()
	{
		aNeedsLayout = true;
	}

	/**
	 * Computes the layout of the graph.
	 * If you override this method, you must first call 
	 * <code>super.layout</code>.
	 */
	protected void layout()
	{
		if(!aNeedsLayout)
		{
			return;
		}
		aRootNodes.removeAll(aNodesToBeRemoved);
		aEdges.removeAll(aEdgesToBeRemoved);
		aNodesToBeRemoved.clear();
		aEdgesToBeRemoved.clear();

		for(Node node : aRootNodes)
		{
			node.view().layout(this);
		}
		aNeedsLayout = false;
	}

	/**
	 * Gets the smallest rectangle enclosing the graph.
	 * 
	 * @return the bounding rectangle
	 */
	public Rectangle getBounds()
	{
		Rectangle bounds = null;
		for(Node node : aRootNodes )
		{
			if(bounds == null)
			{
				bounds = node.view().getBounds();
			}
			else
			{
				bounds = bounds.add(node.view().getBounds());
			}
		}
		for(Edge edge : aEdges)
		{
			bounds = bounds.add(edge.view().getBounds());
		}
		if(bounds == null )
		{
			return new Rectangle(0, 0, 0, 0);
		}
		else
		{
			return new Rectangle(bounds.getX(), bounds.getY(), 
					bounds.getWidth() + AbstractNodeView.SHADOW_GAP, bounds.getHeight() + AbstractNodeView.SHADOW_GAP);
		}
	}

	/**
	 * Gets the node types of a particular graph type.
	 * @return an array of node prototypes
	 */   
	public abstract Node[] getNodePrototypes();

	/**
	 * Gets the edge types of a particular graph type.
	 * @return an array of edge prototypes
	 */   
	public abstract Edge[] getEdgePrototypes();

	/**
	 * Gets the nodes of this graph.
	 * @return an unmodifiable collection of the nodes
	 */
	public Collection<Node> getRootNodes()
	{ return aRootNodes; }

	/**
	 * Gets the edges of this graph.
	 * @return an unmodifiable collection of the edges
	 */
	public Collection<Edge> getEdges() 
	{ return aEdges; }
	
	/**
	 * @param pNode the node to test for
	 * @return All the edges connected to pNode
	 * pNode not null
	 */
	public Collection<Edge> getEdges(Node pNode)
	{
		assert pNode != null;
		Collection<Edge> lReturn = new ArrayList<>();
		for( Edge edge : aEdges )
		{
			if( edge.getStart() == pNode || edge.getEnd() == pNode )
			{
				lReturn.add(edge);
			}
		}
		return lReturn;
	}

	/**
	 * Restores a root node to this graph. It is assume that
	 * restoring the node is a valid operation, and that the 
	 * node properties store its proper position. The root node
	 * restored should be already linked to all its children (for
	 * instance by a deserializer). This operation does not 
	 * trigger any notifications.
	 * 
	 * @param pNode The node to restore
	 */
	public void restoreRootNode(Node pNode)
	{
		aRootNodes.add(pNode); 
	}
	
	/**
	 * Adds an edge to this graph. This method does no validation,
	 * but triggers a notification.
	 * 
	 * @param pEdge the edge to insert.
	 */
	public void insertEdge(Edge pEdge)
	{
		aEdges.add(pEdge);
		notifyEdgeAdded(pEdge);
	}
	
	/**
	 * Restores an edge to this graph. It is assume that
	 * restoring the edge is a valid operation. This operation does not 
	 * trigger any notifications. Note that we pass the edge's
	 * two endpoint because these values are not serializable
	 * due to the absence of set methods for them in the Edge interface.
	 * 
	 * @param pEdge The edge to restore
	 * @param pStart The starting node for the edge
	 * @param pEnd Then end 
	 */
	public void restoreEdge(Edge pEdge, Node pStart, Node pEnd)
	{
		pEdge.connect(pStart, pEnd, this);
		aEdges.add(pEdge);
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
	protected boolean canConnect(Edge pEdge, Node pNode1, Node pNode2, Point pPoint2)
	{
		if( pNode2 == null )
		{
			return false;
		}
		if( existsEdge(pEdge.getClass(), pNode1, pNode2))
		{
			return false;
		}
		if((pNode2 instanceof NoteNode || pNode1 instanceof NoteNode) && !(pEdge instanceof NoteEdge))
		{
			return false;
		}
		if( pEdge instanceof NoteEdge && !(pNode1 instanceof NoteNode || pNode2 instanceof NoteNode))
		{
			return false;
		}
		return true;
	}
}


