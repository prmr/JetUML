/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 by the contributors of the JetUML project.
 *
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ca.mcgill.cs.stg.jetuml.commands;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ca.mcgill.cs.stg.jetuml.graph.Graph;

/**
 * Stores the change of properties of an element.
 * @author EJBQ
 *
 */
public class PropertyChangeCommand implements Command
{
	private Graph aGraph;
	private Object aObject;
	private Object aPrevPropValue; 
	private Object aNewPropValue;
	private int aIndex;

	/**
	 * Creates the command and sets the values of the element. These should be clones so they do not get edited.
	 * @param pGraph The panel of the object being changed.
	 * @param pObject The graph element being transformed
	 * @param pPrevPropValue The initial value
	 * @param pNewPropValue The new value
	 * @param pIndex Which property of the object this is, so that we can select it
	 */
	public PropertyChangeCommand(Graph pGraph, Object pObject, Object pPrevPropValue, Object pNewPropValue, int pIndex)
	{
		aGraph = pGraph;
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
			PropertyDescriptor[] descriptors = info.getPropertyDescriptors().clone();  
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
		aGraph.layout();
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
			PropertyDescriptor[] descriptors = info.getPropertyDescriptors().clone();  
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
		aGraph.layout();
	}

}
