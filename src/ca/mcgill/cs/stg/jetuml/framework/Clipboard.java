package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.ObjectDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.SequenceDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.StateDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.UseCaseDiagramGraph;
import ca.mcgill.cs.stg.jetuml.graph.CallEdge;
import ca.mcgill.cs.stg.jetuml.graph.CallNode;
import ca.mcgill.cs.stg.jetuml.graph.ClassRelationshipEdge;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.GraphElement;
import ca.mcgill.cs.stg.jetuml.graph.ImplicitParameterNode;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.ObjectReferenceEdge;
import ca.mcgill.cs.stg.jetuml.graph.ReturnEdge;
import ca.mcgill.cs.stg.jetuml.graph.StateTransitionEdge;

/**
 * @author JoelChev
 * 
 * A class that will be used to store the current graph for cutting/copying and then pasting.
 *
 */
public final class Clipboard 
{
	//private Class<? extends Graph> aDiagramType; // We can use the diagram class to tag the type of nodes and edges.
	private List<Node> aNodes;
	private List<Edge> aEdges;

	/**
	 * A constructor for a Clipboard object.
	 */
	public Clipboard() 
	{
	}

	/**
	 * @param pSelection The currently selected elements to add to the Clipboard.
	 */
	public void addSelection(SelectionList pSelection)
	{
		aNodes = new ArrayList<Node>();
		aEdges = new ArrayList<Edge>();
		Map<Node, Node> originalAndClonedNodes = new LinkedHashMap<Node, Node>();
		for(GraphElement element: pSelection)
		{
			if(element instanceof Node)
			{
				Node curNode = (Node) element;
				Node cloneNode = curNode.clone();
 				originalAndClonedNodes.put(curNode, cloneNode);
				aNodes.add(cloneNode);
				//Add children to the Selection if they are not in the current Selection.
				for(Node childNode:curNode.getChildren())
				{
					if(!(pSelection.contains(childNode)))
					{
						Node clonedChildNode = childNode.clone();
						originalAndClonedNodes.put(childNode, clonedChildNode);
						aNodes.add(clonedChildNode);
					}
				}
			}
		}
		for(GraphElement element: pSelection) //loop through and fix the parent/child relationships for all the clone children
		{
			if(element instanceof Node)
			{
				Node curNode = (Node) element;
				if(!curNode.getChildren().isEmpty())
				{
					Node cloneNode = originalAndClonedNodes.get(curNode);
					List<Node> cloneChildren = cloneNode.getChildren();
					for(int i = 0; i < cloneChildren.size(); i++) //Repalce all children with their clones
					{
						Node removed = cloneChildren.remove(i);
						Node replacement = originalAndClonedNodes.get(removed);
						cloneChildren.add(i, replacement);
					}
				}
				if(curNode.getParent() != null)
				{
					Node cloneNode = originalAndClonedNodes.get(curNode); //replace parent with its clone
					Node cloneParent = originalAndClonedNodes.get(curNode.getParent());
					cloneNode.setParent(cloneParent);
				}
				if(curNode instanceof CallNode && ((CallNode)curNode).getImplicitParameter() != null)
				{
					Node cloneNode = originalAndClonedNodes.get(curNode); //replace parent with its clone
					Node cloneParent = originalAndClonedNodes.get(((CallNode)curNode).getImplicitParameter());
					((CallNode)cloneNode).setImplicitParameter((ImplicitParameterNode)cloneParent);
				}
			}
		}
		for(GraphElement element: pSelection)
		{
			if(element instanceof Edge)
			{
				Edge curEdge = (Edge) element;
				Node start = originalAndClonedNodes.get(curEdge.getStart());
				Node end = originalAndClonedNodes.get(curEdge.getEnd());  
				if (start != null && end != null)
				{
					Edge cloneEdge = (Edge) curEdge.clone();
					cloneEdge.connect(start, end);
					aEdges.add(cloneEdge);
				}
			}
		}
	}

	/**
	 * A wrapper method for the pasteInto method without the pOriginalPositions parameter.
	 * @param pGraphPanel The current GraphPanel to paste contents to.
	 * @return The elements to paste as a selectionList.
	 */
	public SelectionList pasteInto(GraphPanel pGraphPanel)
	{
		return pasteInto(pGraphPanel, false);
	}

	/**
	 * @param pGraphPanel The current GraphPanel to paste contents to.
	 * @param pOriginalPositions Whether to paste in the original position or not.
	 * @return The elements to paste as a selectionList.
	 */
	public SelectionList pasteInto(GraphPanel pGraphPanel, boolean pOriginalPositions)
	{
		Rectangle2D bounds = null;
		Graph aGraph = pGraphPanel.getGraph();
		Node[]currentProtoTypes = aGraph.getNodePrototypes();
		Edge[]currentEdgeTypes = aGraph.getEdgePrototypes();
		ArrayList<Node> copyNodes = new ArrayList<Node>();
		/*
		 * Clone all nodes and remember the original-cloned correspondence
		 */
		Map<Node, Node> originalAndClonedNodes = new LinkedHashMap<Node, Node>();
		/*
		 * First clone all of the nodes and link them with the previous nodes. All the nodes will be iterated over in
		 * the pastSelection SelectionList
		 */
		for(Node curNode: aNodes)
		{
			for(Node n: currentProtoTypes)
			{
				if(curNode.getClass() == n.getClass())
				{
					Node newNode = curNode.clone();
					originalAndClonedNodes.put(curNode, newNode);
					copyNodes.add(newNode);

					if(bounds ==null)
					{
						bounds = curNode.getBounds();
					}
					else
					{
						bounds.add(curNode.getBounds());
					}
				}
			}

		}
		/*
		 * Now the edges can be cloned as all the nodes have been cloned successfully at this point.
		 * The edges will be iterated over in the pastSelection SelectionList.
		 */
		ArrayList<Edge> copyEdges = new ArrayList<Edge>();
		for( Edge curEdge: aEdges)
		{
			for(Edge e: currentEdgeTypes)
			{
				/*
				 * Clone all edges that join copied nodes
				 */
				Node start = originalAndClonedNodes.get(curEdge.getStart());
				Node end = originalAndClonedNodes.get(curEdge.getEnd());  
				if(checkEdgeEquality(curEdge, e, aGraph) &&start != null && end != null)
				{
						Edge e2 = (Edge) e.clone();
						e2.connect(start, end);
						copyEdges.add(e2);
				}
			}
		}	
		/*
		 * updatedSelectionList is the selectionList to return.
		 */
		SelectionList updatedSelectionList = new SelectionList();
		pGraphPanel.startCompoundListening();
		for(Node cloneNode: copyNodes)
		{
			double x = cloneNode.getBounds().getX();
			double y = cloneNode.getBounds().getY();
			if(!pOriginalPositions)
			{
				/*
				 * This translates all the new nodes and their respective edges over to the top left corner of the 
				 * GraphPanel.
				 */
				aGraph.add(cloneNode, new Point2D.Double(x-bounds.getX(), y-bounds.getY()));
			}
			else
			{
				/*
				 * If we care about the original positions we just put them back there
				 */
				aGraph.addNode(cloneNode, new Point2D.Double(x, y));
			}
			/*
			 * Don't add any Children to the SelectionList
			 */
			if(cloneNode.getParent()==null)
			{
				updatedSelectionList.add(cloneNode);
				
			}
		}
		for(Edge cloneEdge: copyEdges)
		{
			aGraph.connect(cloneEdge, cloneEdge.getStart(), cloneEdge.getEnd());
			updatedSelectionList.add(cloneEdge);
		}
		pGraphPanel.endCompoundListening();
		return updatedSelectionList;
	}

	/**
	 * @param pEdge1 The copied or cut edge whose actual type needs to be determined.
	 * @param pEdge2 The edge from the list of edge types in the pGraph.
	 * @param pGraph The current graph in the GraphPanel.
	 * @return true if the two edges have the same type and false if not.
	 * NOTE: All Note edges are removed following this check.
	 */
	public boolean checkEdgeEquality(Edge pEdge1, Edge pEdge2, Graph pGraph)
	{
		boolean equal = false;
		if(pGraph instanceof ClassDiagramGraph || pGraph instanceof UseCaseDiagramGraph)
		{
			if(pEdge1 instanceof ClassRelationshipEdge && pEdge2 instanceof ClassRelationshipEdge)
			{
				equal = classDiagramEdgeEqual((ClassRelationshipEdge)pEdge1, (ClassRelationshipEdge)pEdge2, pGraph);

			}
		}
		else if(pGraph instanceof ObjectDiagramGraph)
		{
			if(pEdge1 instanceof ClassRelationshipEdge && pEdge2 instanceof ClassRelationshipEdge || 
					pEdge1 instanceof ObjectReferenceEdge && pEdge2 instanceof ObjectReferenceEdge)
			{
				equal = true;
			}
		}
		else if(pGraph instanceof SequenceDiagramGraph)
		{
			if(pEdge1 instanceof CallEdge && pEdge2 instanceof CallEdge || pEdge1 instanceof ReturnEdge && pEdge2 instanceof ReturnEdge)
			{
				equal = true;
			}
		}
		else if(pGraph instanceof StateDiagramGraph)
		{
			if(pEdge1 instanceof StateTransitionEdge && pEdge2 instanceof StateTransitionEdge)
			{
				equal = true;
			}
		}
		return equal;
	}

	/**
	 * @param pEdge1 The copied or cut Edge whose actual type needs to be determined.
	 * @param pEdge2 The edge from the list of ClassRelationshipEdges in the pGraph.
	 * @param pGraph The current ClassDiagramGraph in the GraphPanel
	 * @return true if the two edges have the same type, false otherwise.
	 */
	public boolean classDiagramEdgeEqual(ClassRelationshipEdge pEdge1, ClassRelationshipEdge pEdge2, Graph pGraph)
	{
		if(pEdge1.getLineStyle() == pEdge2.getLineStyle())
		{
			if(pEdge1.getStartArrowHead() == pEdge2.getStartArrowHead())
			{
				if(pEdge1.getEndArrowHead()== pEdge2.getEndArrowHead())
				{
					if(pEdge1.getBentStyle() == pEdge2.getBentStyle())
					{
						if(pEdge1.getMiddleLabel().equals(pEdge2.getMiddleLabel()))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}





