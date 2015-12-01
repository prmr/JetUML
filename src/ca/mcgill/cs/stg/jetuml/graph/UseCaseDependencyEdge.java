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

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.LineStyle;
import ca.mcgill.cs.stg.jetuml.framework.SegmentationStyleFactory;

/**
 *  An edge that that represents a UML dependency
 *  between use cases.
 */
public class UseCaseDependencyEdge extends SegmentedLabeledEdge
{
	private static final String LABEL_INCLUDE = "\u00ABinclude\u00BB";
	private static final String LABEL_EXTEND = "\u00ABextend\u00BB";
	
	/**
	 * Type of use case dependency.
	 */
	public enum Type 
	{None, Include, Extend}
	
	private Type aType = Type.None;
	
	/**
	 * Creates a default (un-typed) dependency.
	 */
	public UseCaseDependencyEdge()
	{}
	
	/**
	 * Creates a typed dependency.
	 * @param pType The type of dependency.
	 */
	public UseCaseDependencyEdge(Type pType)
	{
		aType = pType;
	}
	
	/**
	 * @return The type of dependency relation.
	 */
	public Type getType()
	{
		return aType;
	}
	
	/**
	 * @param pType The type of dependency relation.
	 */
	public void setType(Type pType)
	{
		aType = pType;
	}
	
	@Override
	protected ArrowHead obtainEndArrowHead()
	{
		return ArrowHead.V;
	}
	
	@Override
	protected LineStyle obtainLineStyle()
	{
		return LineStyle.DOTTED;
	}
	
	@Override
	protected String obtainMiddleLabel()
	{
		if( aType == Type.Include )
		{
			return LABEL_INCLUDE;
		}
		else if( aType == Type.Extend )
		{
			return LABEL_EXTEND;
		}
		else
		{
			return "";
		}
	}
	
	@Override
	protected Point2D[] getPoints()
	{
		return SegmentationStyleFactory.createStraightStyle().getPath(getStart(), getEnd());
	}
}
