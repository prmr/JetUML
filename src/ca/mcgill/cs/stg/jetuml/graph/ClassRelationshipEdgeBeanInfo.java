/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
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

package ca.mcgill.cs.stg.jetuml.graph;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 *   The bean info for the ClassRelationshipEdge type.
 */
public class ClassRelationshipEdgeBeanInfo extends SimpleBeanInfo
{
	private static PropertyDescriptor[] descriptors;
	
	static
	{
		try
		{
			descriptors = new PropertyDescriptor[] {
	            new PropertyDescriptor("startArrowHead", ClassRelationshipEdge.class),         
	            new PropertyDescriptor("startLabel", ClassRelationshipEdge.class),        
	            new PropertyDescriptor("middleLabel", ClassRelationshipEdge.class),         
	            new PropertyDescriptor("endLabel", ClassRelationshipEdge.class),     
	            new PropertyDescriptor("endArrowHead", ClassRelationshipEdge.class),         
	            new PropertyDescriptor("bentStyle", ClassRelationshipEdge.class),         
	            new PropertyDescriptor("lineStyle", ClassRelationshipEdge.class),         
	         };
	         
			for (int i = 0; i < descriptors.length; i++)
			{
				descriptors[i].setValue("priority", new Integer(i));
			}
		}
	    catch (IntrospectionException exception)
	    {
	    	exception.printStackTrace();
	    }
	}
	
	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return descriptors;
	}  
}

