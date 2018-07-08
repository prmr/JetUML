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
package ca.mcgill.cs.jetuml.diagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ca.mcgill.cs.jetuml.application.GraphModificationListener;
import ca.mcgill.cs.jetuml.diagram.builder.DiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ParentNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.scene.canvas.GraphicsContext;

/**
 *  A diagram consisting of nodes and edges.
 *  
 *  There are three modes for creating a diagram:
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
public abstract class Diagram implements DiagramData
{
	protected GraphModificationListener aModificationListener; // Only access from notify* methods and setter
	protected ArrayList<Node> aRootNodes; // Only nodes without a parent are tracked by the graph.
	protected ArrayList<Edge> aEdges;
	protected transient ArrayList<Node> aNodesToBeRemoved;
	protected transient ArrayList<Edge> aEdgesToBeRemoved;
	private transient boolean aNeedsLayout;
	protected transient DiagramBuilder aBuilder;

	/**
	 * Constructs a graph with no nodes or edges.
	 */
	public Diagram()
	{
		aRootNodes = new ArrayList<>();
		aEdges = new ArrayList<>();
		aNodesToBeRemoved = new ArrayList<>();
		aEdgesToBeRemoved = new ArrayList<>();
		aNeedsLayout = true;
	}
	
	@Override
	public Iterable<DiagramElement> allElements()
	{
		ArrayList<DiagramElement> result = new ArrayList<>(aRootNodes);
		result.addAll(aEdges);
		return result;
	}
	
	@Override
	public Iterable<Node> rootNodes()
	{
		return aRootNodes;
	}
	
	@Override
	public Iterable<Edge> edges()
	{
		return aEdges;
	}
	
	/**
	 * True by default. Override to provide cases where this should be false.
	 * 
	 * @param pNode The node to add if possible. 
	 * @param pRequestedPosition The requested position for the node.
	 * @return True if it is possible to add pNode at position pPoint.
	 */
	public final boolean canAdd(Node pNode, Point pRequestedPosition)
	{
		return aBuilder.canAdd(pNode, pRequestedPosition);
	}
	
	public final boolean canAdd(Edge pEdge, Point pPoint1, Point pPoint2)
	{
		return aBuilder.canAdd(pEdge, pPoint1, pPoint2);
	}
	
	
	
	/**
	 * Notifies the listener, if applicable, of a change to a property
	 * of one of the graph's elements.
	 * 
	 * @param pElement The element whose property changed.
	 * @param pProperty The name of the changed property.
	 * @param pOldValue The value of the property before the change.
	 */
	public final void notifyPropertyChanged(DiagramElement pElement, String pProperty, Object pOldValue)
	{
		if (aModificationListener != null)
		{
			aModificationListener.propertyChanged(pElement.properties().get(pProperty), pOldValue);
		}
	}
	
	private void notifyNodeAdded(Node pNode)
	{
		if (aModificationListener != null)
		{
			aModificationListener.nodeAdded(this, pNode);
		}
	}
	
	private void notifyNodeRemoved(Node pNode)
	{
		if (aModificationListener != null)
		{
			aModificationListener.nodeRemoved(this, pNode);
		}
	}
	
	public final void notifyEdgeAdded(Edge pEdge)
	{
		if (aModificationListener != null)
		{
			aModificationListener.edgeAdded(this, pEdge);
		}
	}
	
	public void notifyEdgeRemoved(Edge pEdge)
	{
		if (aModificationListener != null)
		{
			aModificationListener.edgeRemoved(this, pEdge);
		}
	}
	
	public final void notifyStartingCompoundOperation()
	{
		if (aModificationListener != null)
		{
			aModificationListener.startingCompoundOperation();
		}
	}
	
	public final void notifyEndingCompoundOperation()
	{
		if (aModificationListener != null)
		{
			aModificationListener.finishingCompoundOperation();
		}
	}
	
	/**
	 * Sets the modification listener.
	 * @param pListener the single GraphModificationListener for this Diagram.
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
		aBuilder.addEdge(pEdge, pPoint1, pPoint2);
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
	public final void addNode(Node pNode, Point pPoint, int pMaxWidth, int pMaxHeight)
	{
		aBuilder.addNode(pNode, pPoint, pMaxWidth, pMaxHeight);
		notifyNodeAdded(pNode);
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
		if (!(pNode instanceof ChildNode && ((ChildNode)pNode).getParent() != null))
		{	// The node does not have a parent, insert it as a root node
			aRootNodes.add(pNode);
		}
		else
		{	// Re-insert the node as a child of its parent
			((ChildNode)pNode).getParent().addChild((ChildNode)pNode);
		}
		aNeedsLayout = true;
		notifyNodeAdded(pNode);
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
		for (Node node : aRootNodes)
		{
			Node temp = deepFindNode(node, pPoint);
			if (temp != null)
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
	protected Node deepFindNode(Node pNode, Point pPoint)
	{
		Node node = null;
		if (pNode instanceof ParentNode)
		{
			for (Node child : ((ParentNode) pNode).getChildren())
			{
				node = deepFindNode(child, pPoint);
				if(node != null)
				{
					return node;
				}
			}
		}
		if (pNode.view().contains(pPoint))
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
		for (Edge edge : aEdges)
		{
			if (edge.view().contains(pPoint))
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
		for (Edge edge : aEdges)
		{
			if ((edge.getStart() == pNode || edge.getEnd() == pNode) && !aEdgesToBeRemoved.contains(edge))
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
		for (Edge edge : getEdges())
		{
			if (edge.getClass() == pType && edge.getStart() == pStart && edge.getEnd() == pEnd)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Draws the graph.
	 * @param pGraphics the graphics context
	 */
	public void draw(GraphicsContext pGraphics)
	{
		layout();
		for (Node node : aRootNodes)
		{
			drawNode(node, pGraphics);
		}
		
		for (Edge edge : aEdges)
		{
			edge.view().draw(pGraphics);
		}
	}
	
	private void drawNode(Node pNode, GraphicsContext pGraphics)
	{
		pNode.view().draw(pGraphics);
		if (pNode instanceof ParentNode)
		{
			for (Node node : ((ParentNode) pNode).getChildren())
			{
				drawNode(node, pGraphics);
			}
		}
	}

	/**
	 * Removes a node and all edges that start or end with that node.
	 * @param pNode the node to remove
	 */
	public void removeNode(Node pNode)
	{
		if (aNodesToBeRemoved.contains(pNode))
		{
			return;
		}
		notifyStartingCompoundOperation();
		aNodesToBeRemoved.add(pNode);
		
		if (pNode instanceof ParentNode)
		{
			ArrayList<ChildNode> children = new ArrayList<ChildNode>(((ParentNode) pNode).getChildren());
			//We create a shallow clone so deleting children does not affect the loop
			for (Node childNode: children)
			{
				removeNode(childNode);
			}
		}
		
		// Remove all edges connected to this node
		removeAllEdgesConnectedTo(pNode);

		// Notify all nodes that pNode is being removed.
		for (Node node : aRootNodes)
		{
			removeFromParent( node, pNode );
		}
		
		// Notify all edges that pNode is being removed.
		for (Edge edge : aEdges)
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
		if (pParent instanceof ParentNode)
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
	 * @param pElement The element we want to check is in the graph.
	 * @return True if pElement is a node or edge in this graph.
	 */
	public boolean contains(DiagramElement pElement)
	{	
		if (aEdges.contains( pElement ))
		{
			return true;
		}
		for (Node node : aRootNodes)
		{
			if (containsNode( node, pElement))
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean containsNode(Node pTest, DiagramElement pTarget)
	{
		if (pTest == pTarget)
		{
			return true;
		}
		else if (pTest instanceof ParentNode)
		{
			for (Node node : ((ParentNode) pTest).getChildren())
			{
				if (containsNode(node, pTarget))
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
		aBuilder.removeEdge(pEdge);
	}
	
	public List<Edge> getEdgesToBeRemoved()
	{
		return aEdgesToBeRemoved;
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
		if (!aNeedsLayout)
		{
			return;
		}
		aRootNodes.removeAll(aNodesToBeRemoved);
		aEdges.removeAll(aEdgesToBeRemoved);
		aNodesToBeRemoved.clear();
		aEdgesToBeRemoved.clear();

		for (Node node : aRootNodes)
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
		for (Node node : aRootNodes )
		{
			if (bounds == null)
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
			return new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
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
		for (Edge edge : aEdges)
		{
			if (edge.getStart() == pNode || edge.getEnd() == pNode)
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
}


