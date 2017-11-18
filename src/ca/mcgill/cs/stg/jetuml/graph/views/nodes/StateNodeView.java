package ca.mcgill.cs.stg.jetuml.graph.views.nodes;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Direction;
import ca.mcgill.cs.stg.jetuml.geom.Point;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.nodes.StateNode;

/**
 * An object to render a StateNode.
 * 
 * @author Martin P. Robillard
 *
 */
public class StateNodeView extends AbstractNodeView
{
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 60;
	private static final int ARC_SIZE = 20;
	
	private int aWidth = DEFAULT_WIDTH;
	private int aHeight = DEFAULT_HEIGHT;
	
	/**
	 * @param pNode The node to wrap.
	 */
	public StateNodeView(StateNode pNode)
	{
		super(pNode);
	}
	
	private MultiLineString name()
	{
		return ((StateNode)node()).getName();
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		pGraphics2D.draw(getShape());
		name().draw(pGraphics2D, getBounds());
	}
	
	@Override
	protected Shape getShape()
	{       
		return new RoundRectangle2D.Double(getBounds().getX(), getBounds().getY(), 
				getBounds().getWidth(), getBounds().getHeight(), ARC_SIZE, ARC_SIZE);
	}
	
	@Override	
	public void layout(Graph pGraph)
	{
		Rectangle b = name().getBounds();
		b = new Rectangle(getBounds().getX(), getBounds().getY(), 
				Math.max(b.getWidth(), DEFAULT_WIDTH), Math.max(b.getHeight(), DEFAULT_HEIGHT));
		setBounds(Grid.snapped(b));
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
	public void setBounds(Rectangle pNewBounds)
	{
		node().moveTo(pNewBounds.getOrigin());
		aWidth = pNewBounds.getWidth();
		aHeight = pNewBounds.getHeight();
	}

	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		final Rectangle bounds = getBounds();
		double slope = (double)bounds.getHeight() / (double) bounds.getWidth();
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
}
