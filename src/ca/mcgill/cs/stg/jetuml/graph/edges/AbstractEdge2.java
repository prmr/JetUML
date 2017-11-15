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
public class AbstractEdge2 implements Edge
{
	protected EdgeView aView;
	private Node aStart;
	private Node aEnd;
	private Graph aGraph;
	
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
	
	@Override
	public AbstractEdge2 clone()
	{
		AbstractEdge2 clone;
		try
		{
			clone = (AbstractEdge2) super.clone();
			clone.aView = aView.copy(clone);
			return clone;
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}
}
