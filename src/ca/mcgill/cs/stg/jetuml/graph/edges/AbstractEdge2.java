package ca.mcgill.cs.stg.jetuml.graph.edges;

import java.awt.Graphics2D;

import ca.mcgill.cs.stg.jetuml.geom.Line;
import ca.mcgill.cs.stg.jetuml.geom.Point;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.edges.views.EdgeView;
import ca.mcgill.cs.stg.jetuml.graph.nodes.Node;

/**
 * Abstract edge in the new hierarchy.
 * 
 * @author Martin P. Robillard
 */
public abstract class AbstractEdge2 implements Edge
{
	protected EdgeView aView;
	private Node aStart;
	private Node aEnd;
	private Graph aGraph;
	
	/**
	 * Calls an abstract delegate to generate the view for this edge.
	 */
	protected AbstractEdge2()
	{
		aView = generateView();
	}
	
	@Override
	public Rectangle getBounds()
	{
		return aView.getBounds();
	}

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		aView.draw(pGraphics2D);
	}

	@Override
	public boolean contains(Point pPoint)
	{
		return aView.contains(pPoint);
	}

	@Override
	public void connect(Node pStart, Node pEnd, Graph pGraph)
	{
		assert pStart != null && pEnd != null;
		aStart = pStart;
		aEnd = pEnd;
		aGraph = pGraph;		
	}

	@Override
	public Node getStart()
	{
		return aStart;
	}

	@Override
	public Node getEnd()
	{
		return aEnd;
	}

	@Override
	public Graph getGraph()
	{
		return aGraph;
	}

	@Override
	public Line getConnectionPoints()
	{
		return aView.getConnectionPoints();
	}
	
	/**
	 * Generates a view for this edge. Because of cloning, this cannot
	 * be done in the constructor, because when an edge is clone a new 
	 * wrapper view must be produced for the clone.
	 * 
	 * @return The view that wraps this edge.
	 */
	protected abstract EdgeView generateView();
	
	@Override
	public AbstractEdge2 clone()
	{
		AbstractEdge2 clone;
		try
		{
			clone = (AbstractEdge2) super.clone();
			clone.aView = clone.generateView();
			return clone;
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}
}
