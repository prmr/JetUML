package ca.mcgill.cs.stg.jetuml.commands;

import java.awt.Point;
import java.awt.geom.Point2D;

import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class AddDeleteNodeCommand implements Command{
	Node aNode;
	GraphPanel aGraphPanel;
	Graph aGraph;
	double aX;
	double aY;
	boolean aAdding; //true for adding, false for deleting
	
	public AddDeleteNodeCommand(GraphPanel pGraphPanel, Node pNode, boolean pAdding)
	{
		aGraphPanel = pGraphPanel;
		aGraph = pGraphPanel.getGraph();
		aNode = pNode;
		aX = aNode.getBounds().getMinX();
		aY = aNode.getBounds().getMinY();
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
		aGraph.removeNode(aNode);
		aGraph.layout();
	}
	
	private void add() 
	{
		Point2D point = new Point.Double(aX, aY);
		aGraph.add(aNode, point);
		aGraph.layout();
	}
	
}
