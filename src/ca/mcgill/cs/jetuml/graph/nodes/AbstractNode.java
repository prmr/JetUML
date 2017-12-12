package ca.mcgill.cs.jetuml.graph.nodes;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;

import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.ValueExtractor;
import ca.mcgill.cs.jetuml.graph.ValueExtractor.Type;
import ca.mcgill.cs.jetuml.persistence.Properties;
import ca.mcgill.cs.jetuml.views.nodes.NodeView;

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
	
	@Override
	public Properties properties()
	{
		Properties properties = new Properties();
		properties.put("x", aPosition.getX());
		properties.put("y", aPosition.getY());
		return properties;
	}
	
	@Override
	public void initialize(ValueExtractor pExtractor)
	{
		aPosition = new Point((int)pExtractor.get("x", Type.INT), (int) pExtractor.get("y", Type.INT));
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
