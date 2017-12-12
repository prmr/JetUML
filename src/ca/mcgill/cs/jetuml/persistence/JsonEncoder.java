package ca.mcgill.cs.jetuml.persistence;

import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcgill.cs.jetuml.UMLEditor;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.nodes.ChildNode;
import ca.mcgill.cs.jetuml.graph.nodes.ParentNode;

/**
 * Converts a graph to JSON notation. The notation includes:
 * * The JetUML version
 * * The graph type
 * * An array of node encodings
 * * An array of edge encodings
 * 
 * @author Martin P. Robillard
 *
 */
public final class JsonEncoder
{
	private JsonEncoder() {}
	
	/**
	 * @param pGraph The graph to serialize.
	 * @return A JSON object that encodes the graph.
	 */
	public static JSONObject encode(Graph pGraph)
	{
		assert pGraph != null;
		JSONObject object = new JSONObject();
		object.put("version", ResourceBundle.getBundle(UMLEditor.class.getName() + "Version").getString("version.number"));
		object.put("diagram", pGraph.getClass().getSimpleName());
		SerializationContext context = new SerializationContext(pGraph);
		object.put("nodes", encodeNodes(context));
		object.put("edges", encodeEdges(context));
		return object;
	}
	
	private static JSONArray encodeNodes(SerializationContext pContext)
	{
		JSONArray nodes = new JSONArray();
		for( Node node : pContext ) 
		{
			JSONObject object = toJSONObject(node.properties());
			object.put("id", pContext.getId(node));
			object.put("type", node.getClass().getSimpleName());
			if( node instanceof ParentNode )
			{
				JSONArray children = new JSONArray();
				object.put("children", children);
				for( ChildNode child : ((ParentNode)node).getChildren())
				{
					children.put(pContext.getId(child));
				}
			}
			nodes.put(object);
		}
		return nodes;
	}
	
	private static JSONArray encodeEdges(AbstractContext pContext)
	{
		JSONArray edges = new JSONArray();
		for( Edge edge : pContext.getGraph().getEdges() ) 
		{
			JSONObject object = toJSONObject(edge.properties());
			object.put("start", pContext.getId(edge.getStart()));
			object.put("end", pContext.getId(edge.getEnd()));
			
			edges.put(object);
		}
		return edges;
	}
	
	private static JSONObject toJSONObject(Properties pProperties)
	{
		JSONObject object = new JSONObject();
		for( String key : pProperties )
		{
			Object value = pProperties.get(key);
			if( value instanceof String || value instanceof Enum )
			{
				object.put(key, (String) value);
			}
			else if( value instanceof Integer)
			{
				object.put(key, (int) value);
			}
			else if( value instanceof Boolean)
			{
				object.put(key, (boolean) value);
			}
		}
		return object;
	}
}
