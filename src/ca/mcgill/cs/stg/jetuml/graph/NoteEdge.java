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

package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import ca.mcgill.cs.stg.jetuml.framework.Direction;

/**
 *  A dotted line that connects a note to its attachment.
 */
public class NoteEdge extends AbstractEdge
{
	private static final int DEGREES_180 = 180;
	private static final Stroke DOTTED_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, 
													  BasicStroke.JOIN_ROUND, 0.0f, new float[] { 3.0f, 3.0f }, 0.0f);
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		Stroke oldStroke = pGraphics2D.getStroke();
		pGraphics2D.setStroke(DOTTED_STROKE);
		pGraphics2D.draw(getConnectionPoints());
		pGraphics2D.setStroke(oldStroke);
	}

	@Override
	public Line2D getConnectionPoints()
	{
		Rectangle2D start = getStart().getBounds();
		Rectangle2D end = getEnd().getBounds();
		Direction d = new Direction(end.getCenterX() - start.getCenterX(), end.getCenterY() - start.getCenterY());
		return new Line2D.Double(getStart().getConnectionPoint(d), getEnd().getConnectionPoint(d.turn(DEGREES_180)));
  	}

	@Override
	protected Shape getShape()
	{
		GeneralPath path = new GeneralPath();
		Line2D conn = getConnectionPoints();
		path.moveTo((float)conn.getX1(), (float)conn.getY1());
		path.lineTo((float)conn.getX2(), (float)conn.getY2());
		return path;
	}
}
