package ca.mcgill.cs.jetuml.views.nodes;

import java.awt.Graphics2D;

import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.nodes.ClassNode;
import ca.mcgill.cs.jetuml.views.StringViewer2;

/**
 * An object to render an interface in a class diagram.
 * 
 * @author Martin P. Robillard
 *
 */
public class ClassNodeView extends InterfaceNodeView
{
	private static final StringViewer2 STRING_VIEWER = new StringViewer2(StringViewer2.Align.LEFT, false, false);
	
	/**
	 * @param pNode The node to wrap.
	 */
	public ClassNodeView(ClassNode pNode)
	{
		super(pNode);
	}
	
	private String attributes()
	{
		return ((ClassNode)node()).getAttributes();
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D); 
		int bottomHeight = computeBottom().getHeight();
		Rectangle top = new Rectangle(getBounds().getX(), getBounds().getY(), 
				getBounds().getWidth(), (int) Math.round(getBounds().getHeight() - middleHeight() - bottomHeight));
		Rectangle mid = new Rectangle(top.getX(), top.getMaxY(), top.getWidth(), (int) Math.round(middleHeight()));
		STRING_VIEWER.draw(attributes(), pGraphics2D, mid);
	}
	
	/**
	 * @return True if the node requires a bottom compartment.
	 */
	@Override
	protected boolean needsMiddleCompartment()
	{
		return attributes().length() > 0;
	}
	
	@Override
	protected int middleWidth()
	{
		if( !needsMiddleCompartment() )
		{
			return 0;
		}
		else
		{
			return Math.max(STRING_VIEWER.getBounds(attributes()).getWidth(), DEFAULT_WIDTH);
		}
	}
	
	@Override
	protected int middleHeight()
	{
		if( !needsMiddleCompartment() )
		{
			return 0;
		}
		else
		{
			return Math.max(STRING_VIEWER.getBounds(attributes()).getHeight(), DEFAULT_COMPARTMENT_HEIGHT);
		}
	}
}
