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
 *   The bean info for the ClassNode type.
 */
public class ClassNodeBeanInfo extends SimpleBeanInfo
{
	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			// The first three properties have a priority value so they 
			// can be ordered in a property sheet.
			PropertyDescriptor nameDescriptor = new PropertyDescriptor("name", ClassNode.class);
			nameDescriptor.setValue("priority", new Integer(1));
			PropertyDescriptor attributesDescriptor = new PropertyDescriptor("attributes", ClassNode.class);
			attributesDescriptor.setValue("priority", new Integer(2));
			PropertyDescriptor methodsDescriptor = new PropertyDescriptor("methods", ClassNode.class);
			methodsDescriptor.setValue("priority", new Integer(3));
			
			return new PropertyDescriptor[]
            {
               nameDescriptor,
               attributesDescriptor,
               methodsDescriptor,
               // The last two properties are simply to capture all the important
               // object information that would be there by default if we did not
               // have this BeanInfo class definition
               new PropertyDescriptor("bounds", ClassNode.class),
               new PropertyDescriptor("parent", ClassNode.class)
            };
      }
      catch (IntrospectionException exception)
      {
         return null;
      }
   }
}

