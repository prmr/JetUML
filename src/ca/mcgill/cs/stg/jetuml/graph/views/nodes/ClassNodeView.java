package ca.mcgill.cs.stg.jetuml.graph.views.nodes;

import java.awt.Graphics2D;

import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.nodes.ClassNode;

/**
 * An object to render an interface in a class diagram.
 * 
 * @author Martin P. Robillard
 *
 */
public class ClassNodeView extends InterfaceNodeView
{
	/**
	 * @param pNode The node to wrap.
	 */
	public ClassNodeView(ClassNode pNode)
	{
		super(pNode);
	}
	
	private MultiLineString attributes()
	{
		return ((ClassNode)node()).getAttributes();
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D); 
		int midHeight = computeMiddle().getHeight();
		int bottomHeight = computeBottom().getHeight();
		Rectangle top = new Rectangle(getBounds().getX(), getBounds().getY(), 
				getBounds().getWidth(), (int) Math.round(getBounds().getHeight() - midHeight - bottomHeight));
		Rectangle mid = new Rectangle(top.getX(), top.getMaxY(), top.getWidth(), (int) Math.round(midHeight));
		attributes().draw(pGraphics2D, mid);
	}
	
	/**
	 * @return True if the node requires a bottom compartment.
	 */
	@Override
	protected boolean needsMiddleCompartment()
	{
		return !attributes().getText().isEmpty();
	}
	
	/**
	 * @return The area of the middle compartment. The x and y values
	 * are meaningless.
	 */
	@Override
	protected Rectangle computeMiddle()
	{
		if( !needsMiddleCompartment() )
		{
			return new Rectangle(0, 0, 0, 0);
		}
			
		Rectangle attributes = attributes().getBounds();
		attributes = attributes.add(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_COMPARTMENT_HEIGHT));
		return attributes;
	}
}
