/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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

package ca.mcgill.cs.stg.jetuml.graph.nodes;

import java.awt.Shape;

import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.geom.Conversions;
import ca.mcgill.cs.stg.jetuml.geom.Direction;
import ca.mcgill.cs.stg.jetuml.geom.Point;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.Graph;

/**
 * A node that has a rectangular shape.
 */
public abstract class RectangularNode extends AbstractNode
{
	private Rectangle aBounds;

	@Override
	public void translate(int pDeltaX, int pDeltaY)
	{
		aBounds = aBounds.translated(pDeltaX, pDeltaY); 
	}

	@Override
	public boolean contains(Point pPoint)
	{
		return aBounds.contains(pPoint);
	}

	@Override
	public Rectangle getBounds()
	{
		return aBounds;
	}

	/**
	 * @param pNewBounds The new bounds for this node.
	 */
	public void setBounds(Rectangle pNewBounds)
	{
		aBounds = pNewBounds;
	}

	@Override
	public void layout(Graph pGraph)
	{
		aBounds = Grid.snapped(aBounds);
	}

	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		double slope = (double)aBounds.getHeight() / (double) aBounds.getWidth();
		double ex = pDirection.getX();
		double ey = pDirection.getY();
		int x = aBounds.getCenter().getX();
		int y = aBounds.getCenter().getY();
      
		if(ex != 0 && -slope <= ey / ex && ey / ex <= slope)
		{  
			// intersects at left or right boundary
			if(ex > 0) 
			{
				x = aBounds.getMaxX();
				y += (aBounds.getWidth() / 2) * ey / ex;
			}
			else
			{
				x = aBounds.getX();
				y -= (aBounds.getWidth() / 2) * ey / ex;
			}
		}
		else if(ey != 0)
		{  
			// intersects at top or bottom
			if(ey > 0) 
			{
				x += (aBounds.getHeight() / 2) * ex / ey;
				y = aBounds.getMaxY();
			}
			else
			{
				x -= (aBounds.getHeight() / 2) * ex / ey;
				y = aBounds.getY();
			}
		}
		return new Point((int)Math.round(x), (int)Math.round(y));
	}

	@Override
	public Shape getShape()
	{
		return Conversions.toRectangle2D(aBounds);
	}
}
