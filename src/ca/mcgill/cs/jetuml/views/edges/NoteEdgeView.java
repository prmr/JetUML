/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.views.edges;

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.ToolGraphics;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

/**
 * A straight dotted line.
 */
public final class NoteEdgeView extends AbstractEdgeView
{
	/**
	 * @param pEdge The edge to wrap.
	 */
	public NoteEdgeView(Edge pEdge)
	{
		super(pEdge);
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		ToolGraphics.strokeSharpPath(pGraphics, (Path) getShape(), LineStyle.DOTTED);
	}
	
	
	@Override
	protected Shape getShape()
	{
		Path path = new Path();
		Line conn = getConnectionPoints();
		MoveTo moveTo = new MoveTo((float)conn.getX1(), (float)conn.getY1());
		LineTo lineTo = new LineTo((float)conn.getX2(), (float)conn.getY2());
		path.getElements().addAll(moveTo, lineTo);
		return path;
	}
}
