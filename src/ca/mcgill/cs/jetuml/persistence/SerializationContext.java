package ca.mcgill.cs.jetuml.persistence;

import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.nodes.ParentNode;

/**
 * A serialization context automatically finds all the nodes
 * in a graph, including children nodes, and creates a new map between
 * nodes and identifiers.
 * 
 * @author Martin P. Robillard
 *
 */
public class SerializationContext extends AbstractContext
{
	/**
	 * Automatically creates the map between nodes in pGraph
	 * and fresh identifiers.
	 * 
	 * @param pGraph The graph to load into the context.
	 * @pre pGraph != null.
	 */
	public SerializationContext(Graph pGraph)
	{
		super(pGraph);
		getAllNodes(pGraph);
	}
	
	/**
	 * Adds a node to the context if it is not already there. Its identifier
	 * is automatically defined. If the node is already in the 
	 * context, it is not added.
	 * 
	 * @param pNode The node to add.
	 * @pre pNode != null;
	 * @pre !aNodes.containsKey(pNode)
	 */
	private void addNode(Node pNode)
	{
		assert pNode != null;
		assert !aNodes.containsKey(pNode);
		aNodes.put(pNode, aNodes.size());
	}
	
	private void getAllNodes(Graph pGraph)
	{
		for( Node node : pGraph.getRootNodes() )
		{
			addNode(node);
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
			addNode(node);
			if( node instanceof ParentNode )
			{
				addChildren((ParentNode)node);
			}
		}
	}
}
