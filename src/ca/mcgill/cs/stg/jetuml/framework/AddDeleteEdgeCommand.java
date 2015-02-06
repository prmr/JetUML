package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;

public class AddDeleteEdgeCommand implements Command{
	Edge aEdge;
	Graph aGraph;
	Point2D aP1;
	Point2D aP2;
	boolean aAdding; //true for adding, false for deleting
	
	public AddDeleteEdgeCommand(Graph pGraph, Edge pEdge, boolean pAdding)
	{
		aGraph = pGraph;
		aEdge = pEdge;
		Line2D ends = aEdge.getConnectionPoints();
		aP1 = ends.getP1();
		aP2 = ends.getP2();
		aAdding = pAdding;
	}
	
	public void undo() 
	{
		if(aAdding)
		{
			delete();
		}
		else
		{
			add();
		}
	}

	/**
	 * Performs the command and adds the node
	 */
	public void execute() 
	{
		if(aAdding)
		{
			add();
		}
		else
		{
			delete();
		}
	}

	private void delete() 
	{
		aGraph.removeEdge(aEdge);
		aGraph.layout();
	}
	
	private void add() 
	{
		aGraph.connect(aEdge, aGraph.findNode(aP1), aGraph.findNode(aP2));
		aGraph.layout();
	}
	
}
