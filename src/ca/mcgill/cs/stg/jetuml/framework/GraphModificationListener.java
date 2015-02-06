package ca.mcgill.cs.stg.jetuml.framework;

import ca.mcgill.cs.stg.jetuml.UMLEditor;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.framework.SelectionList;

public class GraphModificationListener {
	private Command aCurCommand; //used for collecting commands being entered
	private UndoManager aUndoManager;
	
	public GraphModificationListener(UndoManager pUndo)
	{
		aUndoManager = pUndo;
	}
	
	void removeElements(SelectionList l)
	{
		if (l.size() == 0)
		{
			return;
		}
		else
		{
			aCurCommand= new Command();
			
			
			aUndoManager.addCommand(aCurCommand);
		}
	}
	
	void nodeAdded(Graph pGraph, Node pNode)
	{
		
	}

	void nodeRemoved(Graph pGraph, Node pNode)
	{
		
	}

	void nodeMoved(Graph pGraph, Node pNode, double dx, double dy)
	{
		
	}

	void childAttached(Graph pGraph, int index, Node pNode1, Node pNode2)
	{

	}

	void edgeAdded(Graph pGraph, Edge pEdge)
	{
		
	}

	void edgeRemoved(Graph pGraph, Edge pEdge)
	{
		
	}
//TODO
//Not added as the way to change properties yet
//	void propertyChangedOnNodeOrEdge(Graph g, PropertyChangeEvent event)
//	{
//
//	}

}
