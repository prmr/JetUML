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

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
 *   This class defines line styles of various shapes.
 */
public final class LineStyle
{
	public static final LineStyle SOLID = new LineStyle();
	   public static final LineStyle DOTTED = new LineStyle();
	
	   private static final Stroke SOLID_STROKE = new BasicStroke();
	   private static final Stroke DOTTED_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, 
			   BasicStroke.JOIN_MITER, 10.0f, new float[] { 3.0f, 3.0f }, 0.0f);
	
   private LineStyle() {}

   /**
      Gets a stroke with which to draw this line style.
      @return the stroke object that strokes this line style
   */
   public Stroke getStroke()
   {
      if (this == DOTTED)
	{
		return DOTTED_STROKE;
	}
      return SOLID_STROKE;
   }

   @Override
   public String toString()
   {
	   if( this == SOLID )
	   {
		   return "SOLID";
	   }
	   else if( this == DOTTED )
	   {
		   return "DOTTED";
	   }
	   else
	   {
		   return "Unknown";
	   }
   }
}
