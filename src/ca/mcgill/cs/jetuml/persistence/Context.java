package ca.mcgill.cs.jetuml.persistence;

import java.util.HashMap;
import java.util.Iterator;

import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.nodes.ParentNode;

/**
 * A serialization context that contains all the nodes in a graph mapped
 * to an id.
 * 
 * @author Martin P. Robillard
 *
 */
public class Context implements Iterable<Node>
{
	private final HashMap<Node, Integer> aNodes = new HashMap<>();
	
	/**
	 * Creates a serialization context for a given instance of a graph.
	 * 
	 * @param pGraph The graph to serialize.
	 * @pre pGraph != null;
	 */
	public Context(Graph pGraph)
	{
		getAllNodes(pGraph);
	}
	
	/**
	 * @param pNode The node to chek.
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
	
	private void getAllNodes(Graph pGraph)
	{
		for( Node node : pGraph.getRootNodes() )
		{
			aNodes.put(node, aNodes.size());
			if( node instanceof ParentNode )
			{
				addChildren((ParentNode) node);
			}
		}
	}
	
	private void addChildren(ParentNode pParent)
	{
		for( Node node : pParent.getChildren() )
		{
			if( !aNodes.containsKey(node))
			{
				aNodes.put(node, aNodes.size());
			}
			if( node instanceof ParentNode )
			{
				addChildren((ParentNode)node);
			}
		}
	}

	@Override
	public Iterator<Node> iterator()
	{
		return aNodes.keySet().iterator();
	}
}
