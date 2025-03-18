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
package org.jetuml.rendering.edges;

import java.util.function.Function;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Edge;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.geom.TextPosition;
import org.jetuml.rendering.ArrowHead;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.LineStyle;
import org.jetuml.rendering.RenderingContext;
import org.jetuml.rendering.StringRenderer;

/**
 * Can draw a straight edge with a label than can be obtained dynamically. 
 */
public class LabeledStraightEdgeRenderer extends StraightEdgeRenderer
{	
	private static final StringRenderer LABEL_RENDERER = new StringRenderer(TextPosition.TOP_CENTER);
	
	private final Function<Edge, String> aLabelExtractor;
	
	/**
	 * Creates a new view with the required LineStyle and ArrowHead and label provider.
	 * 
	 * @param pLineStyle The line style for the edge.
	 * @param pArrowHead The arrow head for the end of the arrow. The start is always NONE.
	 * @param pLabelExtractor A function to extract for the edge's label.
	 */
	public LabeledStraightEdgeRenderer(DiagramRenderer pParent, LineStyle pLineStyle, ArrowHead pArrowHead,
			Function<Edge, String> pLabelExtractor)
	{
		super(pParent, pLineStyle, pArrowHead);
		aLabelExtractor = pLabelExtractor;
	}
	
	@Override
	public void draw(DiagramElement pElement, RenderingContext pContext)
	{
		super.draw(pElement, pContext);
		Edge edge = (Edge) pElement;
		String label = wrapLabel(edge);
		
		if( label.length() > 0 )
		{
			LABEL_RENDERER.draw(label, getConnectionPoints(edge).spanning()
					.translated(0, -LABEL_RENDERER.getDimension(label).height()), pContext);
		}
	}
	
	private String wrapLabel(Edge pEdge) 
	{
		int distanceInX = Math.abs(parent().getBounds(pEdge.start()).center().x() -
				parent().getBounds(pEdge.end()).center().x());
		int distanceInY = Math.abs(parent().getBounds(pEdge.start()).center().y() -
				parent().getBounds(pEdge.end()).center().y());
		return super.wrapLabel(aLabelExtractor.apply(pEdge), distanceInX, distanceInY);
	}

	private Rectangle getStringBounds(Edge pEdge)
	{
		String label = wrapLabel(pEdge);
		assert label != null && label.length() > 0;
		Point center = getConnectionPoints(pEdge).spanning().center();
		Dimension textDimension = LABEL_RENDERER.getDimension(label);
		return new Rectangle(center.x() - textDimension.width()/2, center.y() - textDimension.height()/2, textDimension.width(), 
				textDimension.height());
	}
	
	@Override
	public Rectangle getBounds(DiagramElement pElement)
	{
		Rectangle bounds = super.getBounds(pElement);
		Edge edge = (Edge) pElement;
		String label = aLabelExtractor.apply(edge);
		if( label.length() > 0 )
		{
			bounds = bounds.add(getStringBounds(edge));
		}
		return bounds;
	}
}