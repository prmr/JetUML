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

import ca.mcgill.cs.jetuml.diagram.nodes.ParentNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.scene.canvas.GraphicsContext;

/**
 *  A diagram consisting of nodes and edges.
 */
public abstract class Diagram implements DiagramData
{
	/*
	 * Only root nodes are explicitly tracked by a diagram object. Nodes
	 * that are children of their parent should be managed and accessed
	 * through their parent node.
	 */
	protected ArrayList<Node> aRootNodes;
	protected ArrayList<Edge> aEdges;

	/**
	 * Constructs a diagram with no nodes or edges.
	 */
	public Diagram()
	{
		aRootNodes = new ArrayList<>();
		aEdges = new ArrayList<>();
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
	 * where depth is measured in terms of distance from
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
	 * Draws the diagram.
	 * @param pGraphics the graphics context
	 */
	public void draw(GraphicsContext pGraphics)
	{
		for(Node node : aRootNodes)
		{
			drawNode(node, pGraphics);
		}
		
		for(Edge edge : aEdges)
		{
			edge.view().draw(pGraphics);
		}
	}
	
	private void drawNode(Node pNode, GraphicsContext pGraphics)
	{
		pNode.view().draw(pGraphics);
		if(pNode instanceof ParentNode)
		{
			for(Node node : ((ParentNode) pNode).getChildren())
			{
				drawNode(node, pGraphics);
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
	 * @return The number of edges in the diagram.
	 */
	public int numberOfEdges()
	{
		return aEdges.size();
	}
	
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
	 * Adds pNode as a root node in this diagram. Callers of this method 
	 * must ensure that the addition respects the integrity of the diagram.
	 * 
	 * @param pNode The node to add.
	 * @pre pNode != null
	 */
	public void addRootNode(Node pNode)
	{
		assert pNode != null;
		aRootNodes.add(pNode);
	}
	
	/**
	 * Removes pNode from the list of root nodes in this
	 * diagram. Callers must ensure that the removal preserves
	 * the integrity of the diagram.
	 * 
	 * @param pNode The node to remove.
	 * @pre pNode != null && pNode is contained as a root node.
	 */
	public void removeRootNode(Node pNode)
	{
		assert pNode != null && aRootNodes.contains(pNode);
		aRootNodes.remove(pNode);
	}
	
	/**
	 * Adds pEdge to the diagram. pEdge should already be connected to its 
	 * start and end nodes.
	 * 
	 * @param pEdge The edge to add.
	 * @pre pEdge != null && pEdge.getStart() != null && pEdge.getEnd() != null && pEdge.getGraph != null
	 */
	public void addEdge(Edge pEdge)
	{
		assert pEdge != null && pEdge.getStart() != null && pEdge.getEnd() != null && pEdge.getGraph() != null;
		aEdges.add(pEdge);
	}
	
	/**
	 * Removes pEdge from this diagram. Callers must ensure that the removal
	 * preserves the integrity of the diagram.
	 * 
	 * @param pEdge The edge to remove. 
	 * @pre pEdge != null && pEdge is contained in the diagram
	 */
	public void removeEdge(Edge pEdge)
	{
		assert pEdge != null && aEdges.contains(pEdge);
		aEdges.remove(pEdge);
	}
}


