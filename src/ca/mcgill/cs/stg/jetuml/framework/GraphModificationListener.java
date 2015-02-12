package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.geom.Rectangle2D;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ca.mcgill.cs.stg.jetuml.graph.GraphElement;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.commands.AddDeleteEdgeCommand;
import ca.mcgill.cs.stg.jetuml.commands.AddDeleteNodeCommand;
import ca.mcgill.cs.stg.jetuml.commands.CompoundCommand;
import ca.mcgill.cs.stg.jetuml.commands.MoveCommand;
import ca.mcgill.cs.stg.jetuml.commands.PropertyChangeCommand;

public class GraphModificationListener 
{
	private CompoundCommand aCurCommand; //used for collecting commands being entered
	private UndoManager aUndoManager;
	private Node[] aSelectionNodes;
	private Rectangle2D[] aSelectionBounds;
	private Object[] aPropertyValues;

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
	 * Tracks the elements in pSelectedElements and records their positions.
	 */
	public void startTrackingMove(GraphPanel pGraphPanel, SelectionList pSelectedElements)
	{
		aSelectionNodes = new Node[pSelectedElements.size()];
		aSelectionBounds = new Rectangle2D[pSelectedElements.size()];
		int i = 0;
		for(GraphElement e : pSelectedElements)
		{
			if(e instanceof Node)
			{
				aSelectionNodes[i] = (Node) e;
				aSelectionBounds[i] = aSelectionNodes[i].getBounds();
				i++;
			}
		}
	}

	/**
	 * Creates a compound command with each node move and adds it to the stack.
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
		for(i = 0; i<aSelectionNodes.length && aSelectionNodes[i] != null; i++)
		{
			double dY = selectionBounds2[i].getY() - aSelectionBounds[i].getY();
			double dX = selectionBounds2[i].getX() - aSelectionBounds[i].getX();
			if (dX > 0 || dY > 0)
			{
				cc.add(new MoveCommand(pGraphPanel, aSelectionNodes[i], dX, dY));
			}
		}
		if (cc.size() > 0) 
		{
			aUndoManager.add(cc);
		}
	}

	public void trackPropertyChange(GraphPanel aGraphPanel, Object edited)
	{
		BeanInfo info;
		try 
		{
			info = Introspector.getBeanInfo(edited.getClass());
			PropertyDescriptor[] oldDescriptors = (PropertyDescriptor[])info.getPropertyDescriptors().clone();
			aPropertyValues = new Object[oldDescriptors.length];
			for(int i = 0; i< aPropertyValues.length; i++)
			{
				final Method getter = oldDescriptors[i].getReadMethod();
				aPropertyValues[i] = getter.invoke(edited, new Object[] {});
				if(aPropertyValues[i] instanceof MultiLineString)
				{
					MultiLineString temp = (MultiLineString) aPropertyValues[i];
					aPropertyValues[i] = temp.clone();
				}
			}
		} 
		catch (IntrospectionException e) 
		{
			e.printStackTrace();
			return;
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
		{
			e.printStackTrace();
			return;
		}

	}

	public void finishPropertyChange(GraphPanel aGraphPanel, Object edited)
	{
		BeanInfo info;
		CompoundCommand cc = new CompoundCommand();
		try 
		{
			info = Introspector.getBeanInfo(edited.getClass());
			PropertyDescriptor[] descriptors = (PropertyDescriptor[])info.getPropertyDescriptors().clone();  
			for(int i = 0; i<descriptors.length; i++)
			{
				final Method getter = descriptors[i].getReadMethod();
				Object propVal = getter.invoke(edited, new Object[] {});
				if (!propVal.equals(aPropertyValues[i]))
				{
					Object oldPropValue = aPropertyValues[i];
					Object propValue;
					if(aPropertyValues[i] instanceof MultiLineString)
					{
						MultiLineString temp = (MultiLineString) propVal;
						propValue = temp.clone();
					}
					else
					{
						propValue = propVal;
					}
					cc.add(new PropertyChangeCommand(aGraphPanel, edited, oldPropValue, propValue, i));
				}
			}
		}
		catch (IntrospectionException e) 
		{
			e.printStackTrace();
			return;
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
		{
			e.printStackTrace();
			return;
		}
		finally
		{
			if (cc.size() > 0)
			{
				aUndoManager.add(cc);
			}
		}
	}

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

}
