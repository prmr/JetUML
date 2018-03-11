/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2017 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.graph.edges;

import ca.mcgill.cs.jetuml.views.ArrowHead;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.edges.EdgeView;
import ca.mcgill.cs.jetuml.views.edges.EdgeView2;
import ca.mcgill.cs.jetuml.views.edges.SegmentationStyle;
import ca.mcgill.cs.jetuml.views.edges.SegmentationStyle2;
import ca.mcgill.cs.jetuml.views.edges.SegmentationStyleFactory;
import ca.mcgill.cs.jetuml.views.edges.SegmentationStyleFactory2;
import ca.mcgill.cs.jetuml.views.edges.SegmentedEdgeView;
import ca.mcgill.cs.jetuml.views.edges.SegmentedEdgeView2;

/**
 *  An edge that that represents a dynamic collaboration between
 *  two objects in an object diagram, with optional labels.
 */
public class ObjectCollaborationEdge extends ClassRelationshipEdge
{
	@Override
	protected EdgeView generateView()
	{
		return new SegmentedEdgeView(this, obtainSegmentationStyle(),
				() -> LineStyle.SOLID, () -> ArrowHead.NONE, () -> ArrowHead.NONE,
				() -> getStartLabel(), () -> getMiddleLabel(), () -> getEndLabel());
	}
	
	@Override
	protected EdgeView2 generateView2()
	{
		return new SegmentedEdgeView2(this, obtainSegmentationStyle2(),
				() -> LineStyle.SOLID, () -> ArrowHead.NONE, () -> ArrowHead.NONE,
				() -> getStartLabel(), () -> getMiddleLabel(), () -> getEndLabel());
	}
	
	@Override
	public SegmentationStyle obtainSegmentationStyle()
	{
		return SegmentationStyleFactory.createStraightStrategy();
	}

	@Override
	public SegmentationStyle2 obtainSegmentationStyle2() 
	{
		return SegmentationStyleFactory2.createStraightStrategy();
	}


}
