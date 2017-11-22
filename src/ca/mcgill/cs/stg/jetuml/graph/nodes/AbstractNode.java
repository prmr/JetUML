package ca.mcgill.cs.stg.jetuml.graph.nodes;

import java.awt.Graphics2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;

import ca.mcgill.cs.stg.jetuml.geom.Direction;
import ca.mcgill.cs.stg.jetuml.geom.Point;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.views.nodes.NodeView;

/**
 * Common elements for the Node hierarchy.
 * 
 * @author Martin P. Robillard
 *
 */
public abstract class AbstractNode implements Node
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
	public Rectangle getBounds()
	{
		return aView.getBounds();
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
	public void draw(Graphics2D pGraphics2D)
	{
		aView.draw(pGraphics2D);		
	}

	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		return aView.getConnectionPoint(pDirection);
	}

	@Override
	public void layout(Graph pGraph)
	{
		aView.layout(pGraph);	
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
		return getClass().getSimpleName() + " " + getBounds();
	}
	
	/**
	 * The persistence delegate recovers the position of the point.
	 * 
	 * @param pEncoder the encoder to which to add the delegate
	 */
	public static void setPersistenceDelegate(Encoder pEncoder)
	{
		pEncoder.setPersistenceDelegate(AbstractNode.class, new DefaultPersistenceDelegate()
		{
			protected void initialize(Class<?> pType, Object pOldInstance, Object pNewInstance, Encoder pOut) 
			{
				super.initialize(pType, pOldInstance, pNewInstance, pOut);
				int x = ((Node)pOldInstance).position().getX();
				int y = ((Node)pOldInstance).position().getY();
				pOut.writeStatement( new Statement(pOldInstance, "translate", new Object[]{ x, y }) );            
			}
		});
	}
	
}
