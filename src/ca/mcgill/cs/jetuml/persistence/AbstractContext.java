package ca.mcgill.cs.jetuml.persistence;

import java.util.HashMap;
import java.util.Iterator;

import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Node;

/**
 * Base class for serialization and deserialization contexts. A context 
 * is a mapping between nodes and arbitrary identifiers. The only constraint
 * on identifiers is that they consistently preserve mapping between objects and
 * their identity.
 * 
 * @author Martin P. Robillard
 *
 */
public abstract class AbstractContext implements Iterable<Node>
{
	protected final HashMap<Node, Integer> aNodes = new HashMap<>();
	private final Graph aGraph;
	
	/**
	 * Initializes the context with a graph.
	 * 
	 * @param pGraph The graph that corresponds to the context.
	 * @pre pGraph != null.
	 */
	protected AbstractContext(Graph pGraph)
	{
		assert pGraph != null;
		aGraph = pGraph;
	}
	
	/**
	 * @return The graph associated with this context. Never null.
	 */
	public Graph getGraph()
	{
		return aGraph;
	}
	
	/**
	 * @param pNode The node to check.
	 * @return The id for the node.
	 * @pre pNode != null
	 * @pre pNode is in the map.
	 */
	public int getId(Node pNode)
	{
		assert pNode != null;
		assert aNodes.containsKey(pNode);
		return aNodes.get(pNode);
	}
	
	@Override
	public Iterator<Node> iterator()
	{
		return aNodes.keySet().iterator();
	}
}
