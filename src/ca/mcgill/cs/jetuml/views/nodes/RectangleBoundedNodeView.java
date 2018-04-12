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
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.views.Grid;
import javafx.scene.canvas.GraphicsContext;

/**
 * A view for nodes that are bounded by a rectangle.
 * 
 * @author Martin P. Robillard
 * @author Kaylee I. Kutschera - Migration to JavaFX
 */
public abstract class RectangleBoundedNodeView extends AbstractNodeView
{
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 60;
	
	private int aWidth = DEFAULT_WIDTH;
	private int aHeight = DEFAULT_HEIGHT;
	
	/**
	 * @param pNode The node to wrap.
	 * @param pMinWidth The minimum width for the node.
	 * @param pMinHeight The minimum height for the node.
	 */
	protected RectangleBoundedNodeView(Node pNode, int pMinWidth, int pMinHeight)
	{
		super(pNode);
		aWidth = pMinWidth;
		aHeight = pMinHeight;
	}
	
	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(node().position().getX(), node().position().getY(), aWidth, aHeight);
	}
	
	@Override
	public boolean contains(Point pPoint)
	{
		return getBounds().contains(pPoint);
	}

	/**
	 * @param pNewBounds The new bounds for this node.
	 */
	protected void setBounds(Rectangle pNewBounds)
	{
		node().moveTo(pNewBounds.getOrigin());
		aWidth = pNewBounds.getWidth();
		aHeight = pNewBounds.getHeight();
	}
	
	@Override
	protected void fillShape(GraphicsContext pGraphics, boolean pShadow) 
	{
		Rectangle bounds = getBounds();
		if (pShadow) 
		{
			pGraphics.setFill(SHADOW_COLOR);
			pGraphics.fillRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		}
		else 
		{
			pGraphics.setFill(BACKGROUND_COLOR);
			pGraphics.fillRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
			pGraphics.strokeRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		}	
	}

	/* 
	 * Returns a point in the middle of the appropriate side of the node.
	 * @see ca.mcgill.cs.jetuml.graph.views.nodes.NodeView#getConnectionPoint(ca.mcgill.cs.jetuml.geom.Direction)
	 */
	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		double slope = (double) aHeight / (double) aWidth;
		double ex = pDirection.getX();
		double ey = pDirection.getY();
		final Rectangle bounds = getBounds();
		int x = bounds.getCenter().getX();
		int y = bounds.getCenter().getY();
      
		if(ex != 0 && -slope <= ey / ex && ey / ex <= slope)
		{  
			// intersects at left or right boundary
			if(ex > 0) 
			{
				x = bounds.getMaxX();
				y += (bounds.getWidth() / 2) * ey / ex;
			}
			else
			{
				x = bounds.getX();
				y -= (bounds.getWidth() / 2) * ey / ex;
			}
		}
		else if(ey != 0)
		{  
			// intersects at top or bottom
			if(ey > 0) 
			{
				x += (bounds.getHeight() / 2) * ex / ey;
				y = bounds.getMaxY();
			}
			else
			{
				x -= (bounds.getHeight() / 2) * ex / ey;
				y = bounds.getY();
			}
		}
		return new Point(x, y);
	}
	
	@Override
	public void layout(Graph pGraph)
	{
		Rectangle snapped = Grid.snapped(getBounds());
		node().moveTo(snapped.getOrigin());
		aWidth = snapped.getWidth();
		aHeight = snapped.getHeight();
	}
}
