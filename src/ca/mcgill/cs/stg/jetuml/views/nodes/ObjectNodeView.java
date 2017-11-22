package ca.mcgill.cs.stg.jetuml.views.nodes;

import java.awt.Graphics2D;
import java.util.List;

import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Conversions;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.nodes.ChildNode;
import ca.mcgill.cs.stg.jetuml.graph.nodes.FieldNode;
import ca.mcgill.cs.stg.jetuml.graph.nodes.ObjectNode;

/**
 * An object to render an object in an object diagram.
 * 
 * @author Martin P. Robillard
 *
 */
public class ObjectNodeView extends RectangleBoundedNodeView
{
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 60;
	private static final int XGAP = 5;
	private static final int YGAP = 5;
	
	private int aTopHeight;
	
	/**
	 * @param pNode The node to wrap.
	 */
	public ObjectNodeView(ObjectNode pNode)
	{
		super(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	private MultiLineString name()
	{
		return ((ObjectNode)node()).getName();
	}
	
	private List<ChildNode> children()
	{
		return ((ObjectNode)node()).getChildren();
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		Rectangle top = getTopRectangle();
		pGraphics2D.draw(Conversions.toRectangle2D(top));
		pGraphics2D.draw(Conversions.toRectangle2D(getBounds()));
		name().draw(pGraphics2D, top);
	}
	
	@Override
	public void layout(Graph pGraph)
	{
		Rectangle bounds = name().getBounds(); 
		bounds = bounds.add(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT - YGAP));
		int leftWidth = 0;
		int rightWidth = 0;
		int height = 0;
		if( children().size() != 0 )
		{
			height = YGAP;
		}
		for(ChildNode field : children())
		{
			field.view().layout(pGraph);
			Rectangle b2 = field.view().getBounds();
			height += b2.getHeight() + YGAP;   
			int axis = ((FieldNode)field).obtainAxis();
			leftWidth = Math.max(leftWidth, axis);
			rightWidth = Math.max(rightWidth, b2.getWidth() - axis);
		}
		int width = (int) (2 * Math.max(leftWidth, rightWidth) + 2 * XGAP);
		width = Math.max(width, bounds.getWidth());
		width = Math.max(width, DEFAULT_WIDTH);
		bounds = new Rectangle(getBounds().getX(), getBounds().getY(), width, bounds.getHeight() + height);
		Rectangle snappedBounds = Grid.snapped(bounds);
		setBounds(snappedBounds);
		bounds = snappedBounds;
		aTopHeight = bounds.getHeight() - height;
		int ytop = (int)(bounds.getY() + aTopHeight + YGAP);
		int xmid = bounds.getCenter().getX();
		for(ChildNode field : children())
		{
			Rectangle b2 = field.view().getBounds();
			((FieldNode)field).setBounds(new Rectangle((int)(xmid - ((FieldNode)field).obtainAxis()), 
					ytop, ((FieldNode)field).obtainAxis() + rightWidth, b2.getHeight()));
			ytop += field.view().getBounds().getHeight() + YGAP;
		}
	}
	
	/**
	 * Returns the rectangle at the top of the object node.
	 * @return the top rectangle
	 */
	private Rectangle getTopRectangle()
	{
		return new Rectangle(getBounds().getX(), getBounds().getY(), getBounds().getWidth(), aTopHeight);
	}
}
