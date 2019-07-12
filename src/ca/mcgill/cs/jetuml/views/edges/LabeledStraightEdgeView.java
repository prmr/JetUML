/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018, 2019 by the contributors of the JetUML project.
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

import java.util.function.Supplier;

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.ArrowHead;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.StringViewer;
import javafx.scene.canvas.GraphicsContext;

/**
 * Can draw a straight edge with a label than can be obtained dynamically. 
 */
public class LabeledStraightEdgeView extends StraightEdgeView
{	
	private static final StringViewer STRING_VIEWER = new StringViewer(StringViewer.Align.CENTER, false, false);
	private static final int SHIFT = -10;
	
	private final Supplier<String> aLabelSupplier;
	
	/**
	 * Creates a new view with the required LineStyle and ArrowHead and label provider.
	 * 
	 * @param pEdge The edge to wrap.
	 * @param pLineStyle The line style for the edge.
	 * @param pArrowHead The arrow head for the end of the arrow. The start is always NONE.
	 * @param pLabelSupplier A supplier for the edge's label.
	 */
	public LabeledStraightEdgeView(Edge pEdge, LineStyle pLineStyle, ArrowHead pArrowHead,
			Supplier<String> pLabelSupplier)
	{
		super(pEdge, pLineStyle, pArrowHead);
		aLabelSupplier = pLabelSupplier;
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		super.draw(pGraphics);
		String label = aLabelSupplier.get();
		if( label.length() > 0 )
		{
			STRING_VIEWER.draw(label, pGraphics, getConnectionPoints().spanning().translated(0, SHIFT));
		}
	}
	
	private Rectangle getStringBounds()
	{
		String label = aLabelSupplier.get();
		assert label != null && label.length() > 0;
		Dimension dimensions = STRING_VIEWER.getDimension(label);
		Point center = getConnectionPoints().spanning().getCenter();
		return new Rectangle(center.getX()-dimensions.getWidth()/2, center.getY() + SHIFT, dimensions.getWidth(), dimensions.getHeight());
	}
	
	@Override
	public Rectangle getBounds()
	{
		Rectangle bounds = super.getBounds();
		String label = aLabelSupplier.get();
		if( label.length() > 0 )
		{
			bounds = bounds.add(getStringBounds());
		}
		return bounds;
	}
}