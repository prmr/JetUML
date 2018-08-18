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
 *  An edge that that represents a UML association, with optional 
 *  labels and directionality.
 */
public final class AssociationEdge extends ClassRelationshipEdge
{
	/**
	 * Possible directionalities for an association.
	 */
	public enum Directionality 
	{None, Start, End, Both}
	
	private Directionality aDirectionality = Directionality.None;
	
	@Override
	protected EdgeView generateView()
	{
		return new SegmentedEdgeView(this, SegmentationStyleFactory.createHVHStrategy(),
				() -> LineStyle.SOLID, () -> getStartArrowHead(), () -> getEndArrowHead(),
				() -> getStartLabel(), () -> getMiddleLabel(), () -> getEndLabel());
	}
	
	/**
	 * @param pDirectionality The desired directionality.
	 */
	public void setDirectionality( Directionality pDirectionality )
	{
		aDirectionality = pDirectionality;
	}
	
	/**
	 * @return The directionality of this association.
	 */
	public Directionality getDirectionality()
	{
		return aDirectionality;
	}
	
	@Override
	protected void buildProperties()
	{
		super.buildProperties();
		properties().add("directionality", () -> aDirectionality, 
				pDirectionality -> aDirectionality = Directionality.valueOf((String)pDirectionality ));
	}
	
	private ArrowHead getStartArrowHead()
	{
		if( aDirectionality == Directionality.Both || aDirectionality == Directionality.Start )
		{
			return ArrowHead.V;
		}
		else
		{
			return ArrowHead.NONE;
		}
	}
	
	private ArrowHead getEndArrowHead()
	{
		if( aDirectionality == Directionality.Both || aDirectionality == Directionality.End )
		{
			return ArrowHead.V;
		}
		else
		{
			return ArrowHead.NONE;
		}
	}
	
	@Override
	public SegmentationStyle obtainSegmentationStyle()
	{
		return SegmentationStyleFactory.createHVHStrategy();
	}
}
