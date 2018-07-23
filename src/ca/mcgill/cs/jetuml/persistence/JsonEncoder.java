/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
 *     
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.persistence;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.Properties;
import ca.mcgill.cs.jetuml.diagram.Property;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ParentNode;

/**
 * Converts a graph to JSON notation. The notation includes:
 * * The JetUML version
 * * The graph type
 * * An array of node encodings
 * * An array of edge encodings
 */
public final class JsonEncoder
{
	private JsonEncoder() {}
	
	/**
	 * @param pGraph The graph to serialize.
	 * @return A JSON object that encodes the graph.
	 */
	public static JSONObject encode(Diagram pGraph)
	{
		assert pGraph != null;
		
		JSONObject object = new JSONObject();
		object.put("version", RESOURCES.getString("application.version.number"));
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
			nodes.put(encodeNode(node, pContext));
		}
		return nodes;
	}
	
	private static JSONObject encodeNode(Node pNode, SerializationContext pContext)
	{
		JSONObject object = toJSONObject(pNode.properties());
		object.put("id", pContext.getId(pNode));
		object.put("type", pNode.getClass().getSimpleName());
		if( pNode instanceof ParentNode )
		{
			object.put("children", encodeChildren(pNode, pContext));
		}
		return object;
	}
	
	private static JSONArray encodeChildren(Node pNode, SerializationContext pContext)
	{
		JSONArray children = new JSONArray();
		for( ChildNode child : ((ParentNode)pNode).getChildren())
		{
			children.put(pContext.getId(child));
		}
		return children;
	}
	
	private static JSONArray encodeEdges(AbstractContext pContext)
	{
		JSONArray edges = new JSONArray();
		for( Edge edge : pContext.getGraph().edges() ) 
		{
			JSONObject object = toJSONObject(edge.properties());
			object.put("type", edge.getClass().getSimpleName());
			object.put("start", pContext.getId(edge.getStart()));
			object.put("end", pContext.getId(edge.getEnd()));
			
			edges.put(object);
		}
		return edges;
	}
	
	private static JSONObject toJSONObject(Properties pProperties)
	{
		JSONObject object = new JSONObject();
		for( Property property : pProperties )
		{
			Object value = property.get();
			if( value instanceof String || value instanceof Enum )
			{
				object.put(property.getName(), value.toString());
			}
			else if( value instanceof Integer)
			{
				object.put(property.getName(), (int) value);
			}
			else if( value instanceof Boolean)
			{
				object.put(property.getName(), (boolean) value);
			}
		}
		return object;
	}
}
