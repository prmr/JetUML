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
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.gui.DiagramCanvasController;
import ca.mcgill.cs.jetuml.gui.SelectionModel;

/**
 * Stores a graph subset for purpose of pasting. The clip-board does not
 * accept edges unless both end-points are also being copied.
 * 
 * The Clipboard is a singleton. This is necessary to allow copying elements
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
	 * Clones the selection in pPanel and stores it in the clip-board.
	 * @param pSelection The elements to copy. Cannot be null.
	 */
	public void copy(SelectionModel pSelection)
	{
		assert pSelection != null;
		aNodes.clear();
		aEdges.clear();
		
		// First copy the edges so we can assign their end-points when copying nodes.
		// Do not include dangling edges.
		for( DiagramElement element : pSelection )
		{
			if( element instanceof Edge && pSelection.capturesEdge((Edge)element ))
			{	
				aEdges.add((Edge)((Edge) element).clone());
			}
		}
		
		// Clone the nodes and re-route their edges
		for( DiagramElement element : pSelection )
		{
			if( element instanceof Node )
			{
				if( missingParent( (Node)element ))
				{
					continue;
				}
				Node cloned = ((Node) element).clone();
				aNodes.add(cloned);
				reassignEdges(aEdges, (Node)element, cloned);
			}
		}
		
		// Delete any edge whose parent is not in aNodes
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
	
	/**
	 * Copies the selection list in the panel (as done by the copy method) and removes all
	 * the nodes in the selection from the graph wrapped by this pPanel.
	 * 
	 * @param pController The controller.
	 */
	public void cut(DiagramCanvasController pController)
	{
		assert pController != null;
		copy(pController.getSelectionModel());	
		pController.removeSelected();
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
				edge.connect(pNew, edge.getEnd(), edge.getGraph());
			}
			if( edge.getEnd() == pOld)
			{
				edge.connect(edge.getStart(), pNew, edge.getGraph());
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
	
	/**
	 * Pastes the current selection into the pGraphPanel.
	 * @param pController The current Diagram to paste contents to.
	 * @return The elements to paste as a selectionList.
	 */
	// CSOFF: Fix in later release
	public List<DiagramElement> paste(DiagramCanvasController pController)
	{
		if( !validPaste(pController.getDiagram()))
		{
			return new ArrayList<>();
		}
		
		pController.startCompoundGraphOperation();
		List<Edge> clonedEdges = new ArrayList<>();
		for( Edge edge : aEdges )
		{
			clonedEdges.add((Edge) edge.clone());
		}
		
		List<Node> clonedRootNodes = new ArrayList<>();
		Rectangle bounds = null;

		for( Node node : aNodes )
		{
			Node cloned = node.clone();
			clonedRootNodes.add(cloned);
			reassignEdges(clonedEdges, node, cloned);
			bounds = updateBounds(bounds, node);
		}
		
		removeDanglingReferencesToParents(clonedRootNodes);
		
		for( Node node : clonedRootNodes )
		{
			pController.getDiagram().insertNode(node);
		}
		for( Edge edge : clonedEdges )
		{
			// Verify that the nodes were correctly added.
			// It is possible that some nodes could not be 
			// pasted (e.g., children nodes without their parent)
			// so some edges might no longer be relevant.
			if( pController.getDiagram().contains( edge.getStart() ) && pController.getDiagram().contains(edge.getEnd()))
			{
				pController.getDiagram().insertEdge(edge);
			}
		}
		
		// Reposition the graph
		for( Edge edge : clonedEdges )
		{
			bounds = updateBounds(bounds, edge);
		}
		for( Node node : clonedRootNodes )
		{
			node.translate(-bounds.getX(), -bounds.getY());
		}
		// End graph repositioning
		pController.finishCompoundGraphOperation();
		
		ArrayList<DiagramElement> selectionList  = new ArrayList<>();
		for( Edge edge : clonedEdges )
		{
			selectionList.add(edge);
		}
		for( Node node : clonedRootNodes )
		{
			selectionList.add(node);
		}
		return selectionList;
	} // CSON:
	
	// Goes through pNodes and removes the reference to the parent
	// of any node who does not have a parent in the pNodes list
	private static void removeDanglingReferencesToParents(List<Node> pNodes)
	{
		for( Node node : pNodes )
		{
			if( node instanceof ChildNode && ((ChildNode)node).getParent() != null )
			{
				if( !pNodes.contains(((ChildNode)node).getParent()))
				{
					((ChildNode)node).getParent().removeChild((ChildNode)node);
				}
			}
		}
	}
	
	private static Rectangle updateBounds(Rectangle pBounds, DiagramElement pElement)
	{
		Rectangle bounds = pBounds;
		if( bounds == null )
		{
			bounds = getBounds(pElement);
		}
		else
		{
			bounds = bounds.add( getBounds(pElement));
		}
		return bounds;
	}
	
	private static Rectangle getBounds(DiagramElement pElement)
	{
		if( pElement instanceof Node )
		{
			return ((Node)pElement).view().getBounds();
		}
		else if( pElement instanceof Edge )
		{
			return ((Edge)pElement).view().getBounds();
		}
		else
		{
			assert false;
			return null;
		}
	}
	
	/*
	 * Returns true only of all the nodes and edges in the selection 
	 * are compatible with the target graph type.
	 */
	private boolean validPaste(Diagram pGraph)
	{
		for( Edge edge : aEdges )
		{
			if( !validEdgeFor(edge, pGraph ))
			{
				return false;
			}
		}
		for( Node node : aNodes )
		{
			if( !validNodeFor(node, pGraph ))
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
