/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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
package ca.mcgill.cs.stg.jetuml.framework;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ca.mcgill.cs.stg.jetuml.commands.CompoundCommand;
import ca.mcgill.cs.stg.jetuml.commands.PropertyChangeCommand;
import ca.mcgill.cs.stg.jetuml.graph.Graph;

/**
 * Tacks the modification of GraphElement properties.
 * 
 * @author Martin P. Robillard
 */
public class PropertyChangeTracker 
{
	private Object[] aPropertyValues;
	private Object aEdited;

	/**
	 * Records the value of the properties of pEdited.
	 * 
	 * @param pEdited The object being edited.
	 * 
	 */
	public void startTrackingPropertyChange(Object pEdited)
	{
		try 
		{
			aEdited = pEdited;
			PropertyDescriptor[] oldDescriptors = Introspector.getBeanInfo(pEdited.getClass()).getPropertyDescriptors();
			aPropertyValues = new Object[oldDescriptors.length];
			for(int i = 0; i< aPropertyValues.length; i++)
			{
				aPropertyValues[i] = getPropertyValue(oldDescriptors[i]);
			}
		} 
		catch( IntrospectionException | IllegalArgumentException e ) 
		{
			assert false;
			return;
		}
	}
	
	/*
	 * Returns the value of the property represented by pDescriptor on pEdited, or null
	 * if the property cannot be read for any reason.
	 */
	private Object getPropertyValue(PropertyDescriptor pDescriptor)
	{
		final Method getter = pDescriptor.getReadMethod();
		if( getter == null )
		{
			return null;
		}
		else
		{
			try
			{
				return copyIfNecessary(getter.invoke( aEdited ));
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				return null;
			}
		}
	}
	
	/*
	 * Encodes domain knowledge about which types of objects need to be 
	 * copied and which can safely be shared. If an object is mutable,
	 * it is copied.
	 */
	private static Object copyIfNecessary(Object pObject)
	{
		if(pObject instanceof MultiLineString)
		{
			return ((MultiLineString) pObject).clone();
		}
		else
		{
			return pObject;
		}
	}

	/**
	 * Creates and returns a CompoundCommand that represents any change
	 * in properties detected between the time startTrackingPropertyChange
	 * and stopTrackingPropertyChange were called.
	 * 
	 * @param pGraph The Graph containing the selected elements.
	 * @return A CompoundCommand describing the property changes.
	 */
	public CompoundCommand stopTrackingPropertyChange(Graph pGraph)
	{
		try 
		{
			CompoundCommand command = new CompoundCommand();
			PropertyDescriptor[] descriptors = Introspector.getBeanInfo(aEdited.getClass()).getPropertyDescriptors();  
			for(int i = 0; i < descriptors.length; i++)
			{
				Object propVal = getPropertyValue(descriptors[i]);
				if(!equals(propVal, aPropertyValues[i]))
				{
					command.add(new PropertyChangeCommand(pGraph, aEdited, aPropertyValues[i], copyIfNecessary(propVal), i));
				}
				
			}
			return command;
		}
		catch(IntrospectionException | IllegalArgumentException e) 
		{
			assert false;
			return null;
		}
	}
	
	/*
	 * Equality taking null equality into account.
	 */
	private static boolean equals(Object pObject1, Object pObject2)
	{
		if( pObject1 == null )
		{
			return pObject2 == null;
		}
		else
		{
			return pObject1.equals(pObject2);
		}
	}
}
