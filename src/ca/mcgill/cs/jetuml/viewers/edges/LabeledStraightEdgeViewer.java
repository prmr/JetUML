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

import java.util.function.Function;

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.ArrowHead;
import ca.mcgill.cs.jetuml.viewers.LineStyle;
import ca.mcgill.cs.jetuml.viewers.StringViewer;
import ca.mcgill.cs.jetuml.viewers.StringViewer.Alignment;
import ca.mcgill.cs.jetuml.viewers.StringViewer.TextDecoration;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;
import javafx.scene.canvas.GraphicsContext;

/**
 * Can draw a straight edge with a label than can be obtained dynamically. 
 */
public class LabeledStraightEdgeViewer extends StraightEdgeViewer
{	
	private static final StringViewer STRING_VIEWER = StringViewer.get(Alignment.CENTER_CENTER, TextDecoration.PADDED);
	
	private final Function<Edge, String> aLabelExtractor;
	
	/**
	 * Creates a new view with the required LineStyle and ArrowHead and label provider.
	 * 
	 * @param pLineStyle The line style for the edge.
	 * @param pArrowHead The arrow head for the end of the arrow. The start is always NONE.
	 * @param pLabelExtractor A function to extract for the edge's label.
	 */
	public LabeledStraightEdgeViewer(LineStyle pLineStyle, ArrowHead pArrowHead,
			Function<Edge, String> pLabelExtractor)
	{
		super(pLineStyle, pArrowHead);
		aLabelExtractor = pLabelExtractor;
	}
	
	@Override
	public void draw(Edge pEdge, GraphicsContext pGraphics)
	{
		super.draw(pEdge, pGraphics);
		String label = wrapLabel(pEdge);
		int labelHeight = STRING_VIEWER.getDimension(label).height();
		if( label.length() > 0 )
		{
			STRING_VIEWER.draw(label, pGraphics, getConnectionPoints(pEdge).spanning().translated(0, -labelHeight/2));
		}
	}
	
	private String wrapLabel(Edge pEdge) 
	{
		int distanceInX = Math.abs(NodeViewerRegistry.getBounds(pEdge.getStart()).getCenter().getX() -
				NodeViewerRegistry.getBounds(pEdge.getEnd()).getCenter().getX());
		int distanceInY = Math.abs(NodeViewerRegistry.getBounds(pEdge.getStart()).getCenter().getY() -
				NodeViewerRegistry.getBounds(pEdge.getEnd()).getCenter().getY());
		return super.wrapLabel(aLabelExtractor.apply(pEdge), distanceInX, distanceInY);
	}

	private Rectangle getStringBounds(Edge pEdge)
	{
		String label = wrapLabel(pEdge);
		assert label != null && label.length() > 0;
		Dimension dimensions = STRING_VIEWER.getDimension(label);
		Point center = getConnectionPoints(pEdge).spanning().getCenter();
		return new Rectangle(center.getX()-dimensions.width()/2, center.getY() - dimensions.height()/2, dimensions.width(), 
				dimensions.height());
	}
	
	@Override
	public Rectangle getBounds(Edge pEdge)
	{
		Rectangle bounds = super.getBounds(pEdge);
		String label = aLabelExtractor.apply(pEdge);
		if( label.length() > 0 )
		{
			bounds = bounds.add(getStringBounds(pEdge));
		}
		return bounds;
	}
}