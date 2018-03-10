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

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Graph2;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.views.Grid;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render a CircularStateNode.
 * 
 * @author Martin P. Robillard
 *
 */
public class CircularStateNodeView2 extends AbstractNodeView2
{
	private static final int DIAMETER = 20;
	private static final int DEFAULT_GAP = 3;   
	private final boolean aFinal;
	
	/**
	 * @param pNode The node to wrap.
	 * @param pFinal true if this is a final node, false if it's an initial node.
	 */
	public CircularStateNodeView2(Node pNode, boolean pFinal)
	{
		super(pNode);
		aFinal = pFinal;
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		super.draw(pGraphics);
		Ellipse2D circle = new Ellipse2D.Double(node().position().getX(), node().position().getY(), 
				DIAMETER, DIAMETER);
      
      	if(aFinal)
      	{
      		Ellipse2D inside = new Ellipse2D.Double( node().position().getX() + DEFAULT_GAP, 
      				node().position().getY() + DEFAULT_GAP, DIAMETER - 2 * DEFAULT_GAP, DIAMETER - 2 * DEFAULT_GAP);
      		pGraphics.fill(inside);
      		pGraphics.draw(circle);
      	}
		else
		{
			pGraphics.fill(circle);
		}      
	}
	
	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		Rectangle bounds = getBounds();
		double a = bounds.getWidth() / 2;
		double b = bounds.getHeight() / 2;
		double x = pDirection.getX();
		double y = pDirection.getY();
		double cx = bounds.getCenter().getX();
		double cy = bounds.getCenter().getY();
      
		if(a != 0 && b != 0 && !(x == 0 && y == 0))
		{
			double t = Math.sqrt((x * x) / (a * a) + (y * y) / (b * b));
			return new Point( (int) Math.round(cx + x / t), (int) Math.round(cy + y / t));
		}
		else
		{
			return new Point((int) Math.round(cx), (int) Math.round(cy));
		}
	}   	 
	
//	@Override
//	public Shape getShape()
//	{
//		return new Ellipse2D.Double(getBounds().getX(), getBounds().getY(), DIAMETER - 1, DIAMETER - 1);
//	}

	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(node().position().getX(), node().position().getY(), DIAMETER, DIAMETER);
	}
	
	@Override
	public void layout(Graph2 pGraph)
	{
		node().moveTo(Grid.snapped(getBounds()).getOrigin());
	}

	@Override
	public boolean contains(Point pPoint)
	{
		return getBounds().contains(pPoint);
	}

	@Override
	protected void fillShape(GraphicsContext pGraphics) 
	{
		// TODO Auto-generated method stub
		
	}
}
