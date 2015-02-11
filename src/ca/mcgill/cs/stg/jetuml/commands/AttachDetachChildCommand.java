package ca.mcgill.cs.stg.jetuml.commands;

import java.awt.Point;
import java.awt.geom.Point2D;

import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class AttachDetachChildCommand implements Command{
	Node aNode1;
	Node aNode2;
	GraphPanel aGraphPanel;
	double aX;
	double aY;
	boolean aAdding; //true for adding, false for deleting
	
	/**
	 * Gives us the pieces we need and sets up the command
	 * Here pNode2 is the child of pNode1, whether attaching or detaching
	 * @param pGraph
	 * @param pNode1
	 * @param pNode2
	 * @param pAdding
	 */
	public AttachDetachChildCommand(GraphPanel pGraphPanel, Node pNode1, Node pNode2, boolean pAdding)
	{
		aGraphPanel = pGraphPanel;
		aNode1 = pNode1;
		aNode2 = pNode2;
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
		
		aGraphPanel.repaint();

	}
	
	private void add() 
	{
		
		aGraphPanel.repaint();
	}
	
}