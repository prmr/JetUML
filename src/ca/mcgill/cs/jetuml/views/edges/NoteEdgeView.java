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

import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.graph.Edge;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * A straight dotted line.
 * 
 * @author Martin P. Robillard
 */
public class NoteEdgeView extends AbstractEdgeView
{
	private static final StrokeLineCap LINE_CAP = StrokeLineCap.ROUND;
	private static final StrokeLineJoin LINE_JOIN = StrokeLineJoin.ROUND;
	private static final double MITER_LIMIT = 0;
	private static final double[] DASHES = new double[] {3, 3};
	
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
		StrokeLineCap oldCap = pGraphics.getLineCap();
		StrokeLineJoin oldJoin = pGraphics.getLineJoin();
		double oldMiter = pGraphics.getMiterLimit();
		double[] oldDashes = pGraphics.getLineDashes();
		
		pGraphics.beginPath();
		
		pGraphics.setLineCap(LINE_CAP);
		pGraphics.setLineJoin(LINE_JOIN);
		pGraphics.setMiterLimit(MITER_LIMIT);
		pGraphics.setLineDashes(DASHES);
		
		completeDrawPath(pGraphics, (Path) getShape());
		
		pGraphics.setLineCap(oldCap);
		pGraphics.setLineJoin(oldJoin);
		pGraphics.setMiterLimit(oldMiter);
		pGraphics.setLineDashes(oldDashes);
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
