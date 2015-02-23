package ca.mcgill.cs.stg.jetuml.commands;

import java.awt.Point;

import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.graph.Node;

/**
 * Stores the addition/removal of a node from the graph.
 * @author EJBQ
 */
public class AddDeleteNodeCommand implements Command
{
	private Node aNode;
	private GraphPanel aGraphPanel;
	private double aX;
	private double aY;
	private boolean aAdding; //true for adding, false for deleting
	
	/**
	 * Creates the command.
	 * @param pGraphPanel The panel to add/delete the node
	 * @param pNode The node to be added/deleted
	 * @param pAdding True when adding, false when deleting
	 */
	public AddDeleteNodeCommand(GraphPanel pGraphPanel, Node pNode, boolean pAdding)
	{
		aGraphPanel = pGraphPanel;
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
		aGraphPanel.removeNode(aNode);
		aGraphPanel.layoutGraph();
		aGraphPanel.repaint();
	}
	
	/**
	 * Adds the edge to the graph at the point in its properties.
	 */
	private void add() 
	{
		Point.Double point = new Point.Double(aX, aY);
		aGraphPanel.addNode(aNode, point);
		aGraphPanel.layoutGraph();
		aGraphPanel.repaint();
	}
	
}
