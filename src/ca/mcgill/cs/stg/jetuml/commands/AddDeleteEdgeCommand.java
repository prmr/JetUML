package ca.mcgill.cs.stg.jetuml.commands;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class AddDeleteEdgeCommand implements Command{
	Edge aEdge;
	GraphPanel aGraphPanel;
	Node aP1;
	Node aP2;
	boolean aAdding; //true for adding, false for deleting
	
	public AddDeleteEdgeCommand(GraphPanel pGraphPanel, Edge pEdge, boolean pAdding)
	{
		aGraphPanel = pGraphPanel;
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
		aGraphPanel.removeEdge(aEdge);
		aGraphPanel.layout();
	}
	
	private void add() 
	{
		Point.Double n1Point = new Point.Double();
		n1Point.setLocation(aP1.getBounds().getX() + 1, aP1.getBounds().getY() + 1);
		Point.Double n2Point = new Point.Double();
		n2Point.setLocation(aP2.getBounds().getX() + 1, aP2.getBounds().getY() + 1);
		aGraphPanel.addEdge(aEdge, n1Point, n2Point);
		aGraphPanel.repaint();
	}
	
}
