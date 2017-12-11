package ca.mcgill.cs.jetuml.persistence;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcgill.cs.jetuml.diagrams.UseCaseDiagramGraph;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.ValueExtractor;
import ca.mcgill.cs.jetuml.graph.nodes.ActorNode;

/**
 * Converts a JSONObject to a graph.
 *
 * @author Martin P. Robillard
 *
 */
public final class JsonDecoder
{
	private static final String PREFIX = "ca.mcgill.cs.jetuml.diagrams.";
	private static final String PREFIX2 = "ca.mcgill.cs.jetuml.graph.nodes.";
	
	private JsonDecoder() {}
	
	public static void main(String[] args)
	{
		UseCaseDiagramGraph graph = new UseCaseDiagramGraph();
		graph.addNode( new ActorNode(), new Point(10, 20));
		graph.addNode( new ActorNode(), new Point(100, 200));
		JSONObject object = JsonEncoder.encode(graph);
		Graph decoded = JsonDecoder.decode(object);
	}
	
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
			Class<?> diagramClass = Class.forName(PREFIX + pGraph.getString("diagram"));
			Graph graph = (Graph) diagramClass.newInstance();
			JSONArray nodes = pGraph.getJSONArray("nodes");
			for( int i = 0; i < nodes.length(); i++ )
			{
				JSONObject object = nodes.getJSONObject(i);
				Class<?> nodeClass = Class.forName(PREFIX2 + object.getString("type"));
				Node node = (Node) nodeClass.newInstance();
				node.initialize(createValueExtractor(object));
				graph.restoreRootNode(node);
			}
			return graph;
		}
		catch( ClassNotFoundException | IllegalAccessException | InstantiationException exception )
		{
			throw new DeserializationException("Cannot instantiate serialized object", exception);
		}
	}
}
