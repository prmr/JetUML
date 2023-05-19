/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
 * 
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.persistence;

import org.jetuml.application.Version;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.Property;
import org.jetuml.geom.Point;
import org.jetuml.persistence.DeserializationException.Category;
import org.jetuml.persistence.json.JsonArray;
import org.jetuml.persistence.json.JsonException;
import org.jetuml.persistence.json.JsonObject;

/**
 * Converts a JSONObject to a diagram. Instances of this class are intended to be
 * used as a single-use wrapper around a JSON object that is to be decoded, as such
 * new JsonDecoder(pInputObject).decode()
 */
public final class JsonDecoder
{
	private static final String PREFIX_NODES = "org.jetuml.diagram.nodes.";
	private static final String PREFIX_EDGES = "org.jetuml.diagram.edges.";
	
	private static final String PROPERTY_DIAGRAM = "diagram";
	private static final String PROPERTY_NODES = "nodes";
	private static final String PROPERTY_EDGES = "edges";
	private static final String PROPERTY_VERSION = "version";

	/* 
	 * The object that will be decoded.
	 */
	private final JsonObject aInputObject;
	private Version aVersion;
	private DeserializationContext aContext; // Wraps the diagram
	
	/**
	 * @param pInputObject The object to decode into a diagram.
	 * @pre pInputObject != null;
	 */
	public JsonDecoder(JsonObject pInputObject) 
	{
		assert pInputObject != null;
		aInputObject = pInputObject;
	}

	/**
	 * @param pDiagram A JSON object that encodes the diagram.
	 * @return The decoded diagram.
	 * @throws DeserializationException If it's not possible to decode the
	 * object into a valid diagram.
	 */
	public Diagram decode()
	{
		extractVersion();
		extractDiagram();
		try
		{
			decodeNodes();
			restoreChildren();
			restoreRootNodes();
			decodeEdges();
			return aContext.pDiagram();
		}
		catch( JsonException exception )
		{
			throw new DeserializationException(Category.SYNTACTIC, exception.getMessage());
		}
	}
	
	private void extractVersion()
	{
		try
		{
			aVersion = Version.parse(extractString(PROPERTY_VERSION));
		}
		catch(IllegalArgumentException exception)
		{
			throw new DeserializationException(Category.STRUCTURAL, "Cannot parse version number");
		}
	}
	
	private void extractDiagram()
	{
		aContext = new DeserializationContext(new Diagram(DiagramType.fromName(extractString(PROPERTY_DIAGRAM))));
	}
	
	/*
	 * Extracts a string value for the given property name, and raises
	 * a structural DeserializationException if the property is not found
	 * or was not stored as a string.
	 */
	private String extractString(String pPropertyName)
	{
		try
		{
			return aInputObject.getString(pPropertyName);
		}
		catch(JsonException exception)
		{
			throw new DeserializationException(Category.STRUCTURAL, String.format("Cannot obtain value of property '%s'", pPropertyName));
		}
	}
	
	/*
	 * Extracts a JsonArray value for the given property name, and raises
	 * a structural DeserializationException if the property is not found
	 * or was not stored as an array.
	 */
	private JsonArray extractArray(String pPropertyName)
	{
		try
		{
			return aInputObject.getJsonArray(pPropertyName);
		}
		catch(JsonException exception)
		{
			throw new DeserializationException(Category.STRUCTURAL, String.format("Cannot obtain value of property '%s'", pPropertyName));
		}
	}
	
	/*
	 * Extracts information about nodes from pObject and creates new objects to
	 * represent them. throws Deserialization Exception
	 */
	private void decodeNodes()
	{
		JsonArray nodes = extractArray(PROPERTY_NODES);
		for( int i = 0; i < nodes.size(); i++ )
		{
			try
			{
				JsonObject object = nodes.getJsonObject(i);
				Class<?> nodeClass = Class.forName(PREFIX_NODES + object.getString("type"));
				Node node = (Node) nodeClass.getDeclaredConstructor().newInstance();
				node.moveTo(new Point(object.getInt("x"), object.getInt("y")));
				for( Property property : node.properties() )
				{
					property.set(object.get(property.name().external()));
				}
				aContext.addNode(node, object.getInt("id"));
			}
			catch(ReflectiveOperationException exception)
			{
				throw new DeserializationException(Category.STRUCTURAL, "Cannot instantiate serialized object", exception);
			}
		}
	}

	/*
	 * Discovers the root nodes and stores them in the diagram.
	 */
	private void restoreRootNodes()
	{
		for( Node node : aContext )
		{
			if( !node.hasParent() )
			{
				aContext.pDiagram().addRootNode(node);
			}
		}
	}

	/*
	 * Restores the parent-child hierarchy within the context's diagram. Assumes
	 * the context has been initialized with all the nodes.
	 */
	private void restoreChildren()
	{
		JsonArray nodes = extractArray(PROPERTY_NODES);
		for( int i = 0; i < nodes.size(); i++ )
		{
			JsonObject object = nodes.getJsonObject(i);
			if( object.hasProperty("children") )
			{
				Node node = aContext.getNode(object.getInt("id"));
				JsonArray children = object.getJsonArray("children");
				for( int j = 0; j < children.size(); j++ )
				{
					node.addChild(aContext.getNode(children.getInt(j)));
				}
			}
		}
	}

	/*
	 * Extracts information about nodes from pObject and creates new objects to
	 * represent them. throws Deserialization Exception
	 */
	private void decodeEdges()
	{
		JsonArray edges = extractArray(PROPERTY_EDGES);
		for( int i = 0; i < edges.size(); i++ )
		{
			try
			{
				JsonObject object = edges.getJsonObject(i);
				Class<?> edgeClass = Class.forName(PREFIX_EDGES + object.getString("type"));
				Edge edge = (Edge) edgeClass.getDeclaredConstructor().newInstance();

				for( Property property : edge.properties() )
				{
					property.set(object.get(property.name().external()));
				}
				edge.connect(aContext.getNode(object.getInt("start")), aContext.getNode(object.getInt("end")));
				aContext.pDiagram().addEdge(edge);
			}
			catch (ReflectiveOperationException exception)
			{
				throw new DeserializationException(Category.STRUCTURAL, "Cannot instantiate serialized object", exception);
			}
		}
	}
}
