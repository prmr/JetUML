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

/**
 *  Stores the logical structure of a diagram. This class hierarchy
 *  is only concerned with maintaining information about the logical
 *  structure of a diagram (nodes and edges). Specifically, it should 
 *  not encode any business rules about the valid construction of diagrams
 *  (handled by DiagramBuilder), or of computing the geometry of 
 *  a diagram (handled by DiagramView). DiagramData provides immutable
 *  access to the information stored in the diagram.
 */
public abstract class Diagram implements DiagramData
{
	/*
	 * Only root nodes are explicitly tracked by a diagram object. Nodes
	 * that are children of their parent should be managed and accessed
	 * through their parent node.
	 */
	private ArrayList<Node> aRootNodes;
	private ArrayList<Edge> aEdges;

	/**
	 * Creates an empty diagram.
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
	 * Checks whether pElement is in the diagram. If pElement
	 * is a node, the method returns true if it is a root node,
	 * or any of its parent is a root node.
	 * 
	 * @param pElement The element we want to check is in the diagram.
	 * @return True if pElement is a node or edge in this diagram.
	 * @pre pElement != null
	 */
	public boolean contains(DiagramElement pElement)
	{	
		assert pElement != null;
		if(aEdges.contains( pElement ))
		{
			return true;
		}
		for(Node node : aRootNodes)
		{
			if(containsNode( node, pElement))
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean containsNode(Node pTest, DiagramElement pTarget)
	{
		if(pTest == pTarget)
		{
			return true;
		}
		else if(pTest instanceof ParentNode)
		{
			for(Node node : ((ParentNode) pTest).getChildren())
			{
				if(containsNode(node, pTarget))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the node types of a particular diagram type.
	 * @return An array of node prototypes
	 */   
	public abstract Node[] getNodePrototypes();

	/**
	 * Gets the edge types of a particular diagram type.
	 * @return an array of edge prototypes
	 */   
	public abstract Edge[] getEdgePrototypes();

	/**
	 * @param pNode the node to test for
	 * @return All the edges connected to pNode
	 * @pre pNode != null
	 * @pre contains(pNode)
	 */
	public Iterable<Edge> edgesConnectedTo(Node pNode)
	{
		assert pNode != null && contains(pNode);
		Collection<Edge> lReturn = new ArrayList<>();
		for(Edge edge : aEdges)
		{
			if (edge.getStart() == pNode || edge.getEnd() == pNode)
			{
				lReturn.add(edge);
			}
		}
		return lReturn;
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
	 * start and end nodes. The edge is added to the end of the list of edges.
	 * 
	 * @param pEdge The edge to add.
	 * @pre pEdge != null && pEdge.getStart() != null && pEdge.getEnd() != null && pEdge.getGraph != null
	 */
	public void addEdge(Edge pEdge)
	{
		assert pEdge != null && pEdge.getStart() != null && pEdge.getEnd() != null && pEdge.getDiagram() != null;
		aEdges.add(pEdge);
	}
	
	/**
	 * @param pEdge The edge to check.
	 * @return The index of pEdge in the list of edges.
	 * @pre contains(pEdge)
	 */
	public int indexOf(Edge pEdge)
	{
		assert contains(pEdge);
		return aEdges.indexOf(pEdge);
	}
	
	/**
	 * Adds pEdge at index pIndex, and shifts the 
	 * existing edges to the right of the list.
	 * 
	 * @param pIndex Where to add the edge.
	 * @param pEdge The edge to add.
	 * @pre pEdge != null && pIndex >=0 && pIndex < aEdges.size()
	 */
	public void addEdge(int pIndex, Edge pEdge)
	{
		assert pEdge != null && pIndex >=0 && pIndex <= aEdges.size();
		aEdges.add(pIndex, pEdge);
	}
	
	/**
	 * @return The number of edges in the diagram.
	 */
	public int numberOfEdges()
	{
		return aEdges.size();
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