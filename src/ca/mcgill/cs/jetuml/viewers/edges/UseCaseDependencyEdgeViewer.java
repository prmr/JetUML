/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseDependencyEdge;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.ArrowHead;
import ca.mcgill.cs.jetuml.viewers.LineStyle;
import ca.mcgill.cs.jetuml.viewers.StringViewer;
import ca.mcgill.cs.jetuml.viewers.StringViewer.Alignment;
import ca.mcgill.cs.jetuml.viewers.StringViewer.TextDecoration;
import javafx.scene.canvas.Canvas;

/**
 * A straight dotted line with a V end decoration and special icons 
 * to distinguish between dependency types.
 */
public final class UseCaseDependencyEdgeViewer extends LabeledStraightEdgeViewer
{	
	/**
	 * Creates a viewer for UseCaseDependencyEdge instances.
	 */
	public UseCaseDependencyEdgeViewer()
	{
		super(LineStyle.DOTTED, ArrowHead.V, edge -> ((UseCaseDependencyEdge)edge).getType().getLabel());
	}
	
	@Override
	public Canvas createIcon(Edge pEdge)
	{
		Canvas canvas = super.createIcon(pEdge);
		final float scale = 0.75f;
		canvas.getGraphicsContext2D().scale(scale, scale);
		StringViewer.get(Alignment.CENTER_CENTER, TextDecoration.PADDED).draw(getIconTag(pEdge), 
				canvas.getGraphicsContext2D(), new Rectangle(1, BUTTON_SIZE, 1, 1));
		return canvas;
	}

	private static String getIconTag(Edge pEdge)
	{
		return ((UseCaseDependencyEdge)pEdge).getType().getLabel().substring(1, 2).toUpperCase();
	}
}