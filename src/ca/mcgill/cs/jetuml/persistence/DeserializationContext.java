package ca.mcgill.cs.jetuml.persistence;

import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Node;

/**
 * A deserialization context allows clients to incrementally build
 * up the context. The identifiers that correspond to objects must be 
 * specified explicitly. 
 * 
 * @author Martin P. Robillard
 *
 */
public class DeserializationContext extends AbstractContext
{
	/**
	 * Initializes an empty context and associates it with
	 * pGraph.
	 * 
	 * @param pGraph The graph associated with the context.
	 * @pre pGraph != null.
	 */
	public DeserializationContext(Graph pGraph)
	{
		super( pGraph );
	}
	
	/**
	 * Adds a node to the context.
	 * 
	 * @param pNode The node to add.
	 * @param pId The id to associated with this node.
	 * @pre pNode != null;
	 */
	public void addNode(Node pNode, int pId)
	{
		assert pNode != null;
		aNodes.put(pNode, pId);
	}
	
	/**
	 * @param pId The identifier to search for.
	 * @return The node associated with this identifier.
	 * @pre pId exists as a value.
	 */
	public Node getNode(int pId)
	{
		for( Node node : aNodes.keySet() )
		{
			if( aNodes.get(node) == pId )
			{
				return node;
			}
		}
		assert false;
		return null;
	}
}
