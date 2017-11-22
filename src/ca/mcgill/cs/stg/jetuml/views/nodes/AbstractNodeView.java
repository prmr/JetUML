package ca.mcgill.cs.stg.jetuml.views.nodes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

/**
 * Basic services for drawing nodes.
 * 
 * @author Martin P. Robillard
 *
 */
public abstract class AbstractNodeView implements NodeView
{
	public static final int SHADOW_GAP = 4;
	private static final Color SHADOW_COLOR = Color.LIGHT_GRAY;
	
	private Node aNode;
	
	/**
	 * @param pNode The node to wrap.
	 */
	protected AbstractNodeView(Node pNode)
	{
		aNode = pNode;
	}
	
	/**
	 * @return The wrapped edge.
	 */
	protected Node node()
	{
		return aNode;
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		Shape shape = getShape();
		Color oldColor = pGraphics2D.getColor();
		pGraphics2D.translate(SHADOW_GAP, SHADOW_GAP);      
		pGraphics2D.setColor(SHADOW_COLOR);
		pGraphics2D.fill(shape);
		pGraphics2D.translate(-SHADOW_GAP, -SHADOW_GAP);
		pGraphics2D.setColor(pGraphics2D.getBackground());
		pGraphics2D.fill(shape);      
		pGraphics2D.setColor(oldColor);
	}
	
	/**
     *  @return the shape to be used for computing the drop shadow
    */
	protected abstract Shape getShape();

	@Override
	public void layout(Graph pGraph)
	{}
}
