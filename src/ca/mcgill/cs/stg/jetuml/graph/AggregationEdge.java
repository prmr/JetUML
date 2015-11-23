/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 by the contributors of the JetUML project.
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

/**
 * @author Martin P. Robillard
 */

package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.BentStyle;

/**
 *  An edge that that represents a UML aggregation or 
 *  composition, with optional labels and directionality.
 */
public class AggregationEdge extends ClassRelationshipEdge2
{
	private boolean aDirected = false;
	private boolean aComposition = false;
	
	/**
	 * Creates an aggregation edge by specifying if it 
	 * can be a composition edge.
	 * 
	 * @param pComposition True if this represents a composition
	 */
	public AggregationEdge( boolean pComposition )
	{
		aComposition = pComposition;
	}
	
	/**
	 * Creates an aggregation edge.
	 */
	public AggregationEdge()
	{}
	
	public boolean isDirected()
	{
		return aDirected;
	}
	
	public void setDirected(boolean pDirected)
	{
		aDirected = pDirected;
	}
	
	public boolean isComposition()
	{
		return aComposition;
	}
	
	public void setComposition(boolean pComposition)
	{
		aComposition = pComposition;
	}
	
	@Override
	protected ArrowHead obtainEndArrowHead()
	{
		if( aDirected )
		{
			return ArrowHead.V;
		}
		else
		{
			return ArrowHead.NONE;
		}
	}
	
	@Override
	protected ArrowHead obtainStartArrowHead()
	{
		if( aComposition )
		{
			return ArrowHead.BLACK_DIAMOND;
		}
		else
		{
			return ArrowHead.DIAMOND;
		}
	}
	
	@Override
	public ArrayList<Point2D> getPoints()
	{
		return BentStyle.HVH.getPath(getStart().getBounds(), getEnd().getBounds());
   }
}
