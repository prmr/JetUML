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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.graph.Edge;

/**
 * A straight dotted line.
 * 
 * @author Martin P. Robillard
 */
public class NoteEdgeView extends AbstractEdgeView
{
	private static final Stroke DOTTED_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, 
			  BasicStroke.JOIN_ROUND, 0.0f, new float[] { 3.0f, 3.0f }, 0.0f);
		
	/**
	 * @param pEdge The edge to wrap.
	 */
	public NoteEdgeView(Edge pEdge)
	{
		super(pEdge);
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		Stroke oldStroke = pGraphics2D.getStroke();
		pGraphics2D.setStroke(DOTTED_STROKE);
		pGraphics2D.draw(getShape());
		pGraphics2D.setStroke(oldStroke);
	}
	
	
	@Override
	protected Shape getShape()
	{
		GeneralPath path = new GeneralPath();
		Line conn = getConnectionPoints();
		path.moveTo((float)conn.getX1(), (float)conn.getY1());
		path.lineTo((float)conn.getX2(), (float)conn.getY2());
		return path;
	}
}
