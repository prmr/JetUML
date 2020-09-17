/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.diagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;

/**
 * Stores the logical structure of a diagram. This class is only concerned with 
 * maintaining information about the logical structure of a diagram (nodes and edges). 
 * Specifically, it should not encode any business rules about the valid construction
 * of diagrams (handled by DiagramBuilder), or of computing the geometry of a diagram
 * (handled by DiagramView). DiagramData provides immutable access to the information
 * stored in the diagram.
 */
public final class Diagram implements DiagramData
{
	/*
	 * Only root nodes are explicitly tracked by a diagram object. Nodes that are children of their parent should be
	 * managed and accessed through their parent node.
	 */
	private final ArrayList<Node> aRootNodes;
	private final ArrayList<Edge> aEdges;
	private final DiagramType aType;

	/**
	 * Creates an empty diagram.
	 * 
	 * @param pType The type of the diagram.
	 */
	public Diagram(DiagramType pType)
	{
		aType = pType;
		aRootNodes = new ArrayList<>();
		aEdges = new ArrayList<>();
	}

	/**
	 * Creates a copy of the current diagram. The copy is a completely distinct graph of nodes and edges with the same
	 * topology as this diagram.
	 * 
	 * @return A copy of this diagram. Never null.
	 */
	public Diagram duplicate()
	{
		Diagram copy = new Diagram(this.aType);
		aEdges.forEach(edge -> copy.aEdges.add(edge.clone()));

		for( Node node : aRootNodes )
		{
			Node nodeCopy = node.clone();
			copy.aRootNodes.add(nodeCopy);
			reassignEdges(copy.aEdges, node, nodeCopy);
		}

		// Reassign diagram
		copy.aEdges.forEach(edge -> edge.connect(edge.getStart(), edge.getEnd(), copy));
		for( Node node : copy.aRootNodes )
		{
			copy.attachNode(node);
		}
		return copy;
	}

	/*
	 * Recursively attach the node and all its children to this diagram.
	 */
	private void attachNode(Node pNode)
	{
		pNode.attach(this);
		for( Node child : pNode.getChildren() )
		{
			attachNode(child);
		}
	}

	/*
	 * For node pOriginal, go through all edges that refer to it and replace it with pCopy in the edge. Do this
	 * recursively for all children of pOriginal, assuming the same topology for pCopy.
	 */
	private static void reassignEdges(List<Edge> pEdges, Node pOriginal, Node pCopy)
	{
		for( Edge edge : pEdges )
		{
			if( edge.getStart() == pOriginal )
			{
				edge.connect(pCopy, edge.getEnd(), edge.getDiagram());
			}
			if( edge.getEnd() == pOriginal )
			{
				edge.connect(edge.getStart(), pCopy, edge.getDiagram());
			}
		}
		List<Node> oldChildren = pOriginal.getChildren();
		List<Node> newChildren = pCopy.getChildren();
		for( int i = 0; i < oldChildren.size(); i++ )
		{
			reassignEdges(pEdges, oldChildren.get(i), newChildren.get(i));
		}
	}

	@Override
	public List<Node> rootNodes()
	{
		return Collections.unmodifiableList(aRootNodes);
	}

	@Override
	public List<Edge> edges()
	{
		return Collections.unmodifiableList(aEdges);
	}

	/**
	 * @return The type of this diagram.
	 */
	public DiagramType getType()
	{
		return aType;
	}

	/**
	 * @return The file extension (including the dot) corresponding to files of this diagram type.
	 */
	public String getFileExtension()
	{
		return aType.getFileExtension();
	}

	/**
	 * @return The name of the diagram.
	 */
	public String getName()
	{
		return aType.getName();
	}

	/**
	 * Checks whether pElement is in the diagram. If pElement is a node, the method returns true if it is a root node,
	 * or any of its parent is a root node.
	 * 
	 * @param pElement The element we want to check is in the diagram.
	 * @return True if pElement is a node or edge in this diagram.
	 * @pre pElement != null
	 */
	public boolean contains(DiagramElement pElement)
	{
		assert pElement != null;
		if( aEdges.contains(pElement) )
		{
			return true;
		}
		for( Node node : aRootNodes )
		{
			if( containsNode(node, pElement) )
			{
				return true;
			}
		}
		return false;
	}

	private boolean containsNode(Node pTest, DiagramElement pTarget)
	{
		if( pTest == pTarget )
		{
			return true;
		}
		for( Node node : pTest.getChildren() )
		{
			if( containsNode(node, pTarget) )
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @param pNode The node to check.
	 * @return True if pNode is a root node of the Diagram.
	 * @pre pNode != null.
	 */
	public boolean containsAsRoot(Node pNode)
	{
		assert pNode != null;
		return aRootNodes.contains(pNode);
	}

	/**
	 * Gets the types of elements that can be using prototypes for a diagram type. 
	 * The list returned is a copy of the prototypes: it can be safely modified.
	 * 
	 * @return A non-null list of node prototypes
	 */
	public List<DiagramElement> getPrototypes()
	{
		return aType.getPrototypes();
	}

	/**
	 * @param pNode The node to test for
	 * @return All the edges connected to pNode
	 * @pre pNode != null
	 * @pre contains(pNode)
	 */
	public Iterable<Edge> edgesConnectedTo(Node pNode)
	{
		assert pNode != null && contains(pNode);
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
	 * Adds pNode as a root node in this diagram. Callers of this method must ensure that the addition respects the
	 * integrity of the diagram.
	 * 
	 * @param pNode The node to add.
	 * @pre pNode != null
	 */
	public void addRootNode(Node pNode)
	{
		assert pNode != null;
		recursiveAttach(pNode);
		aRootNodes.add(pNode);
	}

	private void recursiveAttach(Node pNode)
	{
		pNode.attach(this);
		pNode.getChildren().forEach(this::recursiveAttach);
	}

	private void recursiveDetach(Node pNode)
	{
		pNode.detach();
		pNode.getChildren().forEach(this::recursiveDetach);
	}

	/**
	 * Removes pNode from the list of root nodes in this diagram. Callers must ensure that the removal preserves the
	 * integrity of the diagram.
	 * 
	 * @param pNode The node to remove.
	 * @pre pNode != null && pNode is contained as a root node.
	 */
	public void removeRootNode(Node pNode)
	{
		assert pNode != null && aRootNodes.contains(pNode);
		recursiveDetach(pNode);
		aRootNodes.remove(pNode);
	}

	/**
	 * Adds pEdge to the diagram. pEdge should already be connected to its start and end nodes. The edge is added to the
	 * end of the list of edges.
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
	 * Adds pEdge at index pIndex, and shifts the existing edges to the right of the list.
	 * 
	 * @param pIndex Where to add the edge.
	 * @param pEdge The edge to add.
	 * @pre pEdge != null && pIndex >=0 && pIndex < aEdges.size()
	 */
	public void addEdge(int pIndex, Edge pEdge)
	{
		assert pEdge != null && pIndex >= 0 && pIndex <= aEdges.size();
		aEdges.add(pIndex, pEdge);
	}


	/**
	 * @param pEdge
	 *            The edge to check.
	 * @return The index of pEdge in the list of edges.
	 * @pre contains(pEdge)
	 */
	public int indexOf(Edge pEdge)
	{
		assert contains(pEdge);
		return aEdges.indexOf(pEdge);
	}

	/**
	 * Removes pEdge from this diagram. Callers must ensure that the removal preserves the integrity of the diagram.
	 * 
	 * @param pEdge The edge to remove.
	 * @pre pEdge != null && pEdge is contained in the diagram
	 */
	public void removeEdge(Edge pEdge)
	{
		assert pEdge != null && aEdges.contains(pEdge);
		aEdges.remove(pEdge);
	}

	/**
	 * Recursively reorder the node to be on top of its parent's children. If the node is not a child node or the node
	 * does not have a parent, check if the node is a root node of the diagram and place it on top.
	 * 
	 * @param pNode The node to be placed on top
	 * @pre pNode != null
	 */
	public void placeOnTop(Node pNode)
	{
		assert pNode != null;
		// Certain nodes should not have their order changed
		if( pNode.getClass() == CallNode.class || pNode.getClass() == FieldNode.class )
		{
			return;
		}
		if( pNode.hasParent() )
		{
			Node parent = pNode.getParent();
			// Move the child node to the top of all other children
			parent.placeLast(pNode);
			// Recursively reorder the node's parent
			placeOnTop(parent);
		}
		else if( containsAsRoot(pNode) )
		{
			removeRootNode(pNode);
			addRootNode(pNode);
		}
	}
}