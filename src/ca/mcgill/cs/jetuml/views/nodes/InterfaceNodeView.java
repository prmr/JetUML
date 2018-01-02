package ca.mcgill.cs.jetuml.views.nodes;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import ca.mcgill.cs.jetuml.geom.Conversions;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.nodes.InterfaceNode;
import ca.mcgill.cs.jetuml.views.Grid;
import ca.mcgill.cs.jetuml.views.StringViewer2;

/**
 * An object to render an interface in a class diagram.
 * 
 * @author Martin P. Robillard
 *
 */
public class InterfaceNodeView extends RectangleBoundedNodeView
{
	protected static final int DEFAULT_WIDTH = 100;
	protected static final int DEFAULT_HEIGHT = 60;
	protected static final int DEFAULT_COMPARTMENT_HEIGHT = 20;
	private static final StringViewer2 METHOD_VIEWER = new StringViewer2(StringViewer2.Align.LEFT, false, false);
	private static final StringViewer2 NAME_VIEWER = new StringViewer2(StringViewer2.Align.CENTER, true, false);
	
	/**
	 * @param pNode The node to wrap.
	 */
	public InterfaceNodeView(InterfaceNode pNode)
	{
		super(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	private String name()
	{
		return ((InterfaceNode)node()).getName();
	}
	
	private String methods()
	{
		return ((InterfaceNode)node()).getMethods();
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		int bottomHeight = computeBottom().getHeight();
		Rectangle2D top = new Rectangle2D.Double(getBounds().getX(), getBounds().getY(), 
				getBounds().getWidth(), getBounds().getHeight() - middleHeight() - bottomHeight);
		pGraphics2D.draw(top);
		NAME_VIEWER.draw(name(), pGraphics2D, Conversions.toRectangle(top));
		Rectangle2D mid = new Rectangle2D.Double(top.getX(), top.getMaxY(), top.getWidth(), middleHeight());
		pGraphics2D.draw(mid);
		Rectangle2D bot = new Rectangle2D.Double(top.getX(), mid.getMaxY(), top.getWidth(), bottomHeight);
		pGraphics2D.draw(bot);
		METHOD_VIEWER.draw(methods(), pGraphics2D, Conversions.toRectangle(bot));
	}
	
	/**
	 * @return The width of the middle compartment.
	 */
	protected int middleWidth()
	{
		return 0;
	}
	
	/**
	 * @return The width of the middle compartment.
	 */
	protected int middleHeight()
	{
		return 0;
	}
	
	/**
	 * @return The area of the bottom compartment. The x and y values
	 * are meaningless.
	 */
	protected Rectangle computeBottom()
	{
		if( !needsBottomCompartment() )
		{
			return new Rectangle(0, 0, 0, 0);
		}
			
		Rectangle bottom = METHOD_VIEWER.getBounds(methods());
		bottom = bottom.add(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_COMPARTMENT_HEIGHT));
		return bottom;
	}
	
	/**
	 * The top is computed to be at least the default
	 * node size.
	 * @return The area of the top compartment
	 */
	protected Rectangle computeTop()
	{
		Rectangle top = NAME_VIEWER.getBounds(name()); 
		
		int minHeight = DEFAULT_COMPARTMENT_HEIGHT;
		if(!needsMiddleCompartment() && !needsBottomCompartment() )
		{
			minHeight = DEFAULT_HEIGHT;
		}
		else if( needsMiddleCompartment() ^ needsBottomCompartment() )
		{
			minHeight = 2 * DEFAULT_COMPARTMENT_HEIGHT;
		}
		top = top.add(new Rectangle(0, 0, DEFAULT_WIDTH, minHeight));

		return top;
	}
	
	/**
	 * @return True if the node requires a bottom compartment.
	 */
	protected boolean needsMiddleCompartment()
	{
		return false;
	}
	
	@Override
	public void layout(Graph pGraph)
	{
		Rectangle top = computeTop();
		Rectangle bottom = computeBottom();

		Rectangle bounds = new Rectangle(getBounds().getX(), getBounds().getY(), 
				Math.max(Math.max(top.getWidth(), middleWidth()), bottom.getWidth()), top.getHeight() + middleHeight() + bottom.getHeight());
		setBounds(Grid.snapped(bounds));
	}
	
	/**
	 * @return True if the node requires a bottom compartment.
	 */
	protected boolean needsBottomCompartment()
	{
		return methods().length() > 0;
	}
}
