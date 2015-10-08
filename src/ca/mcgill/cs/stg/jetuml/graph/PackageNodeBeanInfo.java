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
 *  The bean info for the PackageNode type.
 *  @author Martin P. Robillard
 */
public class PackageNodeBeanInfo extends SimpleBeanInfo
{
	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			// The first two properties have a priority value so they 
			// can be ordered in a property sheet.
			PropertyDescriptor nameDescriptor = new PropertyDescriptor("name", PackageNode.class);
			nameDescriptor.setValue("priority", new Integer(1));
			PropertyDescriptor contentsDescriptor = new PropertyDescriptor("contents", PackageNode.class);
			contentsDescriptor.setValue("priority", new Integer(2));
			
			return new PropertyDescriptor[]
            {
               nameDescriptor,
               contentsDescriptor,
               // The last two properties are simply to capture all the important
               // object information that would be there by default if we did not
               // have this BeanInfo class definition
               new PropertyDescriptor("bounds", PackageNode.class),
               new PropertyDescriptor("parent", PackageNode.class)
            };
      }
      catch (IntrospectionException exception)
      {
         return null;
      }
   }
}

