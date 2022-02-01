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
package ca.mcgill.cs.jetuml.application;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import ca.mcgill.cs.jetuml.annotations.Singleton;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;

/**
 * Stores a set of diagram elements for the purpose of pasting into a diagram.
 * 
 * Copying a list of elements into the clipboard results in a number of transformations
 * to the list and its elements to render the elements suitable for pasting:
 * - All elements are cloned
 * - Dangling edges are removed
 * - Nodes requiring a missing parent are removed
 * - Dangling references to parents are removed
 * - The nodes are repositioned so that the top left coordinate of the set of elements
 *   is at the origin (0,0).
 *   
 * The list of elements stored into the clipboard is assumed to respect the non-redundancy 
 * constraint that no element whose deletion leads to the deletion of a node is selected with the node.
 * 
 * The clipboard is a singleton. This is necessary to allow copying elements
 * between diagrams of the same type.
 */
@Singleton
public final class Clipboard 
{
	private static final Clipboard INSTANCE = new Clipboard();
	
	private final List<Node> aNodes = new ArrayList<>();
	private final List<Edge> aEdges = new ArrayList<>();

	/**
	 * Creates an empty clip-board.
	 */
	private Clipboard() 
	{}
	
	/**
	 * @return The Singleton instance of the Clipboard.
	 */
	public static Clipboard instance()
	{
		return INSTANCE;
	}
	
	/**
	 * Copies the elements in pSelection into the clip board.  
	 * The list of elements stored into the clipboard is assumed to 
	 * respect the non-redundancy constraint that no element whose 
	 * deletion leads to the deletion of a node is selected with the node.
	 * The transformation described in the class documentation are applied.
	 * 
	 * @param pSelection The elements to copy. Cannot be null.
	 */
	public void copy(Iterable<DiagramElement> pSelection)
	{
		assert pSelection != null;
		clear();
		aEdges.addAll(copyEdges(pSelection));
		aNodes.addAll(copyNodes(aEdges, pSelection));
		removeDanglingEdges();
		removeDanglingReferencesToParents();
	}
	
	/**
	 * @return A list of clones of the elements in this clipboard.
	 */
	public Iterable<DiagramElement> getElements()
	{
		List<Edge> clonedEdges = copyEdges(new ArrayList<>(aEdges));
		List<Node> clonedNodes = copyNodes(clonedEdges, new ArrayList<>(aNodes));
		List<DiagramElement> result = new ArrayList<>();
		result.addAll(clonedEdges);
		result.addAll(clonedNodes);
		return result;
	}
	
	/*
	 * Empties the clipboard
	 */
	private void clear()
	{
		aNodes.clear();
		aEdges.clear();
	}
	
	/*
	 * Makes a clone of every edges in pSelection and copies it into the clipboard	 
	 */
	private static List<Edge> copyEdges(Iterable<DiagramElement> pSelection)
	{
		return stream(pSelection.spliterator(), false)
			.filter(Edge.class::isInstance)
			.map(Edge.class::cast)
			.map(Edge::clone)
			.collect(toList());
	}
	
	/*
	 * Makes a clone of every node in pSelection, copies it into the clipboard,
	 * and reassigns its edges
	 */
	private List<Node> copyNodes(List<Edge> pEdges, Iterable<DiagramElement> pSelection)
	{
		List<Node> result = new ArrayList<>();
		for( DiagramElement element : pSelection )
		{
			if( element instanceof Node )
			{
				if( missingParent( (Node)element ))
				{
					continue;
				}
				Node cloned = ((Node) element).clone();
				result.add(cloned);
				reassignEdges(pEdges, (Node)element, cloned);
			}
		}
		return result;
	}
	
	private void removeDanglingEdges()
	{
		List<Edge> toDelete = new ArrayList<>();
		for( Edge edge : aEdges )
		{
			if( !recursivelyContains(edge.getStart()) || !recursivelyContains(edge.getEnd()))
			{
				toDelete.add(edge);
			}
		}
		for( Edge edge : toDelete )
		{
			aEdges.remove(edge);
		}
	}
	
	private boolean recursivelyContains(Node pNode)
	{
		for( Node node : aNodes )
		{
			if( node == pNode )
			{
				return true;
			}
			else if( recursivelyContains(pNode, node.getChildren()) )
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean recursivelyContains(Node pNode, List<Node> pNodes)
	{
		for( Node node : pNodes )
		{
			if( node == pNode )
			{
				return true;
			}
			else if( recursivelyContains(pNode, node.getChildren()) )
			{
				return true;
			}
		}
		return false;
	}
	
	private void reassignEdges(List<Edge> pEdges, Node pOld, Node pNew)
	{
		for( Edge edge : pEdges )
		{
			if( edge.getStart() == pOld )
			{
				edge.connect(pNew, edge.getEnd(), edge.getDiagram());
			}
			if( edge.getEnd() == pOld)
			{
				edge.connect(edge.getStart(), pNew, edge.getDiagram());
			}
		}
		List<Node> oldChildren = pOld.getChildren();
		List<Node> newChildren = pNew.getChildren();
		for( int i = 0; i < oldChildren.size(); i++ )
		{
			reassignEdges(pEdges, oldChildren.get(i), newChildren.get(i));
		}
	}
	
	/*
	 * Returns true if pNode needs a parent that isn't in 
	 * the clipboard.
	 */
	private boolean missingParent(Node pNode)
	{
		return pNode.requiresParent() && !aNodes.contains(pNode.getParent()) ;
	}
	
	/*
	 * Removes the reference to the parent of any node in the list.
	 * This operation is safe because nodes in the clip-board
	 * can only be pasted as root nodes. Children nodes would
	 * be copied through their parent.
	 */
	private void removeDanglingReferencesToParents()
	{
		aNodes.stream()
			.filter(Node::hasParent)
			.forEach(Node::unlink);
	}
	
	/**
	 * Returns true only of all the nodes and edges in the selection 
	 * are compatible with the type of the target diagram.
	 * 
	 * @param pDiagram The diagram to paste into.
	 * 
	 * @return True if and only if it is possible to paste the content
	 *     of the clipboard into pDiagram.
	 */
	public boolean validPaste(Diagram pDiagram)
	{
		for( Edge edge : aEdges )
		{
			if( !validElementFor(edge, pDiagram ))
			{
				return false;
			}
		}
		for( Node node : aNodes )
		{
			if( !validElementFor(node, pDiagram ))
			{
				return false;
			}
		}
		return true;
	}
	
	private static boolean validElementFor( DiagramElement pElement, Diagram pDiagram )
	{
		// PointNodes are allowed in all diagrams despite not being contained in prototypes.
		if ( pElement.getClass() == PointNode.class ) 
		{
			return true;
		}
		return pDiagram.getPrototypes().stream()
				.map(Object::getClass)
				.anyMatch(Predicate.isEqual(pElement.getClass()));
	}
}
