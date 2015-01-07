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

package com.horstmann.violet.framework;

import java.beans.PropertyEditorSupport;

/**
   A helper class for showing names of objects in a property
   sheet that allows the user to pick one of a finite set of 
   named values.
*/
public class PropertySelector extends PropertyEditorSupport
{
   /**
      Constructs a selector that correlates names and objects.
      @param n the strings to display in a combo box
      @param v the corresponding object values
   */
   public PropertySelector(String[] n, Object[] v)
   {
      names = n;
      values = v;
   }

   public String[] getTags()
   {
      return names;
   }

   public String getAsText()
   {
      for (int i = 0; i < values.length; i++)
         if (getValue().equals(values[i])) return names[i];
      return null;
   }

   public void setAsText(String s)
   {
      for (int i = 0; i < names.length; i++)
         if (s.equals(names[i])) setValue(values[i]);
   }

   private String[] names;
   private Object[] values;
}
