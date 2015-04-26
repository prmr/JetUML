/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import ca.mcgill.cs.stg.jetuml.framework.Direction;
import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;

/**
 * An object node in a scenario diagram.
 */
public class ImplicitParameterNode extends ParentNode
{
	private static final int DEFAULT_TOP_HEIGHT = 60;
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 120;
	
	private double aTopHeight;
	private MultiLineString aName;

	/**
     * Construct an object node with a default size.
	 */
	public ImplicitParameterNode()
	{
		aName = new MultiLineString();
		aName.setUnderlined(true);
		setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
		aTopHeight = DEFAULT_TOP_HEIGHT;
	}

	@Override
	public boolean contains(Point2D pPoint)
	{
		Rectangle2D bounds = getBounds();
		return bounds.getX() <= pPoint.getX() && pPoint.getX() <= bounds.getX() + bounds.getWidth();
	}

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		Rectangle2D top = getTopRectangle();
		pGraphics2D.draw(top);
		aName.draw(pGraphics2D, top);
		double xmid = getBounds().getCenterX();
		Line2D line = new Line2D.Double(xmid, top.getMaxY(), xmid, getBounds().getMaxY());
		Stroke oldStroke = pGraphics2D.getStroke();
		// CSOFF:
		pGraphics2D.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[] { 5.0f, 5.0f }, 0.0f));
		// CSON:
		pGraphics2D.draw(line);
		pGraphics2D.setStroke(oldStroke);
	}

	/**
     * Returns the rectangle at the top of the object node.
     * @return the top rectangle
	 */
	public Rectangle2D getTopRectangle()
	{
		return new Rectangle2D.Double(getBounds().getX(), getBounds().getY(), getBounds().getWidth(), aTopHeight);
	}

	@Override
	public Shape getShape()
	{ return getTopRectangle(); }
   
	@Override
	public boolean addEdge(Edge pEdge, Point2D pPoint1, Point2D pPoint2)
	{
		return false;
	}

	@Override
	public Point2D getConnectionPoint(Direction pDirection)
	{
		if(pDirection.getX() > 0)
		{
			return new Point2D.Double(getBounds().getMaxX(), getBounds().getMinY() + aTopHeight / 2);
		}
		else
		{
			return new Point2D.Double(getBounds().getX(), getBounds().getMinY() + aTopHeight / 2);
		}
	}

	@Override
	public void layout(Graph pGraph, Graphics2D pGraphics2D, Grid pGrid)
	{
		Rectangle2D b = aName.getBounds(pGraphics2D); 
		b.add(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_TOP_HEIGHT));      
		Rectangle2D top = new Rectangle2D.Double(getBounds().getX(), getBounds().getY(), b.getWidth(), b.getHeight());
		pGrid.snap(top);
		setBounds(new Rectangle2D.Double(top.getX(), top.getY(), top.getWidth(), getBounds().getHeight()));
		aTopHeight = top.getHeight();
	}

	/**
     * Sets the name property value.
     * @param pName the name of this object
	 */
	public void setName(MultiLineString pName)
	{
		aName = pName;
	}

	/**
     * Gets the name property value.
     * @return the name of this object
	 */
	public MultiLineString getName()
	{
		return aName;
	}

	@Override
	public ImplicitParameterNode clone()
	{
		ImplicitParameterNode cloned = (ImplicitParameterNode) super.clone();
		cloned.aName = (MultiLineString) aName.clone();
		return cloned;
	}

	@Override
	public boolean addNode(Node pNode, Point2D pPoint)
	{
		return pNode instanceof CallNode || pNode instanceof PointNode;
	}
}
