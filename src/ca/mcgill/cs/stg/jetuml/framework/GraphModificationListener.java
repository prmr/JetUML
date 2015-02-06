package ca.mcgill.cs.stg.jetuml.framework;

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
import ca.mcgill.cs.stg.jetuml.framework.SelectionList;

public class GraphModificationListener {
	private CompoundCommand aCurCommand; //used for collecting commands being entered
	private UndoManager aUndoManager;
	private boolean aTrackPropChange = false;
	
	public GraphModificationListener(UndoManager pUndo)
	{
		aUndoManager = pUndo;
	}
	
	public void nodeAdded(Graph pGraph, Node pNode)
	{
		AddDeleteNodeCommand ac = new AddDeleteNodeCommand(pGraph, pNode, true);
		aUndoManager.add(ac);
	}

	public void nodeRemoved(Graph pGraph, Node pNode)
	{
		AddDeleteNodeCommand dc = new AddDeleteNodeCommand(pGraph, pNode, false);
		aUndoManager.add(dc);
	}

	public void nodeMoved(Graph pGraph, Node pNode, double dx, double dy)
	{
		MoveCommand mc = new MoveCommand(pGraph, pNode, dx, dy);
		aUndoManager.add(mc);
	}

	public void childAttached(Graph pGraph, int index, Node pNode1, Node pNode2)
	{
		
	}
	
	public void childDetached(Graph pGraph, int index, Node pNode1, Node pNode2)
	{
		
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

	public void edgeAdded(Graph pGraph, Edge pEdge)
	{
		AddDeleteEdgeCommand ac = new AddDeleteEdgeCommand(pGraph, pEdge, true);
		aUndoManager.add(ac);
	}

	public void edgeRemoved(Graph pGraph, Edge pEdge)
	{
		AddDeleteEdgeCommand dc = new AddDeleteEdgeCommand(pGraph, pEdge, false);
		aUndoManager.add(dc);
	}
	
	void propertyChangedOnNodeOrEdge(Graph pGraph, PropertyChangeEvent pEvent)
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
