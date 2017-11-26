package ca.mcgill.cs.jetuml.views.nodes;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import ca.mcgill.cs.jetuml.framework.MultiLineString;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.nodes.FieldNode;

/**
 * An object to render a FieldNode.
 * 
 * @author Martin P. Robillard
 *
 */
public class FieldNodeView extends RectangleBoundedNodeView
{
	private static final MultiLineString EQUALS = new MultiLineString();
	private static final int DEFAULT_WIDTH = 60;
	private static final int DEFAULT_HEIGHT = 20;
	
	static
	{
		EQUALS.setText(" = ");
	}
	
	/**
	 * @param pNode The node to wrap.
	 */
	public FieldNodeView(FieldNode pNode)
	{
		super(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	private MultiLineString name()
	{
		return ((FieldNode)node()).getName();
	}
	
	private MultiLineString value()
	{
		return ((FieldNode)node()).getValue();
	}
	
	/**
	 * @param pNewBounds The new bounds for this node.
	 */
	public void setBounds(Rectangle pNewBounds)
	{
		super.setBounds(pNewBounds);
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		final Rectangle bounds = getBounds();
		name().draw(pGraphics2D, new Rectangle(bounds.getX(), bounds.getY(), leftWidth(), bounds.getHeight()));
		EQUALS.draw(pGraphics2D, new Rectangle(bounds.getX() + leftWidth(), bounds.getY(), midWidth(), bounds.getHeight()));
		value().draw(pGraphics2D, new Rectangle(bounds.getMaxX() - rightWidth(), bounds.getY(), rightWidth(), bounds.getHeight()));
	}
	
	private int leftWidth()
	{
		return name().getBounds().getWidth();
	}
	
	private int midWidth()
	{
		return EQUALS.getBounds().getWidth();
	}
	
	private int rightWidth()
	{
		int rightWidth = value().getBounds().getWidth();
		if(rightWidth == 0)
		{
			rightWidth = DEFAULT_WIDTH / 2;
		}
		return rightWidth;
	}
	
	@Override
	public void layout(Graph pGraph)
	{
		final int width = leftWidth() + midWidth() + rightWidth();
		final int height = Math.max(name().getBounds().getHeight(), Math.max(value().getBounds().getHeight(), EQUALS.getBounds().getHeight()));
		final Rectangle bounds = getBounds();
		setBounds(new Rectangle(bounds.getX(), bounds.getY(), width, height));
	}
	
	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		Rectangle bounds = getBounds();
		return new Point((bounds.getMaxX() + bounds.getX() + getAxis()) / 2, bounds.getCenter().getY());
	}
	
	/**
	 * @return The axis.
	 */
	public int getAxis()
	{
		return leftWidth() + midWidth() / 2;
	}
	
	@Override
	public Shape getShape()
	{
		return new Rectangle2D.Double();
	}
}
