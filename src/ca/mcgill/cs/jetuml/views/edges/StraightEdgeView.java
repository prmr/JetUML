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
import ca.mcgill.cs.jetuml.views.ArrowHead;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.ToolGraphics;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Path;

/**
 * Can draw an edge as a straight line between the connection
 * points of the start and end nodes that are closest to each
 * other. The LineStyle and end ArrowHead can be customized. The 
 * start ArrowHead is NONE. There is no label.
 */
public final class StraightEdgeView extends AbstractEdgeView
{	
	private final LineStyle aLineStyle;
	private final ArrowHead aArrowHead;
	
	/**
	 * Creates a new view with the required LineStyle and ArrowHead.
	 * 
	 * @param pEdge The edge to wrap.
	 * @param pLineStyle The line style for the edge.
	 * @param pArrowHead The arrow head for the end of the arrow. The start is always NONE.
	 */
	public StraightEdgeView(Edge pEdge, LineStyle pLineStyle, ArrowHead pArrowHead)
	{
		super(pEdge);
		aLineStyle = pLineStyle;
		aArrowHead = pArrowHead;
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		Path shape = (Path) getShape();
		ToolGraphics.strokeSharpPath(pGraphics, shape, aLineStyle);
		Line connectionPoints = getConnectionPoints();
		aArrowHead.view().draw(pGraphics, connectionPoints.getPoint1(), connectionPoints.getPoint2());
	}
}
