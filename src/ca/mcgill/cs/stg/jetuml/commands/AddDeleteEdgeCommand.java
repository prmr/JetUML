package ca.mcgill.cs.stg.jetuml.commands;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class AddDeleteEdgeCommand implements Command{
	Edge aEdge;
	GraphPanel aGraphPanel;
	Graph aGraph;
	Node aP1;
	Node aP2;
	boolean aAdding; //true for adding, false for deleting
	
	public AddDeleteEdgeCommand(GraphPanel pGraphPanel, Edge pEdge, boolean pAdding)
	{
		aGraphPanel = pGraphPanel;
		aGraph = pGraphPanel.getGraph();
		aEdge = pEdge;
		aP1 = aEdge.getStart();
		aP2 = aEdge.getEnd();
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
		aGraphPanel.repaint();
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
		aGraphPanel.repaint();
	}

	private void delete() 
	{
		aGraph.removeEdge(aEdge);
		aGraph.layout();
	}
	
	private void add() 
	{
		aGraph.connect(aEdge, aP1, aP2);
		aGraph.layout();
	}
	
}
