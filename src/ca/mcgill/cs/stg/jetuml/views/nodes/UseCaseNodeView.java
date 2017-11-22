package ca.mcgill.cs.stg.jetuml.views.nodes;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.nodes.UseCaseNode;

/**
 * An object to render a UseCaseNode.
 * 
 * @author Martin P. Robillard
 *
 */
public class UseCaseNodeView extends RectangleBoundedNodeView
{
	private static final int DEFAULT_WIDTH = 110;
	private static final int DEFAULT_HEIGHT = 40;
	
	/**
	 * @param pNode The node to wrap.
	 */
	public UseCaseNodeView(UseCaseNode pNode)
	{
		super(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);      
		pGraphics2D.draw(getShape());
		name().draw(pGraphics2D, getBounds());
	}
	
	@Override
	public Shape getShape()
	{
		return new Ellipse2D.Double(node().position().getX(), node().position().getY(), 
				getBounds().getWidth(), getBounds().getHeight());
	}
	
	private MultiLineString name()
	{
		return ((UseCaseNode)node()).getName();
	}
	
	@Override	
	public void layout(Graph pGraph)
	{
		Rectangle bounds = name().getBounds();
		bounds = new Rectangle(getBounds().getX(), getBounds().getY(), 
				Math.max(bounds.getWidth(), DEFAULT_WIDTH), Math.max(bounds.getHeight(), DEFAULT_HEIGHT));
		setBounds(Grid.snapped(bounds));
	}
}
