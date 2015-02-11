package ca.mcgill.cs.stg.jetuml.commands;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class PropertyChangeCommand implements Command{
	Node aNode;
	GraphPanel aGraphPanel;
	Object aObject;
	String aPropName; 
	Object aPrevPropValue; 
	Object aNewPropValue;
	int aIndex;

	/**
	 * Creates the command and sets the values.
	 */
	public PropertyChangeCommand(GraphPanel pGraphPanel, Object pObject, String pPropName, Object pPrevPropValue, Object pNewPropValue, int pIndex)
	{
		aGraphPanel = pGraphPanel;
		aObject = pObject; 
		aPropName = pPropName;
		aPrevPropValue = pPrevPropValue;
		aNewPropValue = pNewPropValue;
		aIndex = pIndex;
	}

	
	/**
	 * Undoes the command and changes the property of the Object to the old value.
	 */
	public void undo() 
	{
		BeanInfo info;
		try
		{
			info = Introspector.getBeanInfo(aObject.getClass());
			PropertyDescriptor[] descriptors = (PropertyDescriptor[])info.getPropertyDescriptors();
			descriptors[aIndex].setValue(aPropName, aPrevPropValue);
		}
		catch(IntrospectionException e)
		{
			return;
		}
		aGraphPanel.repaint();
	}

	/**
	 * Performs the command and changes the property of the Object.
	 */
	public void execute() 
	{
		BeanInfo info;
		try
		{
			info = Introspector.getBeanInfo(aObject.getClass());
			PropertyDescriptor[] descriptors = (PropertyDescriptor[])info.getPropertyDescriptors();
			descriptors[aIndex].setValue(aPropName, aNewPropValue);
		}
		catch(IntrospectionException e)
		{
			return;
		}
		aGraphPanel.repaint();
	}

}
