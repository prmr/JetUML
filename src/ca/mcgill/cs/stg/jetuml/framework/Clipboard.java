package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
	 * Fixes the parent child relationships of the cloned children nodes. 
	 * @param pElement the current GraphElement being considered.
	 * @param pOriginalAndClonedNodes the LinkedHasMap of original and cloned nodes.
	 */
	public void fixParentChildRelationShips(GraphElement pElement, Map<Node, Node> pOriginalAndClonedNodes)
	{
		if(pElement instanceof ParentNode)
		{
			ParentNode curNode = (ParentNode) pElement;
			ParentNode cloneNode = (ParentNode)pOriginalAndClonedNodes.get(curNode);
			List<ChildNode> cloneChildren = cloneNode.getChildren();
			for(int i = 0; i < cloneChildren.size(); i++) 
			{
				ChildNode removed = cloneChildren.remove(i);
				ChildNode replacement = (ChildNode)pOriginalAndClonedNodes.get(removed);
				cloneChildren.add(i, replacement);
			}
			
		}
		if(pElement instanceof ChildNode)
		{	
			ChildNode curNode = (ChildNode) pElement;
			if(curNode.getParent() != null)
			{
				ChildNode cloneNode = (ChildNode)pOriginalAndClonedNodes.get(curNode); 
				cloneNode.setParent((ParentNode)pOriginalAndClonedNodes.get(curNode.getParent()));
			}
		}
	}

	/**
	 * Pastes the current selection into the pGraphPanel.
	 * @param pGraphPanel The current GraphPanel to paste contents to.
	 * @return The elements to paste as a selectionList.
	 */
	public SelectionList paste(GraphPanel pGraphPanel)
	{
		if( !validPaste(pGraphPanel.getGraph()))
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
			Node cloned = ((Node) node).clone();
			clonedRootNodes.add(cloned);
			reassignEdges(clonedEdges, (Node)node, cloned);
			bounds = updateBounds(bounds, node);

		}
		
		for( Node node : clonedRootNodes )
		{
			pGraphPanel.getGraph().add(node, new Point2D.Double(node.getBounds().getX()-bounds.getX(),
					node.getBounds().getY() - bounds.getY()));
		}
		for( Edge edge : clonedEdges )
		{
			pGraphPanel.getGraph().connect(edge, edge.getStart(), edge.getEnd());
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
		
//		pGraphPanel.getGraph().add(cloned, pPoint)
//		double x = cloneNode.getBounds().getX();
//		double y = cloneNode.getBounds().getY();
//		
//		/*
//		 * This translates all the new nodes and their respective edges over to the top left corner of the 
//		 * GraphPanel.
//		 */
//		aGraph.add(cloneNode, new Point2D.Double(x-pBounds.getX(), y-pBounds.getY()));
		
//		Rectangle2D bounds = null;
//		Graph aGraph = pGraphPanel.getGraph();
//		Node[]currentProtoTypes = aGraph.getNodePrototypes();
//		Edge[]currentEdgeTypes = aGraph.getEdgePrototypes();
//		ArrayList<Node> copyNodes = new ArrayList<Node>();
//		/*
//		 * Clone all nodes and remember the original-cloned correspondence
//		 */
//		Map<Node, Node> originalAndClonedNodes = new LinkedHashMap<Node, Node>();
//		/*
//		 * First clone all of the nodes and link them with the previous nodes. All the nodes will be iterated over in
//		 * the pastSelection SelectionList
//		 */
//		for(Node curNode: aNodes)
//		{
//			for(Node n: currentProtoTypes)
//			{
//				if(curNode.getClass() == n.getClass())
//				{
//					Node newNode = curNode.clone();
//					originalAndClonedNodes.put(curNode, newNode);
//					copyNodes.add(newNode);
//
//					if(bounds ==null)
//					{
//						bounds = curNode.getBounds();
//					}
//					else
//					{
//						bounds.add(curNode.getBounds());
//					}
//				}
//			}
//		}
//		for(GraphElement element: aNodes) //loop through and fix the parent/child relationships for all the clone children
//		{
//			fixParentChildRelationShips(element, originalAndClonedNodes);
//		}
//		/*
//		 * Now the edges can be cloned as all the nodes have been cloned successfully at this point.
//		 * The edges will be iterated over in the pastSelection SelectionList.
//		 */
//		ArrayList<Edge> copyEdges = new ArrayList<Edge>();
//		for( Edge curEdge: aEdges)
//		{
//			for(Edge e: currentEdgeTypes)
//			{
//				/*
//				 * Clone all edges that join copied nodes
//				 */
//				Node start = originalAndClonedNodes.get(curEdge.getStart());
//				Node end = originalAndClonedNodes.get(curEdge.getEnd());  
//				if(checkEdgeEquality(curEdge, e, aGraph) &&start != null && end != null)
//				{
//						Edge e2 = (Edge) e.clone();
//						aGraph.connect(e2, start, end);
//						copyEdges.add(e2);
//				}
//			}
//		}	
//		return constructNewSelectionList(pGraphPanel, copyNodes, copyEdges, bounds);
	}
	
	private Rectangle2D updateBounds(Rectangle2D pBounds, GraphElement pElement)
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
	boolean validPaste(Graph pGraph)
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
	
	static boolean validNodeFor( Node pNode, Graph pGraph )
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
	
	static boolean validEdgeFor( Edge pEdge, Graph pGraph )
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

//	/**
//	 * A helper method to construct the new SelectionList in the graph.
//	 * 
//	 * @param pGraphPanel the current GraphPanel.
//	 * @param pCopyNodes the list of copied Nodes in the paste operation.
//	 * @param pCopyEdges the list of copied Edges int the paste operation.
//	 * @param pBounds the bounds to translate the elements.
//	 * @return a new SelectionList with the pasted elements.
//	 */
//	public SelectionList constructNewSelectionList(GraphPanel pGraphPanel, ArrayList<Node> pCopyNodes, ArrayList<Edge> pCopyEdges, 
//			Rectangle2D pBounds)
//	{
//		/*
//		 * updatedSelectionList is the selectionList to return.
//		 */
//		Graph aGraph = pGraphPanel.getGraph();
//		SelectionList updatedSelectionList = new SelectionList();
//		pGraphPanel.startCompoundListening();
//		for(Node cloneNode: pCopyNodes)
//		{
//			double x = cloneNode.getBounds().getX();
//			double y = cloneNode.getBounds().getY();
//			
//			/*
//			 * This translates all the new nodes and their respective edges over to the top left corner of the 
//			 * GraphPanel.
//			 */
//			aGraph.add(cloneNode, new Point2D.Double(x-pBounds.getX(), y-pBounds.getY()));
//			
//			
//			/*
//			 * Don't add any Children to the SelectionList
//			 */
//			if(!(cloneNode instanceof ChildNode && ((ChildNode)cloneNode).getParent()!=null))
//			{
//				updatedSelectionList.add(cloneNode);
//			}
//		}
//		for(Edge cloneEdge: pCopyEdges)
//		{
//			/*
//			 * If the start and end nodes of a given edge are both CallNodes, then the Graph connection
//			 * is skipped. Otherwise duplicate Call Nodes are produced. This is due to nodes now internally
//			 * storing their origin and terminal edges.
//			 */
//			if(!(cloneEdge.getStart() instanceof CallNode && cloneEdge.getEnd() instanceof CallNode))
//			{
//				Point2D startCenter = new Point2D.Double(cloneEdge.getStart().getBounds().getCenterX(), 
//						cloneEdge.getStart().getBounds().getCenterY());
//				Point2D endCenter = new Point2D.Double(cloneEdge.getEnd().getBounds().getCenterX(), cloneEdge.getEnd().getBounds().getCenterY());
//				aGraph.connect(cloneEdge, startCenter, endCenter);
//			}	
//			updatedSelectionList.add(cloneEdge);
//		}
//		pGraphPanel.endCompoundListening();
//		return updatedSelectionList;
//	}
//	/**
//	 * @param pEdge1 The copied or cut edge whose actual type needs to be determined.
//	 * @param pEdge2 The edge from the list of edge types in the pGraph.
//	 * @param pGraph The current graph in the GraphPanel.
//	 * @return true if the two edges have the same type and false if not.
//	 * NOTE: All Note edges are removed following this check.
//	 */
//	public boolean checkEdgeEquality(Edge pEdge1, Edge pEdge2, Graph pGraph)
//	{
//		boolean equal = false;
//		if(pGraph instanceof ClassDiagramGraph || pGraph instanceof UseCaseDiagramGraph)
//		{
//			if(pEdge1 instanceof ClassRelationshipEdge && pEdge2 instanceof ClassRelationshipEdge)
//			{
//				equal = classDiagramEdgeEqual((ClassRelationshipEdge)pEdge1, (ClassRelationshipEdge)pEdge2, pGraph);
//
//			}
//		}
//		else if(pGraph instanceof ObjectDiagramGraph)
//		{
//			if(pEdge1 instanceof ClassRelationshipEdge && pEdge2 instanceof ClassRelationshipEdge || 
//					pEdge1 instanceof ObjectReferenceEdge && pEdge2 instanceof ObjectReferenceEdge)
//			{
//				equal = true;
//			}
//		}
//		else if(pGraph instanceof SequenceDiagramGraph)
//		{
//			if(pEdge1 instanceof CallEdge && pEdge2 instanceof CallEdge || pEdge1 instanceof ReturnEdge && pEdge2 instanceof ReturnEdge)
//			{
//				equal = true;
//			}
//		}
//		else if(pGraph instanceof StateDiagramGraph)
//		{
//			if(pEdge1 instanceof StateTransitionEdge && pEdge2 instanceof StateTransitionEdge)
//			{
//				equal = true;
//			}
//		}
//		return equal;
//	}

//	/**
//	 * @param pEdge1 The copied or cut Edge whose actual type needs to be determined.
//	 * @param pEdge2 The edge from the list of ClassRelationshipEdges in the pGraph.
//	 * @param pGraph The current ClassDiagramGraph in the GraphPanel
//	 * @return true if the two edges have the same type, false otherwise.
//	 */
//	public boolean classDiagramEdgeEqual(ClassRelationshipEdge pEdge1, ClassRelationshipEdge pEdge2, Graph pGraph)
//	{
//		if(pEdge1.getLineStyle() == pEdge2.getLineStyle())
//		{
//			if(pEdge1.getStartArrowHead() == pEdge2.getStartArrowHead())
//			{
//				if(pEdge1.getEndArrowHead()== pEdge2.getEndArrowHead())
//				{
//					if(pEdge1.getBentStyle() == pEdge2.getBentStyle())
//					{
//						if(pEdge1.getMiddleLabel().equals(pEdge2.getMiddleLabel()))
//						{
//							return true;
//						}
//					}
//				}
//			}
//		}
//		return false;
//	}

}





