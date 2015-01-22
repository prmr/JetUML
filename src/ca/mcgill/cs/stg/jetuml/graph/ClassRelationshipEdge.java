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

package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import ca.mcgill.cs.stg.jetuml.BentStyle;

/**
 *  An edge that is shaped like a line with up to 
 *  three segments with an arrowhead.
 */
public class ClassRelationshipEdge extends SegmentedLineEdge
{
	private BentStyle aBentStyle;
	
	/**
     *  Constructs a straight edge.
     */
	public ClassRelationshipEdge()
	{
		aBentStyle = BentStyle.STRAIGHT;
	}

	/**
     *  Sets the bentStyle property.
     * @param pNewValue the bent style
     */
	public void setBentStyle(BentStyle pNewValue)
	{ aBentStyle = pNewValue; }
   
	/**
     * Gets the bentStyle property.
     * @return the bent style
     */
	public BentStyle getBentStyle() 
	{ return aBentStyle; }
   
	@Override
	public ArrayList<Point2D> getPoints()
	{
		return aBentStyle.getPath(getStart().getBounds(), getEnd().getBounds());
   }
}
