package ca.mcgill.cs.jetuml.graph.nodes;

import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.graph.AbstractGraphElement;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.views.nodes.NodeView;

/**
 * Common elements for the Node hierarchy.
 * 
 * @author Martin P. Robillard
 *
 */
public abstract class AbstractNode extends AbstractGraphElement implements Node
{
	private NodeView aView;
	private Point aPosition = new Point(0, 0);
	
	/**
	 * Calls an abstract delegate to generate the view for this node
	 * and positions the node at (0,0).
	 */
	protected AbstractNode()
	{
		aView = generateView();
		properties().addInvisible("x", () -> aPosition.getX(), pX -> aPosition.setX((int)pX)); 
		properties().addInvisible("y", () -> aPosition.getY(), pY -> aPosition.setY((int)pY));
	}
	
	@Override
	public void translate(int pDeltaX, int pDeltaY)
	{
		aPosition = new Point( aPosition.getX() + pDeltaX, aPosition.getY() + pDeltaY );
	}
	
	/**
	 * Generates a view for this node. Because of cloning, this cannot
	 * be done in the constructor, because when a node is cloned a new 
	 * wrapper view must be produced for the clone.
	 * 
	 * @return The view that wraps this node.
	 */
	protected abstract NodeView generateView();
	
	@Override
	public NodeView view()
	{
		return aView;
	}
	
	@Override
	public Point position()
	{
		return aPosition;
	}
	
	@Override
	public void moveTo(Point pPoint)
	{
		aPosition = pPoint;
	}

	@Override
	public AbstractNode clone()
	{
		try
		{
			AbstractNode clone = (AbstractNode) super.clone();
			clone.aView = clone.generateView();
			return clone;
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " " + view().getBounds();
	}
}
