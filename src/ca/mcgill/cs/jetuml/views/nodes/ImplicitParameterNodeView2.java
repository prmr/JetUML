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

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import ca.mcgill.cs.jetuml.geom.Conversions;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Graph2;
import ca.mcgill.cs.jetuml.graph.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.views.Grid;
import ca.mcgill.cs.jetuml.views.StringViewer;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render an implicit parameter in a Sequence diagram.
 * 
 * @author Martin P. Robillard
 *
 */
public class ImplicitParameterNodeView2 extends RectangleBoundedNodeView2
{
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 120;
	private static final int DEFAULT_TOP_HEIGHT = 60;
	private static final Stroke STROKE = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[] { 5, 5 }, 0);
	private static final StringViewer NAME_VIEWER = new StringViewer(StringViewer.Align.CENTER, false, true);

	private int aTopHeight = DEFAULT_TOP_HEIGHT;
	
	/**
	 * @param pNode The node to wrap.
	 */
	public ImplicitParameterNodeView2(ImplicitParameterNode pNode)
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
		pGraphics.draw(Conversions.toRectangle2D(top));
		NAME_VIEWER.draw(name(), pGraphics, top);
		int xmid = getBounds().getCenter().getX();
		Stroke oldStroke = pGraphics.getStroke();
		pGraphics.setStroke(STROKE);
		pGraphics.draw(new Line2D.Double(xmid, top.getMaxY(), xmid, getBounds().getMaxY()));
		pGraphics.setStroke(oldStroke);
	}
	
	@Override
	public boolean contains(Point pPoint)
	{
		final Rectangle bounds = getBounds();
		return bounds.getX() <= pPoint.getX() && pPoint.getX() <= bounds.getX() + bounds.getWidth();
	}

//	@Override
//	public Shape getShape()
//	{ return Conversions.toRectangle2D(getTopRectangle()); }
   
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
	public void layout(Graph2 pGraph)
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
