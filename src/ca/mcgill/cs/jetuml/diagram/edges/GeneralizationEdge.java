/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.diagram.edges;

import ca.mcgill.cs.jetuml.views.ArrowHead;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.edges.EdgeView;
import ca.mcgill.cs.jetuml.views.edges.SegmentationStyle;
import ca.mcgill.cs.jetuml.views.edges.SegmentationStyleFactory;
import ca.mcgill.cs.jetuml.views.edges.SegmentedEdgeView;

/**
 *  An edge that that represents a UML generalization (inheritance
 *  or implementation), with optional labels.
 */
public final class GeneralizationEdge extends ClassRelationshipEdge
{
	/**
	 * Type of UML generalization relation.
	 */
	public enum Type 
	{Inheritance, Implementation}
	
	private Type aType = Type.Inheritance;
	
	/**
	 * Creates a generalization edge by specifying its type.
	 * 
	 * @param pType the type of generalization
	 */
	public GeneralizationEdge( Type pType )
	{
		aType = pType;
	}
	
	/**
	 * Creates an inheritance edge.
	 */
	public GeneralizationEdge()
	{}
	
	@Override
	protected EdgeView generateView()
	{
		return new SegmentedEdgeView(this, obtainSegmentationStyle(),
				() -> getLineStyle(), () -> ArrowHead.NONE, () -> ArrowHead.TRIANGLE,
				() -> getStartLabel(), () -> getMiddleLabel(), () -> getEndLabel());
	}
	
	/**
	 * @return The type of generalization.
	 */
	public Type getType()
	{
		return aType;
	}
	
	/**
	 * Sets the type of generalization.
	 * @param pType The desired type of generalization
	 */
	public void setType(Type pType)
	{
		aType = pType;
	}
	
	@Override
	protected void buildProperties()
	{
		super.buildProperties();
		properties().add("Generalization Type", () -> aType, pType -> aType = Type.valueOf((String) pType));
	}
	
	private LineStyle getLineStyle()
	{
		if( aType == Type.Implementation )
		{
			return LineStyle.DOTTED;
		}
		else
		{
			return LineStyle.SOLID;
		}
	}
	
	@Override
	public SegmentationStyle obtainSegmentationStyle()
	{
		return SegmentationStyleFactory.createVHVStrategy();
	}
}
