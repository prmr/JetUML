/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.persistence;

import org.jetuml.JetUML;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.Properties;
import org.jetuml.diagram.Property;
import org.jetuml.persistence.json.JsonArray;
import org.jetuml.persistence.json.JsonObject;

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
	 * @param pDiagram The diagram to serialize.
	 * @return A JSON object that encodes the diagram.
	 */
	public static JsonObject encode(Diagram pDiagram)
	{
		assert pDiagram != null;
		
		JsonObject object = new JsonObject();
		object.put("version", JetUML.VERSION.toString());
		object.put("diagram", pDiagram.getName());
		SerializationContext context = new SerializationContext(pDiagram);
		object.put("nodes", encodeNodes(context));
		object.put("edges", encodeEdges(context));
		return object;
	}
	
	private static JsonArray encodeNodes(SerializationContext pContext)
	{
		JsonArray nodes = new JsonArray();
		for( Node node : pContext ) 
		{
			nodes.add(encodeNode(node, pContext));
		}
		return nodes;
	}
	
	private static JsonObject encodeNode(Node pNode, SerializationContext pContext)
	{
		JsonObject object = toJSONObject(pNode.properties());
		object.put("id", pContext.getId(pNode));
		object.put("type", pNode.getClass().getSimpleName());
		object.put("x", pNode.position().x());
		object.put("y", pNode.position().y());
		if( pNode.getChildren().size() > 0 )
		{
			object.put("children", encodeChildren(pNode, pContext));
		}
		return object;
	}
	
	private static JsonArray encodeChildren(Node pNode, SerializationContext pContext)
	{
		JsonArray children = new JsonArray();
		pNode.getChildren().forEach(child -> children.add(pContext.getId(child)));
		return children;
	}
	
	private static JsonArray encodeEdges(AbstractContext pContext)
	{
		JsonArray edges = new JsonArray();
		for( Edge edge : pContext.diagram().edges() ) 
		{
			JsonObject object = toJSONObject(edge.properties());
			object.put("type", edge.getClass().getSimpleName());
			object.put("start", pContext.getId(edge.start()));
			object.put("end", pContext.getId(edge.end()));
			
			edges.add(object);
		}
		return edges;
	}
	
	private static JsonObject toJSONObject(Properties pProperties)
	{
		JsonObject object = new JsonObject();
		for( Property property : pProperties )
		{
			Object value = property.get();
			if( value instanceof String || value instanceof Enum )
			{
				object.put(property.name().external(), value.toString());
			}
			else if( value instanceof Integer)
			{
				object.put(property.name().external(), value);
			}
			else if( value instanceof Boolean)
			{
				object.put(property.name().external(), value);
			}
		}
		return object;
	}
}
