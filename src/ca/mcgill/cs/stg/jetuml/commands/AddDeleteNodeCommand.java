package ca.mcgill.cs.stg.jetuml.commands;

import java.awt.Point;

import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

/**
 * Stores the addition/removal of a node from the graph.
 * @author EJBQ
 */
public class AddDeleteNodeCommand implements Command
{
	private Node aNode;
	private Graph aGraph;
	private double aX;
	private double aY;
	private boolean aAdding; //true for adding, false for deleting
	
	/**
	 * Creates the command.
	 * @param pGraph The panel to add/delete the node
	 * @param pNode The node to be added/deleted
	 * @param pAdding True when adding, false when deleting
	 */
	public AddDeleteNodeCommand(Graph pGraph, Node pNode, boolean pAdding)
	{
		aGraph = pGraph;
		aNode = pNode;
		aX = aNode.getBounds().getMinX();
		aY = aNode.getBounds().getMinY();
		aAdding = pAdding;
	}
	
	/**
	 * Undoes the command and adds/deletes the node.
	 */
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
	 * Performs the command and adds/deletes the node.
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

	/**
	 * Removes the node from the graph.
	 */
	private void delete() 
	{
		aGraph.removeNode(aNode);
		aGraph.layout();
	}
	
	/**
	 * Adds the edge to the graph at the point in its properties.
	 */
	private void add() 
	{
		Point.Double point = new Point.Double(aX, aY);
		aGraph.add(aNode, point);
		aGraph.layout();
	}
	
}
