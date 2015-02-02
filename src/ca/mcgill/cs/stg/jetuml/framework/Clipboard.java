package ca.mcgill.cs.stg.jetuml.framework;

import java.util.List;

import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

/**
 * Application-specific clipboard to hold grap data
 * for copy and paste operations.
 * 
 * @author Martin P. Robillard
 */
public class Clipboard
{
	private Class<? extends Graph> aDiagramType; // We can use the diagram class to tag the type of nodes and edges.
	private List<Node> aNodes;
	private List<Edge> aEdges;
	
	/**
	 * Looks at all the elements currently selected and makes 
	 * some intelligent decision about what to add in here, e.g., bring
	 * in the edges between selected nodes, etc.
	 * 
	 * @param pSelection
	 */
	public void setContent(Class<? extends Graph> pDiagramType, SelectionList pSelection)
	{
		
	}
}
