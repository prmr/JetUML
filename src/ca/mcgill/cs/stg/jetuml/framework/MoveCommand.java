package ca.mcgill.cs.stg.jetuml.framework;

import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class MoveCommand implements Command{
	Node aNode;
	Graph aGraph;
	double aDX;
	double aDY;
	
	public MoveCommand(Graph gGraph, Node pNode, double pDX, double pDY)
	{
		aNode = pNode;
		aDX = pDX;
		aDY = pDY;
	}
	
	public void undo() {
		aNode.translate(-aDX, -aDY);
	}

	/**
	 * Performs the command and moves the node
	 */
	public void execute() {
		aNode.translate(aDX, aDY);
	}

}
