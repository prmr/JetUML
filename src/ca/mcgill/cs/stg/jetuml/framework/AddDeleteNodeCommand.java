package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.Point;
import java.awt.geom.Point2D;

import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class AddDeleteNodeCommand implements Command{
	Node aNode;
	Graph aGraph;
	double aX;
	double aY;
	boolean aAdding; //true for adding, false for deleting
	
	public AddDeleteNodeCommand(Graph pGraph, Node pNode, boolean pAdding)
	{
		aGraph = pGraph;
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
