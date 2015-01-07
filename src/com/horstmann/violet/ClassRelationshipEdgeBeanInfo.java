/*
Violet - A program for editing UML diagrams.

Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.horstmann.violet;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
   The bean info for the ClassRelationshipEdge type.
*/
public class ClassRelationshipEdgeBeanInfo extends SimpleBeanInfo
{
   public PropertyDescriptor[] getPropertyDescriptors()
   {
      return descriptors;
   }
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
            descriptors[i].setValue("priority", new Integer(i));
      }
      catch (IntrospectionException exception)
      {
         exception.printStackTrace();
      }
   }
}

