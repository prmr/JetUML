/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.viewers.edges;

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge.Directionality;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.viewers.ArrowHead;
import ca.mcgill.cs.jetuml.viewers.LineStyle;
import javafx.scene.canvas.GraphicsContext;

/**
 * A straight dotted line.
 */
public final class DependencyEdgeViewer extends LabeledStraightEdgeViewer
{	
	/**
	 * Creates a viewer for DependencyEdge instances.
	 */
	public DependencyEdgeViewer()
	{
		super(LineStyle.DOTTED, ArrowHead.V, edge -> ((DependencyEdge)edge).getMiddleLabel());
	}
	
	@Override
	public void draw(Edge pEdge, GraphicsContext pGraphics)
	{
		super.draw(pEdge, pGraphics);
		if( ((DependencyEdge)pEdge).getDirectionality() == Directionality.Bidirectional )
		{
			Line connectionPoints = getConnectionPoints(pEdge);
			ArrowHead.V.view().draw(pGraphics, connectionPoints.getPoint2(), connectionPoints.getPoint1());
		}
	}
}