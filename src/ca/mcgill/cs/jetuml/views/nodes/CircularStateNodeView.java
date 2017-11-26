package ca.mcgill.cs.jetuml.views.nodes;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import ca.mcgill.cs.jetuml.framework.Grid;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.nodes.CircularStateNode;

/**
 * An object to render a CircularStateNode.
 * 
 * @author Martin P. Robillard
 *
 */
public class CircularStateNodeView extends AbstractNodeView
{
	private static final int DIAMETER = 20;
	private static final int DEFAULT_GAP = 3;   
	
	/**
	 * @param pNode The node to wrap.
	 */
	public CircularStateNodeView(CircularStateNode pNode)
	{
		super(pNode);
	}
	
	private boolean isFinal()
	{
		return ((CircularStateNode)node()).isFinal();
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		Ellipse2D circle = new Ellipse2D.Double(node().position().getX(), node().position().getY(), 
				DIAMETER, DIAMETER);
      
      	if(isFinal())
      	{
      		Ellipse2D inside = new Ellipse2D.Double( node().position().getX() + DEFAULT_GAP, 
      				node().position().getY() + DEFAULT_GAP, DIAMETER - 2 * DEFAULT_GAP, DIAMETER - 2 * DEFAULT_GAP);
      		pGraphics2D.fill(inside);
      		pGraphics2D.draw(circle);
      	}
		else
		{
			pGraphics2D.fill(circle);
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
			return new Point(cx + x / t, cy + y / t);
		}
		else
		{
			return new Point(cx, cy);
		}
	}   	 
	
	@Override
	public Shape getShape()
	{
		return new Ellipse2D.Double(getBounds().getX(), getBounds().getY(), DIAMETER - 1, DIAMETER - 1);
	}

	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(node().position().getX(), node().position().getY(), DIAMETER, DIAMETER);
	}
	
	@Override
	public void layout(Graph pGraph)
	{
		node().moveTo(Grid.snapped(getBounds()).getOrigin());
	}

	@Override
	public boolean contains(Point pPoint)
	{
		return getBounds().contains(pPoint);
	}
}
