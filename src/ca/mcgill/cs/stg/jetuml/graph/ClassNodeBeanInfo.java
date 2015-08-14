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

import java.beans.SimpleBeanInfo;

/**
 *   The bean info for the ClassNode type.
 */
public class ClassNodeBeanInfo extends SimpleBeanInfo
{
	// prmr we should not use these to alter the presentation
	// of properties as it's also used by the serializer
//	@Override
//	public PropertyDescriptor[] getPropertyDescriptors()
//	{
//		try
//		{
//			PropertyDescriptor nameDescriptor = new PropertyDescriptor("name", ClassNode.class);
//			nameDescriptor.setValue("priority", new Integer(1));
//			PropertyDescriptor attributesDescriptor = new PropertyDescriptor("attributes", ClassNode.class);
//			attributesDescriptor.setValue("priority", new Integer(2));
//			PropertyDescriptor methodsDescriptor = new PropertyDescriptor("methods", ClassNode.class);
//			methodsDescriptor.setValue("priority", new Integer(3));
//			return new PropertyDescriptor[]
//            {
//               nameDescriptor,
//               attributesDescriptor,
//               methodsDescriptor
//            };
//      }
//      catch (IntrospectionException exception)
//      {
//         return null;
//      }
//   }
}

