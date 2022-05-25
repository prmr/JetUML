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

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.Property;
import org.jetuml.geom.Point;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Converts a JSONObject to a versioned diagram.
 */
public final class JsonDecoder
{
	private static final String PREFIX_NODES = "org.jetuml.diagram.nodes.";
	private static final String PREFIX_EDGES = "org.jetuml.diagram.edges.";
	
	private JsonDecoder() {}
	
	/**
	 * @param pDiagram A JSON object that encodes the diagram.
	 * @return The decoded diagram.
	 * @throws DeserializationException If it's not possible to decode the object into a valid diagram.
	 */
	public static Diagram decode(JSONObject pDiagram)
	{
		assert pDiagram != null;
		try
		{
			Diagram diagram = new Diagram(DiagramType.fromName(pDiagram.getString("diagram")));
			DeserializationContext context = new DeserializationContext(diagram);
			decodeNodes(context, pDiagram);
			restoreChildren(context, pDiagram);
			restoreRootNodes(context);
			decodeEdges(context, pDiagram);
			context.attachNodes();
			return diagram;
		}
		catch( JSONException | IllegalArgumentException exception )
		{
			throw new DeserializationException("Cannot decode serialized object", exception);
		}
	}
	
	/* 
	 * Extracts information about nodes from pObject and creates new objects
	 * to represent them.
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
				Node node = (Node) nodeClass.getDeclaredConstructor().newInstance();
				node.moveTo(new Point(object.getInt("x"), object.getInt("y")));
				for( Property property : node.properties() )
				{
					property.set(object.get(property.name().external()));
				}
				pContext.addNode(node, object.getInt("id"));
			}
			catch( ReflectiveOperationException exception )
			{
				throw new DeserializationException("Cannot instantiate serialized object", exception);
			}
		}
	}
	
	/* 
	 * Discovers the root nodes and stores them in the diagram.
	 */
	private static void restoreRootNodes(DeserializationContext pContext)
	{
		for( Node node : pContext )
		{
			if( !node.hasParent() )
			{
				pContext.pDiagram().addRootNode(node);
			}
		}
	}
	
	/* 
	 * Restores the parent-child hierarchy within the context's diagram. Assumes
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
					node.addChild(pContext.getNode(children.getInt(j)));
				}
			}
		}
	}
	
	/* 
	 * Extracts information about nodes from pObject and creates new objects
	 * to represent them.
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
				Edge edge = (Edge) edgeClass.getDeclaredConstructor().newInstance();
				
				for( Property property : edge.properties())
				{
					property.set(object.get(property.name().external()));
				}
				edge.connect(pContext.getNode(object.getInt("start")), pContext.getNode(object.getInt("end")), pContext.pDiagram());
				pContext.pDiagram().addEdge(edge);
			}
			catch( ReflectiveOperationException exception )
			{
				throw new DeserializationException("Cannot instantiate serialized object", exception);
			}
		}
	}
}
