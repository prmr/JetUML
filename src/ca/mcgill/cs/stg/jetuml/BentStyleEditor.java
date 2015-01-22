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

package ca.mcgill.cs.stg.jetuml;

import ca.mcgill.cs.stg.jetuml.framework.PropertySelector;

/**
   A property editor for the BentStyle type.
*/
public class BentStyleEditor extends PropertySelector
{
   public BentStyleEditor()
   {
      super(names, values);
   }

   private static final String[] names = 
   { 
      "Straight", 
      "HV", 
      "VH", 
      "HVH", 
      "VHV" 
   };
   
   private static final Object[] values = 
   {
      BentStyle.STRAIGHT,
      BentStyle.HV,
      BentStyle.VH,
      BentStyle.HVH,
      BentStyle.VHV
   };
}
