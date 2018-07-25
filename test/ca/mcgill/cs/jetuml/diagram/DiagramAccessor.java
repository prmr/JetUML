package ca.mcgill.cs.jetuml.diagram;

import java.util.ArrayList;
import java.util.List;

/**
 * Broadens the interface to a diagram, to facilitate testing.
 */
public class DiagramAccessor
{
	private final Diagram aDiagram;
	
	public DiagramAccessor(Diagram pDiagram)
	{
		aDiagram = pDiagram;
	}
	
	public List<Node> getRootNodes()
	{
		ArrayList<Node> result = new ArrayList<>();
		for( Node node : aDiagram.rootNodes() )
		{
			result.add(node);
		}
		return result;
	}
	
	public List<Edge> getEdges()
	{
		ArrayList<Edge> result = new ArrayList<>();
		for( Edge edge : aDiagram.edges() )
		{
			result.add(edge);
		}
		return result;
	}
	
	public List<Edge> getEdgesConnectedTo(Node pNode)
	{
		ArrayList<Edge> result = new ArrayList<>();
		for( Edge edge : aDiagram.edgesConnectedTo(pNode))
		{
			result.add(edge);
		}
		return result;
	}
	
	public void connectAndAdd(Edge pEdge, Node pStart, Node pEnd)
	{
		pEdge.connect(pStart, pEnd, aDiagram);
		aDiagram.addEdge(pEdge);
	}
}
