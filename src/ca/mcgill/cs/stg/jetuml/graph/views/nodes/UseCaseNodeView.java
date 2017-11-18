package ca.mcgill.cs.stg.jetuml.graph.views.nodes;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Direction;
import ca.mcgill.cs.stg.jetuml.geom.Point;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.nodes.UseCaseNode;

/**
 * An object to render a UseCaseNode.
 * 
 * @author Martin P. Robillard
 *
 */
public class UseCaseNodeView extends AbstractNodeView
{
	private static final int DEFAULT_WIDTH = 110;
	private static final int DEFAULT_HEIGHT = 40;
	
	private int aWidth = DEFAULT_WIDTH;
	private int aHeight = DEFAULT_HEIGHT;
	
	/**
	 * @param pNode The node to wrap.
	 */
	public UseCaseNodeView(UseCaseNode pNode)
	{
		super(pNode);
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);      
		pGraphics2D.draw(getShape());
		name().draw(pGraphics2D, getBounds());
	}
	
	@Override
	public Shape getShape()
	{
		return new Ellipse2D.Double(node().position().getX(), node().position().getY(), aWidth, aHeight);
	}
	
	private MultiLineString name()
	{
		return ((UseCaseNode)node()).getName();
	}
	
	/// ---
	
	@Override
	public boolean contains(Point pPoint)
	{
		return getBounds().contains(pPoint);
	}

	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(node().position().getX(), node().position().getY(), aWidth, aHeight);
	}

	@Override
	public void layout(Graph pGraph)
	{
		Rectangle snapped = Grid.snapped(getBounds());
		node().moveTo(snapped.getOrigin());
		aWidth = snapped.getWidth();
		aHeight = snapped.getHeight();
	}

	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		double slope = (double)aHeight / (double) aWidth;
		double ex = pDirection.getX();
		double ey = pDirection.getY();
		Rectangle bounds = getBounds();
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
}
