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

import static ca.mcgill.cs.jetuml.geom.Util.max;

import java.util.List;

import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.StringViewer;
import ca.mcgill.cs.jetuml.views.ViewUtils;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render an implicit parameter in a Sequence diagram.
 */
public final class ImplicitParameterNodeView extends AbstractNodeView
{
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 120;
	private static final int HORIZONTAL_PADDING = 10; // 2x the left and right padding around the name of the implicit parameter
	private static final int TAIL_HEIGHT = 20; // Piece of the life line below the last call node
	private static final int TOP_HEIGHT = 60;
	private static final StringViewer NAME_VIEWER = new StringViewer(StringViewer.Align.CENTER, false, true);

	/**
	 * @param pNode The node to wrap.
	 */
	public ImplicitParameterNodeView(ImplicitParameterNode pNode)
	{
		super(pNode);
	}
	
	private String name()
	{
		return ((ImplicitParameterNode)node()).getName();
	}
	
	private List<ChildNode> children()
	{
		return ((ImplicitParameterNode)node()).getChildren();
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		Rectangle top = getTopRectangle();
		ViewUtils.drawRectangle(pGraphics, top);
		NAME_VIEWER.draw(name(), pGraphics, top);
		int xmid = top.getCenter().getX();
		ViewUtils.drawLine(pGraphics, xmid,  top.getMaxY(), xmid, getBounds().getMaxY(), LineStyle.DOTTED);
	}
	
	@Override
	public boolean contains(Point pPoint)
	{
		final Rectangle bounds = getBounds();
		return bounds.getX() <= pPoint.getX() && pPoint.getX() <= bounds.getX() + bounds.getWidth();
	}

	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		Rectangle bounds = getBounds();
		if(pDirection.getX() > 0)
		{
			return new Point(bounds.getMaxX(), bounds.getY() + TOP_HEIGHT / 2);
		}
		else
		{
			return new Point(bounds.getX(), bounds.getY() + TOP_HEIGHT / 2);
		}
	}
	
	private Point getMaxXYofChildren()
	{
		int maxY = 0;
		int maxX = 0;
		for( ChildNode child : children() )
		{
			Rectangle bounds = child.view().getBounds();
			maxX = Math.max(maxX,  bounds.getMaxX());
			maxY = Math.max(maxY, bounds.getMaxY());
		}
		return new Point(maxX, maxY);
	}
	
	/**
     * Returns the rectangle at the top of the object node.
     * @return the top rectangle
	 */
	public Rectangle getTopRectangle()
	{
		int width = Math.max(NAME_VIEWER.getBounds(name()).getWidth()+ HORIZONTAL_PADDING, DEFAULT_WIDTH); 
		return new Rectangle(node().position().getX(), 0, width, TOP_HEIGHT);
	}

	@Override
	public Rectangle getBounds()
	{
		Rectangle topRectangle = getTopRectangle();
		Point childrenMaxXY = getMaxXYofChildren();
		int width = max(topRectangle.getWidth(), DEFAULT_WIDTH, childrenMaxXY.getX() - node().position().getX());
		int height = max(DEFAULT_HEIGHT, childrenMaxXY.getY() + TAIL_HEIGHT);
		return new Rectangle(node().position().getX(), 0, width, height);
	}
}
