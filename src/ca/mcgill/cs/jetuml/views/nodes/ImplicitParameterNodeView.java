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
package ca.mcgill.cs.jetuml.views.nodes;

import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Diagram;
import ca.mcgill.cs.jetuml.graph.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.views.Grid;
import ca.mcgill.cs.jetuml.views.StringViewer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * An object to render an implicit parameter in a Sequence diagram.
 */
public class ImplicitParameterNodeView extends RectangleBoundedNodeView
{
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 120;
	private static final int DEFAULT_TOP_HEIGHT = 60;
	private static final StrokeLineCap LINE_CAP = StrokeLineCap.ROUND;
	private static final StrokeLineJoin LINE_JOIN = StrokeLineJoin.ROUND;
	private static final double[] DASHES = new double[] {5, 5};
	private static final StringViewer NAME_VIEWER = new StringViewer(StringViewer.Align.CENTER, false, true);

	private int aTopHeight = DEFAULT_TOP_HEIGHT;
	
	/**
	 * @param pNode The node to wrap.
	 */
	public ImplicitParameterNodeView(ImplicitParameterNode pNode)
	{
		super(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	private String name()
	{
		return ((ImplicitParameterNode)node()).getName();
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		super.draw(pGraphics);
		Rectangle top = getTopRectangle();
		NAME_VIEWER.draw(name(), pGraphics, top);
		int xmid = getBounds().getCenter().getX();
		StrokeLineCap oldLineCap = pGraphics.getLineCap();
		StrokeLineJoin oldLineJoin = pGraphics.getLineJoin();
		double[] oldDashes = pGraphics.getLineDashes();
		pGraphics.setLineCap(LINE_CAP);
		pGraphics.setLineJoin(LINE_JOIN);
		pGraphics.setLineDashes(DASHES);
		pGraphics.strokeLine(xmid, top.getMaxY(), xmid, getBounds().getMaxY());
		pGraphics.setLineCap(oldLineCap);
		pGraphics.setLineJoin(oldLineJoin);
		pGraphics.setLineDashes(oldDashes);
	}
	
	@Override
	public boolean contains(Point pPoint)
	{
		final Rectangle bounds = getBounds();
		return bounds.getX() <= pPoint.getX() && pPoint.getX() <= bounds.getX() + bounds.getWidth();
	}

	@Override
	public void fillShape(GraphicsContext pGraphics, boolean pShadow)
	{
		Rectangle top = getTopRectangle();
		if (pShadow) 
		{
			pGraphics.setFill(SHADOW_COLOR);
			pGraphics.fillRect(top.getX(), top.getY(), top.getWidth(), top.getHeight());
		}
		else 
		{
			pGraphics.setFill(BACKGROUND_COLOR);
			pGraphics.fillRect(top.getX(), top.getY(), top.getWidth(), top.getHeight());
			pGraphics.strokeRect(top.getX(), top.getY(), top.getWidth(), top.getHeight());
		}	
	}
   
	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		if(pDirection.getX() > 0)
		{
			return new Point(getBounds().getMaxX(), getBounds().getY() + aTopHeight / 2);
		}
		else
		{
			return new Point(getBounds().getX(), getBounds().getY() + aTopHeight / 2);
		}
	}
	
	@Override
	public void setBounds(Rectangle pNewBounds)
	{
		super.setBounds(pNewBounds);
	}

	@Override
	public void layout(Diagram pGraph)
	{
		Rectangle bounds = NAME_VIEWER.getBounds(name()); 
		bounds = bounds.add(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_TOP_HEIGHT));      
		Rectangle top = new Rectangle(getBounds().getX(), getBounds().getY(), bounds.getWidth(), bounds.getHeight());
		Rectangle snappedTop = Grid.snapped(top);
		setBounds(new Rectangle(snappedTop.getX(), snappedTop.getY(), snappedTop.getWidth(), getBounds().getHeight()));
		aTopHeight = top.getHeight();
	}
	
	/**
     * Returns the rectangle at the top of the object node.
     * @return the top rectangle
	 */
	public Rectangle getTopRectangle()
	{
		return new Rectangle(getBounds().getX(), getBounds().getY(), getBounds().getWidth(), aTopHeight);
	}
}
