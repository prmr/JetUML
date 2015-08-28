package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ca.mcgill.cs.stg.jetuml.graph.ChildNode;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.GraphElement;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.ParentNode;

/**
 * @author Martin P. Robillard
 * 
 * Stores a graph subset for purpose of pasting. The clip-board does not
 * accept edges unless both end-points are also being copied.
 */
public final class Clipboard 
{
	private List<Node> aNodes = new ArrayList<Node>();
	private List<Edge> aEdges = new ArrayList<Edge>();

	/**
	 * Creates an empty clip-board.
	 */
	public Clipboard() 
	{}
	
	/* For testing only */
	Collection<Node> getNodes()
	{
		return Collections.unmodifiableCollection(aNodes);
	}
	
	/* For testing only */
	Collection<Edge> getEdges()
	{
		return Collections.unmodifiableCollection(aEdges);
	}

	/**
	 * Clones the selection and stores it in the clip-board.
	 * @param pSelection The elements to copy. Cannot be null.
	 */
	public void copy(SelectionList pSelection)
	{
		assert pSelection != null;
		aNodes.clear();
		aEdges.clear();
		
		// First copy the edges so we can assign their end-points when copying nodes.
		// Do not include dangling edges.
		for( GraphElement element : pSelection )
		{
			if( element instanceof Edge && pSelection.capturesEdge((Edge)element ))
			{	
				aEdges.add((Edge)((Edge) element).clone());
			}
		}
		
		// Clone the nodes and re-route their edges
		for( GraphElement element : pSelection )
		{
			if( element instanceof Node )
			{
				Node cloned = ((Node) element).clone();
				aNodes.add(cloned);
				reassignEdges(aEdges, (Node)element, cloned);
			}
		}
	}
	
	private void reassignEdges(List<Edge> pEdges, Node pOld, Node pNew)
	{
		for( Edge edge : pEdges )
		{
			if( edge.getStart() == pOld )
			{
				edge.connect(pNew, edge.getEnd());
			}
			if( edge.getEnd() == pOld)
			{
				edge.connect(edge.getStart(), pNew);
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
	
	/**
	 * Pastes the current selection into the pGraphPanel.
	 * @param pGraph The current Graph to paste contents to.
	 * @return The elements to paste as a selectionList.
	 */
	public SelectionList paste(Graph pGraph)
	{
		if( !validPaste(pGraph))
		{
			return new SelectionList();
		}
		
		Rectangle2D bounds = null;
		List<Edge> clonedEdges = new ArrayList<>();
		for( Edge edge : aEdges )
		{
			clonedEdges.add((Edge) edge.clone());
			bounds = updateBounds(bounds, edge);
		}
		
		List<Node> clonedRootNodes = new ArrayList<>();
		for( Node node : aNodes )
		{
			Node cloned = node.clone();
			clonedRootNodes.add(cloned);
			reassignEdges(clonedEdges, node, cloned);
			bounds = updateBounds(bounds, node);

		}
		
		for( Node node : clonedRootNodes )
		{
			pGraph.add(node, new Point2D.Double(node.getBounds().getX()-bounds.getX(),
					node.getBounds().getY() - bounds.getY()));
		}
		for( Edge edge : clonedEdges )
		{
			pGraph.connect(edge, edge.getStart(), edge.getEnd());
		}
		
		SelectionList selectionList  = new SelectionList();
		for( Edge edge : clonedEdges )
		{
			selectionList.add(edge);
		}
		for( Node node : clonedRootNodes )
		{
			selectionList.add(node);
		}
		return selectionList;
	}
	
	private static Rectangle2D updateBounds(Rectangle2D pBounds, GraphElement pElement)
	{
		Rectangle2D bounds = pBounds;
		if( bounds == null )
		{
			bounds = pElement.getBounds();
		}
		else
		{
			bounds.add( pElement.getBounds());
		}
		return bounds;
	}
	
	/*
	 * Returns true only of all the nodes and edges in the selection 
	 * are compatible with the target graph type.
	 */
	private boolean validPaste(Graph pGraph)
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
	
	private static boolean validNodeFor( Node pNode, Graph pGraph )
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
	
	private static boolean validEdgeFor( Edge pEdge, Graph pGraph )
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





