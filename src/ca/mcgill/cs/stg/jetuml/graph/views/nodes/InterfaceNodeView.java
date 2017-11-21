package ca.mcgill.cs.stg.jetuml.graph.views.nodes;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Conversions;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.nodes.InterfaceNode;

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
	
	/**
	 * @param pNode The node to wrap.
	 */
	public InterfaceNodeView(InterfaceNode pNode)
	{
		super(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	private MultiLineString name()
	{
		return ((InterfaceNode)node()).getName();
	}
	
	private MultiLineString methods()
	{
		return ((InterfaceNode)node()).getMethods();
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		int midHeight = computeMiddle().getHeight();
		int bottomHeight = computeBottom().getHeight();
		Rectangle2D top = new Rectangle2D.Double(getBounds().getX(), getBounds().getY(), 
				getBounds().getWidth(), getBounds().getHeight() - midHeight - bottomHeight);
		pGraphics2D.draw(top);
		name().draw(pGraphics2D, Conversions.toRectangle(top));
		Rectangle2D mid = new Rectangle2D.Double(top.getX(), top.getMaxY(), top.getWidth(), midHeight);
		pGraphics2D.draw(mid);
		Rectangle2D bot = new Rectangle2D.Double(top.getX(), mid.getMaxY(), top.getWidth(), bottomHeight);
		pGraphics2D.draw(bot);
		methods().draw(pGraphics2D, Conversions.toRectangle(bot));
	}
	
	/**
	 * @return The area of the middle compartment. The x and y values
	 * are meaningless.
	 */
	protected Rectangle computeMiddle()
	{
		return new Rectangle(0, 0, 0, 0);
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
			
		Rectangle bottom = methods().getBounds();
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
		Rectangle top = name().getBounds(); 
		
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
		Rectangle middle = computeMiddle();
		Rectangle bottom = computeBottom();

		Rectangle bounds = new Rectangle(getBounds().getX(), getBounds().getY(), 
				Math.max(Math.max(top.getWidth(), middle.getWidth()), bottom.getWidth()), top.getHeight() + middle.getHeight() + bottom.getHeight());
		setBounds(Grid.snapped(bounds));
	}
	
	/**
	 * @return True if the node requires a bottom compartment.
	 */
	protected boolean needsBottomCompartment()
	{
		return !methods().getText().isEmpty();
	}
}
