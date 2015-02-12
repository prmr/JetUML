package ca.mcgill.cs.stg.jetuml.commands;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class PropertyChangeCommand implements Command{
	Node aNode;
	GraphPanel aGraphPanel;
	Object aObject;
	Object aPrevPropValue; 
	Object aNewPropValue;
	int aIndex;

	/**
	 * Creates the command and sets the values.
	 */
	public PropertyChangeCommand(GraphPanel pGraphPanel, Object pObject, Object pPrevPropValue, Object pNewPropValue, int pIndex)
	{
		aGraphPanel = pGraphPanel;
		aObject = pObject; 
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
			PropertyDescriptor[] descriptors = (PropertyDescriptor[])info.getPropertyDescriptors().clone();  
			final Method setter = descriptors[aIndex].getWriteMethod();
			setter.invoke(aObject, new Object[] {aPrevPropValue});
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
			PropertyDescriptor[] descriptors = (PropertyDescriptor[])info.getPropertyDescriptors().clone();  
			final Method setter = descriptors[aIndex].getWriteMethod();
			setter.invoke(aObject, new Object[] {aNewPropValue});
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
		aGraphPanel.repaint();
	}

}
