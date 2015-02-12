package ca.mcgill.cs.stg.jetuml.commands;

import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class MoveCommand implements Command
{
	Node aNode;
	GraphPanel aGraphPanel;
	double aDX;
	double aDY;
	
	public MoveCommand(GraphPanel pGraphPanel, Node pNode, double pDX, double pDY)
	{
		aGraphPanel = pGraphPanel;
		aNode = pNode;
		aDX = pDX;
		aDY = pDY;
	}
	
	public void undo() 
	{
		aGraphPanel.moveNode(aNode, -aDX, -aDY);
		aGraphPanel.repaint();
	}

	/**
	 * Performs the command and moves the node
	 */
	public void execute() 
	{
		aGraphPanel.moveNode(aNode, aDX, aDY);
		aGraphPanel.repaint();
	}

}
