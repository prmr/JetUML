package ca.mcgill.cs.stg.jetuml.views.nodes;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import ca.mcgill.cs.stg.jetuml.geom.Direction;
import ca.mcgill.cs.stg.jetuml.geom.Point;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.Node;

/**
 * An object to render a PointNode.
 * 
 * @author Martin P. Robillard
 *
 */
public class PointNodeView extends AbstractNodeView
{
	private static final int SELECTION_DISTANCE = 5;
	
	/**
	 * @param pNode The node to wrap.
	 */
	public PointNodeView(Node pNode)
	{
		super(pNode);
	}
	
	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(node().position().getX(), node().position().getY(), 0, 0);
	}

	@Override
	public boolean contains(Point pPoint)
	{
		return node().position().distance(pPoint) < SELECTION_DISTANCE;
	}

	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		return node().position();
	}

	@Override
	protected Shape getShape()
	{
		return new Rectangle2D.Double(node().position().getX(), node().position().getY(), 0, 0);
	}

}
