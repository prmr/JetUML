package ca.mcgill.cs.stg.jetuml.graph.edges;

import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.views.edges.EdgeView;

/**
 * Abstract edge in the new hierarchy.
 * 
 * @author Martin P. Robillard
 */
public abstract class AbstractEdge implements Edge
{
	protected EdgeView aView;
	private Node aStart;
	private Node aEnd;
	private Graph aGraph;
	
	/**
	 * Calls an abstract delegate to generate the view for this edge.
	 */
	protected AbstractEdge()
	{
		aView = generateView();
	}
	
	@Override
	public Rectangle getBounds()
	{
		return aView.getBounds();
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

	/**
	 * Generates a view for this edge. Because of cloning, this cannot
	 * be done in the constructor, because when an edge is clone a new 
	 * wrapper view must be produced for the clone.
	 * 
	 * @return The view that wraps this edge.
	 */
	protected abstract EdgeView generateView();
	
	@Override
	public AbstractEdge clone()
	{
		AbstractEdge clone;
		try
		{
			clone = (AbstractEdge) super.clone();
			clone.aView = clone.generateView();
			return clone;
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}
	
	@Override
	public EdgeView view()
	{
		return aView;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " " + getStart() + " -> " + getEnd();
	}
}
