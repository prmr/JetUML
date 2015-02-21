package ca.mcgill.cs.stg.jetuml.commands;

import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.graph.Node;

/**
 * Stores the moving of a node.
 * @author EJBQ
 */
public class MoveCommand implements Command
{
	private Node aNode;
	private GraphPanel aGraphPanel;
	private double aDX;
	private double aDY;
	
	/**
	 * Creates the command.
	 * @param pGraphPanel The panel being moved on
	 * @param pNode The node being moved
	 * @param pDX The amount moved horizontally
	 * @param pDY The amount moved vertically
	 */
	public MoveCommand(GraphPanel pGraphPanel, Node pNode, double pDX, double pDY)
	{
		aGraphPanel = pGraphPanel;
		aNode = pNode;
		aDX = pDX;
		aDY = pDY;
	}
	
	/**
	 * Undoes the command and moves the node back where it came from.
	 */
	public void undo() 
	{
		aGraphPanel.moveNode(aNode, -aDX, -aDY);
		aGraphPanel.repaint();
	}

	/**
	 * Performs the command and moves the node.
	 */
	public void execute() 
	{
		aGraphPanel.moveNode(aNode, aDX, aDY);
		aGraphPanel.repaint();
	}

}
