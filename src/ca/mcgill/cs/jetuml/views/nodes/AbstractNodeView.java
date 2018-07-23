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

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.ToolGraphics;
import javafx.scene.canvas.GraphicsContext;

/**
 * Basic services for drawing nodes.
 */
public abstract class AbstractNodeView implements NodeView
{
	private Node aNode;
	
	/**
	 * @param pNode The node to wrap.
	 */
	protected AbstractNodeView(Node pNode)
	{
		aNode = pNode;
	}
	
	/**
	 * @return The wrapped node.
	 */
	protected Node node()
	{
		return aNode;
	}
	
	/* 
	 * The default behavior for containment is to return true if the point is
	 * within the bounding box of the node view.
	 * @see ca.mcgill.cs.jetuml.views.DiagramElementView#contains(ca.mcgill.cs.jetuml.geom.Point)
	 */
	@Override
	public boolean contains(Point pPoint)
	{
		return getBounds().contains(pPoint);
	}
	
	/* 
	 * The default behavior is to returns a point in the middle of the appropriate side of the bounding box 
	 * of the node.
	 * @see ca.mcgill.cs.jetuml.diagram.views.nodes.NodeView#getConnectionPoint(ca.mcgill.cs.jetuml.geom.Direction)
	 */
	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		final Rectangle bounds = getBounds();
		double slope = (double) bounds.getHeight() / (double) bounds.getWidth();
		double ex = pDirection.getX();
		double ey = pDirection.getY();
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
	public void drawSelectionHandles(GraphicsContext pGraphics)
	{
		ToolGraphics.drawHandles(pGraphics, getBounds());		
	}
}
