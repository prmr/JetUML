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
