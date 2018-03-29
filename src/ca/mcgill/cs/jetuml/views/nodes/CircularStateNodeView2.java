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
import ca.mcgill.cs.jetuml.graph.Graph2;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.views.Grid2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

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
		Paint oldFill = pGraphics.getFill();
		double oldLineWidth = pGraphics.getLineWidth();
		pGraphics.setLineWidth(STROKE_WIDTH);
		pGraphics.setFill(Color.BLACK);
		if(aFinal)
		{
      		pGraphics.fillOval(node().position().getX() + DEFAULT_GAP, 
      				node().position().getY() + DEFAULT_GAP, DIAMETER - 2 * DEFAULT_GAP, DIAMETER - 2 * DEFAULT_GAP);
      		pGraphics.strokeOval(node().position().getX(), node().position().getY(), DIAMETER, DIAMETER);
      	}
		else
		{
			pGraphics.fillOval(node().position().getX(), node().position().getY(), DIAMETER, DIAMETER);
		}      
		pGraphics.setFill(oldFill);
		pGraphics.setLineWidth(oldLineWidth);
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

	@Override
	public void fillShape(GraphicsContext pGraphics, boolean pShadow)
	{
		if (pShadow) 
		{
			pGraphics.setFill(SHADOW_COLOR);
			pGraphics.fillOval(getBounds().getX(), getBounds().getY(), DIAMETER - 1, DIAMETER - 1);
		}
		else 
		{
			pGraphics.setFill(BACKGROUND_COLOR);
			pGraphics.fillOval(getBounds().getX(), getBounds().getY(), DIAMETER - 1, DIAMETER - 1);
			pGraphics.strokeOval(getBounds().getX(), getBounds().getY(), DIAMETER - 1, DIAMETER - 1);
		}	
	}

	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(node().position().getX(), node().position().getY(), DIAMETER, DIAMETER);
	}
	
	@Override
	public void layout(Graph2 pGraph)
	{
		node().moveTo(Grid2.snapped(getBounds()).getOrigin());
	}

	@Override
	public boolean contains(Point pPoint)
	{
		return getBounds().contains(pPoint);
	}
}
