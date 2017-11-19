package ca.mcgill.cs.stg.jetuml.graph.views.nodes;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Conversions;
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
	public static final int DEFAULT_WIDTH = 60;
	public static final int DEFAULT_HEIGHT = 20;
	
	private Rectangle aNameBounds;
	private Rectangle aValueBounds;
	private double aBoxWidth;
	private double aAxisX;
	
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
	
	private boolean boxedValue()
	{
		return ((FieldNode)node()).isBoxedValue();
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
		int leftWidth = name().getBounds().getWidth();
		MultiLineString equal = new MultiLineString();
		equal.setText(" = ");
		int midWidth = equal.getBounds().getWidth();
      
		int rightWidth = value().getBounds().getWidth();
		if(rightWidth == 0)
		{
			rightWidth = DEFAULT_WIDTH / 2;
		}
		rightWidth = (int) Math.max(rightWidth, aBoxWidth - midWidth / 2.0);

		aNameBounds = new Rectangle(b.getX(), b.getY(), leftWidth, b.getHeight());
		name().draw(pGraphics2D, aNameBounds);
		Rectangle mid = new Rectangle(b.getX() + leftWidth, b.getY(), midWidth, b.getHeight());
		equal.draw(pGraphics2D, mid);
		aValueBounds = new Rectangle(b.getMaxX() - rightWidth, b.getY(), rightWidth, b.getHeight());
		if(boxedValue())
		{
			value().setJustification(MultiLineString.CENTER);
		}
		else
		{
			name().setJustification(MultiLineString.LEFT);
		}
		value().draw(pGraphics2D, aValueBounds);
		if(boxedValue())
		{
			pGraphics2D.draw(Conversions.toRectangle2D(aValueBounds));
		}
	}
	
	@Override
	public void layout(Graph pGraph)
	{
		aNameBounds = name().getBounds(); 
		aValueBounds = value().getBounds();
		MultiLineString equal = new MultiLineString();
		equal.setText(" = ");
		Rectangle e = equal.getBounds();
		int leftWidth = aNameBounds.getWidth();
		int midWidth = e.getWidth();
		int rightWidth = aValueBounds.getWidth();
		if(rightWidth == 0)
		{
			rightWidth = DEFAULT_WIDTH / 2;
		}
		rightWidth = (int) Math.max(rightWidth, aBoxWidth - midWidth / 2.0);
		double width = leftWidth + midWidth + rightWidth;
		double height = Math.max(aNameBounds.getHeight(), Math.max(aValueBounds.getHeight(), e.getHeight()));

		Rectangle bounds = getBounds();
		setBounds(new Rectangle(bounds.getX(), bounds.getY(), (int)width, (int)height));
		aAxisX = leftWidth + midWidth / 2;
		aValueBounds = new Rectangle(bounds.getMaxX() - rightWidth, bounds.getY(), aValueBounds.getWidth(), aValueBounds.getHeight());
	}
	
	/**
     * Sets the box width.
     * @param pBoxWidth the new box width
	 */
	public void setBoxWidth(double pBoxWidth)
	{
		aBoxWidth = pBoxWidth;
	}
	
	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		Rectangle b = getBounds();
		return new Point((b.getMaxX() + b.getX() + aAxisX) / 2, b.getCenter().getY());
	}
	
	/**
	 * @return The axis.
	 */
	public double getAxis()
	{
		return aAxisX;
	}
	
	@Override
	public Shape getShape()
	{
		if(boxedValue())
		{
			return Conversions.toRectangle2D(aValueBounds);
		}
		else
		{
			return new Rectangle2D.Double();
		}
	}
}
