package ca.mcgill.cs.stg.jetuml.commands;

import java.awt.Point;

import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Node;

/**
 * Stores the addition/removal of a node from the graph.
 * @author EJBQ
 */
public class AddDeleteEdgeCommand implements Command
{
	private Edge aEdge;
	private GraphPanel aGraphPanel;
	private Node aP1;
	private Node aP2;
	private boolean aAdding; //true for adding, false for deleting
	
	/**
	 * Creates the command.
	 * @param pGraphPanel The panel to add/delete the edge
	 * @param pEdge The edge to be added/deleted
	 * @param pAdding True when adding, false when deleting
	 */
	public AddDeleteEdgeCommand(GraphPanel pGraphPanel, Edge pEdge, boolean pAdding)
	{
		aGraphPanel = pGraphPanel;
		aEdge = pEdge;
		aP1 = aEdge.getStart();
		aP2 = aEdge.getEnd();
		aAdding = pAdding;
	}
	
	/**
	 * Undoes the command and adds/deletes the edge.
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
		aGraphPanel.repaint();
	}

	/**
	 * Performs the command and adds/deletes the edge.
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

	/**
	 * Removes the node from the graph.
	 */
	private void delete() 
	{
		aGraphPanel.removeEdge(aEdge);
		aGraphPanel.layoutGraph();
		aGraphPanel.repaint();
	}
	
	/**
	 * Adds the edge to the graph at the points in its start and end node properties.
	 */
	private void add() 
	{
		Point.Double n1Point = new Point.Double();
		n1Point.setLocation(aP1.getBounds().getX() + 1, aP1.getBounds().getY() + 1);
		Point.Double n2Point = new Point.Double();
		n2Point.setLocation(aP2.getBounds().getX() + 1, aP2.getBounds().getY() + 1);
		aGraphPanel.addEdge(aEdge, n1Point, n2Point);
		aGraphPanel.layoutGraph();
		aGraphPanel.repaint();
	}
	
}
