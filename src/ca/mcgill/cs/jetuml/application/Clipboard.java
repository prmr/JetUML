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
package ca.mcgill.cs.jetuml.application;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ParentNode;

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
public final class Clipboard 
{
	private static final Clipboard INSTANCE = new Clipboard();
	
	private List<Node> aNodes = new ArrayList<Node>();
	private List<Edge> aEdges = new ArrayList<Edge>();

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
		updatePosition();
	}
	
	/**
	 * @return A list of clones of the elements in this clipboard.
	 */
	public Iterable<DiagramElement> getElements()
	{
		List<Edge> clonedEdges = copyEdges(new ArrayList<DiagramElement>(aEdges));
		List<Node> clonedNodes = copyNodes(clonedEdges, new ArrayList<DiagramElement>(aNodes));
		List<DiagramElement> result = new ArrayList<DiagramElement>();
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
	private List<Edge> copyEdges(Iterable<DiagramElement> pSelection)
	{
		List<Edge> result = new ArrayList<>();
		for( DiagramElement element : pSelection )
		{
			if( element instanceof Edge )
			{	
				result.add((Edge)((Edge) element).clone());
			}
		}
		return result;
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
	
	private void updatePosition()
	{
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		for( Node node : aNodes )
		{
			minX = Math.min(minX, node.position().getX());
			minY = Math.min(minY, node.position().getY());
		}
		for( Node node : aNodes )
		{
			node.translate(-minX, -minY);
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
			else if( node instanceof ParentNode )
			{
				if( recursivelyContains( pNode, ((ParentNode)node).getChildren()) )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean recursivelyContains(Node pNode, List<ChildNode> pNodes)
	{
		for( Node node : pNodes )
		{
			if( node == pNode )
			{
				return true;
			}
			else if( node instanceof ParentNode )
			{
				if(  recursivelyContains( pNode, ((ParentNode)node).getChildren()) )
				{
					return true;
				}
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
		if( pOld instanceof ParentNode )
		{
			List<ChildNode> oldChildren = ((ParentNode) pOld).getChildren();
			List<ChildNode> newChildren = ((ParentNode) pNew).getChildren();
			for( int i = 0; i < oldChildren.size(); i++)
			{
				reassignEdges(pEdges, oldChildren.get(i), newChildren.get(i));
			}
		}
	}
	
	/*
	 * Returns true of pNode needs a parent that isn't in 
	 * the clipboard.
	 */
	private boolean missingParent(Node pNode)
	{
		return pNode instanceof ChildNode && ((ChildNode)pNode).requiresParent() && !aNodes.contains(((ChildNode)pNode).getParent()) ;
	}
	
	/*
	 * Removes the reference to the parent of any node that
	 * does not have a parent in the copied node list.
	 */
	private void removeDanglingReferencesToParents()
	{
		for( Node node : aNodes )
		{
			if( node instanceof ChildNode && ((ChildNode)node).getParent() != null )
			{
				if( !aNodes.contains(((ChildNode)node).getParent()))
				{
					((ChildNode)node).getParent().removeChild((ChildNode)node);
				}
			}
		}
	}
	
	/**
	 * Returns true only of all the nodes and edges in the selection 
	 * are compatible with the type of the target diagram.
	 * 
	 * @param pDiagram The diagram to paste into.
	 * 
	 * @return True if and only if it is possible to paste the content
	 * of the clipboard into pDiagram.
	 */
	public boolean validPaste(Diagram pDiagram)
	{
		for( Edge edge : aEdges )
		{
			if( !validEdgeFor(edge, pDiagram ))
			{
				return false;
			}
		}
		for( Node node : aNodes )
		{
			if( !validNodeFor(node, pDiagram ))
			{
				return false;
			}
		}
		return true;
	}
	
	private static boolean validNodeFor( Node pNode, Diagram pGraph )
	{
		for( Node node : pGraph.getNodePrototypes() )
		{
			if( pNode.getClass() == node.getClass() )
			{
				return true;
			}
		}
		return false;
	}
	
	private static boolean validEdgeFor( Edge pEdge, Diagram pGraph )
	{
		for( Edge edge : pGraph.getEdgePrototypes() )
		{
			if( pEdge.getClass() == edge.getClass() )
			{
				return true;
			}
		}
		return false;
	}
}
