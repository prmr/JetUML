package ca.mcgill.cs.jetuml.persistence;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.ValueExtractor;
import ca.mcgill.cs.jetuml.graph.nodes.ChildNode;
import ca.mcgill.cs.jetuml.graph.nodes.ParentNode;

/**
 * Converts a JSONObject to a graph.
 *
 * @author Martin P. Robillard
 *
 */
public final class JsonDecoder
{
	private static final String PREFIX_DIAGRAMS = "ca.mcgill.cs.jetuml.diagrams.";
	private static final String PREFIX_NODES = "ca.mcgill.cs.jetuml.graph.nodes.";
	private static final String PREFIX_EDGES = "ca.mcgill.cs.jetuml.graph.edges.";
	
	private JsonDecoder() {}
	
	private static ValueExtractor createValueExtractor(JSONObject pObject)
	{
		return new ValueExtractor()
		{
			@Override
			public Object get(String pKey, Type pType)
			{
				if( pType == Type.BOOLEAN )
				{
					return pObject.getBoolean(pKey);
				}
				else if( pType == Type.INT )
				{
					return pObject.getInt(pKey);
				}
				else
				{
					return pObject.getString(pKey);
				}
			}
		};
	}

	
	/**
	 * @param pGraph A JSON object that encodes the graph.
	 * @return The decoded graph.
	 */
	public static Graph decode(JSONObject pGraph)
	{
		assert pGraph != null;
		try
		{
			Class<?> diagramClass = Class.forName(PREFIX_DIAGRAMS + pGraph.getString("diagram"));
			Graph graph = (Graph) diagramClass.newInstance();
			DeserializationContext context = new DeserializationContext(graph);
			decodeNodes(context, pGraph);
			restoreChildren(context, pGraph);
			decodeEdges(context, pGraph);
			return graph;
		}
		catch( ClassNotFoundException | IllegalAccessException | InstantiationException exception )
		{
			throw new DeserializationException("Cannot instantiate serialized object", exception);
		}
	}
	
	/* 
	 * Extracts information about nodes from pObject and creates new objects
	 * to represent them in pGraph.
	 * throws Deserialization Exception
	 */
	private static void decodeNodes(DeserializationContext pContext, JSONObject pObject)
	{
		JSONArray nodes = pObject.getJSONArray("nodes");
		for( int i = 0; i < nodes.length(); i++ )
		{
			try
			{
				JSONObject object = nodes.getJSONObject(i);
				Class<?> nodeClass = Class.forName(PREFIX_NODES + object.getString("type"));
				Node node = (Node) nodeClass.newInstance();
				node.initialize(createValueExtractor(object));
				pContext.addNode(node, object.getInt("id"));
				pContext.getGraph().restoreRootNode(node);
			}
			catch( ClassNotFoundException | IllegalAccessException | InstantiationException exception )
			{
				throw new DeserializationException("Cannot instantiate serialized object", exception);
			}
		}
	}
	
	/* 
	 * Restores the parent-child hierarchy within the context's graph. Assumes
	 * the context has been initialized with all the nodes.
	 */
	private static void restoreChildren(DeserializationContext pContext, JSONObject pObject)
	{
		JSONArray nodes = pObject.getJSONArray("nodes");
		for( int i = 0; i < nodes.length(); i++ )
		{
			JSONObject object = nodes.getJSONObject(i);
			if( object.has("children"))
			{
				Node node = pContext.getNode( object.getInt("id"));
				JSONArray children = object.getJSONArray("children");
				for( int j = 0; j < children.length(); j++ )
				{
					((ParentNode)node).addChild((ChildNode)pContext.getNode(children.getInt(j)));
				}
			}
		}
	}
	
	/* 
	 * Extracts information about nodes from pObject and creates new objects
	 * to represent them in pGraph.
	 * throws Deserialization Exception
	 */
	private static void decodeEdges(DeserializationContext pContext, JSONObject pObject)
	{
		JSONArray edges = pObject.getJSONArray("edges");
		for( int i = 0; i < edges.length(); i++ )
		{
			try
			{
				JSONObject object = edges.getJSONObject(i);
				Class<?> edgeClass = Class.forName(PREFIX_EDGES + object.getString("type"));
				Edge edge = (Edge) edgeClass.newInstance();
				edge.initialize(createValueExtractor(object));
				pContext.getGraph().restoreEdge(edge, pContext.getNode(object.getInt("start")), pContext.getNode(object.getInt("end")));
			}
			catch( ClassNotFoundException | IllegalAccessException | InstantiationException exception )
			{
				throw new DeserializationException("Cannot instantiate serialized object", exception);
			}
		}
	}
}
