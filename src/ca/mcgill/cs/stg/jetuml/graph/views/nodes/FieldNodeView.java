package ca.mcgill.cs.stg.jetuml.graph.views.nodes;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Direction;
import ca.mcgill.cs.stg.jetuml.geom.Point;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.nodes.FieldNode;

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
	
	private Rectangle aValueBounds;
	
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
		final Rectangle b = getBounds();
		
		name().draw(pGraphics2D, new Rectangle(b.getX(), b.getY(), leftWidth(), b.getHeight()));
		Rectangle mid = new Rectangle(b.getX() + leftWidth(), b.getY(), midWidth(), b.getHeight());
		EQUALS.draw(pGraphics2D, mid);
		aValueBounds = new Rectangle(b.getMaxX() - rightWidth(), b.getY(), rightWidth(), b.getHeight());
		value().draw(pGraphics2D, aValueBounds);
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
		aValueBounds = value().getBounds();
		double width = leftWidth() + midWidth() + rightWidth();
		double height = Math.max(name().getBounds().getHeight(), Math.max(aValueBounds.getHeight(), EQUALS.getBounds().getHeight()));

		Rectangle bounds = getBounds();
		setBounds(new Rectangle(bounds.getX(), bounds.getY(), (int)width, (int)height));
		bounds = getBounds();
		aValueBounds = new Rectangle(bounds.getMaxX() - rightWidth(), bounds.getY(), aValueBounds.getWidth(), aValueBounds.getHeight());
	}
	
	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		Rectangle b = getBounds();
		return new Point((b.getMaxX() + b.getX() + getAxis()) / 2, b.getCenter().getY());
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
