package ca.mcgill.cs.stg.jetuml.commands;

import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class MoveCommand implements Command{
	Node aNode;
	GraphPanel aGraphPanel;
	Graph aGraph;
	double aDX;
	double aDY;
	
	public MoveCommand(GraphPanel pGraphPanel, Node pNode, double pDX, double pDY)
	{
		aGraphPanel = pGraphPanel;
		aGraph = pGraphPanel.getGraph();
		aNode = pNode;
		aDX = pDX;
		aDY = pDY;
	}
	
	public void undo() {
		aNode.translate(-aDX, -aDY);
		aGraphPanel.repaint();
	}

	/**
	 * Performs the command and moves the node
	 */
	public void execute() {
		aNode.translate(aDX, aDY);
		aGraphPanel.repaint();
	}

}
