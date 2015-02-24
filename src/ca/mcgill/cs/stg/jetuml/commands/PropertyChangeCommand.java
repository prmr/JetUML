package ca.mcgill.cs.stg.jetuml.commands;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;

/**
 * Stores the change of properties of an element.
 * @author EJBQ
 *
 */
public class PropertyChangeCommand implements Command
{
	private GraphPanel aGraphPanel;
	private Object aObject;
	private Object aPrevPropValue; 
	private Object aNewPropValue;
	private int aIndex;

	/**
	 * Creates the command and sets the values of the element. These should be clones so they do not get edited.
	 * @param pGraphPanel The panel of the object being changed.
	 * @param pObject The graph element being transformed
	 * @param pPrevPropValue The initial value
	 * @param pNewPropValue The new value
	 * @param pIndex Which property of the object this is, so that we can select it
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
			if (setter != null)
			{
				setter.invoke(aObject, new Object[] {aPrevPropValue});
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
		aGraphPanel.layoutGraph();
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
			if(setter!= null)
			{
				setter.invoke(aObject, new Object[] {aNewPropValue});
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
		aGraphPanel.layoutGraph();
		aGraphPanel.repaint();
	}

}
