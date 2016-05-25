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

package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;

import ca.mcgill.cs.stg.jetuml.framework.Direction;

/**
 *  An invisible node that is used in the toolbar to draw an
 *  edge, and in notes to serve as an end point of the node
 *  connector.
 */
public class PointNode extends AbstractNode
{
	private static final double EPSILON = 0.01;
	
	private Point2D aPoint;

	/**
     * Constructs a point node with coordinates (0, 0).
	 */
	public PointNode()
	{
		aPoint = new Point2D.Double();
	}

	@Override
	public void draw(Graphics2D pGraphics2D)
	{}

	@Override
	public void translate(double pDeltaX, double pDeltaY)
	{
      aPoint.setLocation(aPoint.getX() + pDeltaX, aPoint.getY() + pDeltaY);
	}

	@Override
	public boolean contains(Point2D pPoint)
	{
		final double threshold = 5;
		return aPoint.distance(pPoint) < threshold;
	}

	@Override
	public Rectangle2D getBounds()
	{
		return new Rectangle2D.Double(aPoint.getX(), aPoint.getY(), EPSILON, EPSILON);
	}

	@Override
	public Point2D getConnectionPoint(Direction pDirection)
	{
		return aPoint;
	}
	
	/**
	 * The persistence delegate recovers the position of the point.
	 * 
	 * @param pEncoder the encoder to which to add the delegate
	 */
	public static void setPersistenceDelegate(Encoder pEncoder)
	{
		pEncoder.setPersistenceDelegate(PointNode.class, new DefaultPersistenceDelegate()
		{
			protected void initialize(Class<?> pType, Object pOldInstance, Object pNewInstance, Encoder pOut) 
			{
				super.initialize(pType, pOldInstance, pNewInstance, pOut);
				Double x = ((PointNode)pOldInstance).aPoint.getX();
				Double y = ((PointNode)pOldInstance).aPoint.getY();
				pOut.writeStatement( new Statement(pOldInstance, "translate", new Object[]{ x, y }) );            
			}
		});
	}
}
