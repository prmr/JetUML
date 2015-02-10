package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.geom.Rectangle2D;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.Iterator;

import javax.swing.JLabel;

import ca.mcgill.cs.stg.jetuml.UMLEditor;
import ca.mcgill.cs.stg.jetuml.graph.GraphElement;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.commands.AddDeleteEdgeCommand;
import ca.mcgill.cs.stg.jetuml.commands.AddDeleteNodeCommand;
import ca.mcgill.cs.stg.jetuml.commands.CompoundCommand;
import ca.mcgill.cs.stg.jetuml.commands.MoveCommand;
import ca.mcgill.cs.stg.jetuml.framework.SelectionList;

public class GraphModificationListener {
	private CompoundCommand aCurCommand; //used for collecting commands being entered
	private UndoManager aUndoManager;
	private boolean aTrackPropChange = false;
	private Node[] selectionNodes;
	private Rectangle2D[] selectionBounds;
	
	public GraphModificationListener(UndoManager pUndo)
	{
		aUndoManager = pUndo;
	}
	
	public void nodeAdded(GraphPanel pGraphPanel, Node pNode)
	{
		AddDeleteNodeCommand ac = new AddDeleteNodeCommand(pGraphPanel, pNode, true);
		aUndoManager.add(ac);
	}

	public void nodeRemoved(GraphPanel pGraphPanel, Node pNode)
	{
		AddDeleteNodeCommand dc = new AddDeleteNodeCommand(pGraphPanel, pNode, false);
		aUndoManager.add(dc);
	}

	public void nodeMoved(GraphPanel pGraphPanel, Node pNode, double dx, double dy)
	{
		MoveCommand mc = new MoveCommand(pGraphPanel, pNode, dx, dy);
		aUndoManager.add(mc);
	}

	public void childAttached(GraphPanel pGraphPanel, int index, Node pNode1, Node pNode2)
	{
		
	}
	
	public void childDetached(GraphPanel pGraphPanel, int index, Node pNode1, Node pNode2)
	{
		
	}
	
	/**
	 * Tracks the elements in pSelectedElements and records their positions
	 * Used to many commands at once
	 */
	public void startTrackingMove(GraphPanel pGraphPanel, SelectionList pSelectedElements)
	{
		selectionNodes = new Node[pSelectedElements.size()];
		selectionBounds = new Rectangle2D[pSelectedElements.size()];
		int i = 0;
		for(GraphElement e : pSelectedElements)
		{
			if(e instanceof Node)
			{
				selectionNodes[i] = (Node) e;
				selectionBounds[i] = selectionNodes[i].getBounds();
				i++;
			}
		}
	}
	
	/**
	 * Creates a compound command with each node move and adds it to the stack
	 */
	public void endTrackingMove(GraphPanel pGraphPanel, SelectionList pSelectedElements)
	{
		CompoundCommand cc = new CompoundCommand();
		Rectangle2D[] selectionBounds2 = new Rectangle2D[pSelectedElements.size()];
		int i = 0;
		for(GraphElement e : pSelectedElements)
		{
			if(e instanceof Node)
			{
				selectionBounds2[i] = ((Node) e).getBounds();
				i++;
			}
		}
		for(i = 0; i<selectionNodes.length && selectionNodes[i] != null; i++)
		{
			double dY = selectionBounds2[i].getY() - selectionBounds[i].getY();
			double dX = selectionBounds2[i].getX() - selectionBounds[i].getX();
			if (dX > 0 || dY > 0)
			{
				cc.add(new MoveCommand(pGraphPanel, selectionNodes[i], dX, dY));
			}
		}
		if (cc.size() > 0) {
			aUndoManager.add(cc);
		}
	}
//	public void trackPropertyChange(Graph aGraph, Object edited)
//	{
//		aTrackPropChange = true;
//		BeanInfo info;
//		try {
//			info = Introspector.getBeanInfo(edited.getClass());
//			PropertyDescriptor[] descriptors = (PropertyDescriptor[])info.getPropertyDescriptors().clone();  
//			for(int i = 0; i < descriptors.length; i++)
//			{
//				PropertyEditor editor = getEditor(edited, descriptors[i]);
//				if(editor != null)
//				{
//					
//					
//				}
//			}		
//		} catch (IntrospectionException e) {
//			return;
//		}   
//		
//	}
	
//	public void finishPropertyChange(Graph aGraph, Object edited)
//	{
//		BeanInfo info;
//		try {
//			info = Introspector.getBeanInfo(edited.getClass());
//			PropertyDescriptor[] descriptors = (PropertyDescriptor[])info.getPropertyDescriptors().clone();  
//			
//			
//			
//			
//		} catch (IntrospectionException e) {
//			return;
//		}  
//		aTrackPropChange = false;
//	}

	public void edgeAdded(GraphPanel pGraphPanel, Edge pEdge)
	{
		AddDeleteEdgeCommand ac = new AddDeleteEdgeCommand(pGraphPanel, pEdge, true);
		aUndoManager.add(ac);
	}

	public void edgeRemoved(GraphPanel pGraphPanel, Edge pEdge)
	{
		AddDeleteEdgeCommand dc = new AddDeleteEdgeCommand(pGraphPanel, pEdge, false);
		aUndoManager.add(dc);
	}
	
	void propertyChangedOnNodeOrEdge(GraphPanel pGraphPanel, PropertyChangeEvent pEvent)
	{
		
	}

//	public void removeElements(Graph pGraph, SelectionList l)
//	{
//		if (l.size() == 0)
//		{
//			return;
//		}
//		else
//		{
//			aCurCommand= new CompoundCommand();
//			Iterator<GraphElement> i = l.iterator();
//			for( GraphElement e : l)
//			{
//				if (e instanceof Node)
//				{
//					nodeRemoved(pGraph, (Node)e);
//				}
//				else if (e instanceof Edge)
//				{
//					edgeRemoved(pGraph, (Edge)e);
//				}
//			}
//			aUndoManager.add(aCurCommand);
//		}
//	}
}
