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
 * 
 * The version information stored in a diagram file is purposefully discarded 
 * as JetUML no longer migrates versions. Storing and handling version numbers
 * is deemed not to be worth the complexity. In very rare cases were an decoding
 * issue might be due to versioning, users can always look at the text of the 
 * diagram file to recover the version number.
 */
public final class JsonDecoder
{
	private static final String PREFIX_NODES = "org.jetuml.diagram.nodes.";
	private static final String PREFIX_EDGES = "org.jetuml.diagram.edges.";
	
	private static final String PROPERTY_DIAGRAM = "diagram";
	private static final String PROPERTY_NODES = "nodes";
	private static final String PROPERTY_EDGES = "edges";
	private static final String PROPERTY_VERSION = "version";
	private static final String PROPERTY_TYPE = "type";
	private static final String PROPERTY_X = "x";
	private static final String PROPERTY_Y = "y";
	private static final String PROPERTY_ID = "id";
	private static final String PROPERTY_CHILDREN = "children";
	private static final String PROPERTY_START = "start";
	private static final String PROPERTY_END = "end";

	/* 
	 * The object that will be decoded.
	 */
	private final JsonObject aInputObject;
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
			return aContext.diagram();
		}
		catch( JsonException exception )
		{
			// Just to be extra safe, but normally all exceptions should be 
			// handled in the individual methods of the algorithm.
			throw new DeserializationException(Category.STRUCTURAL, exception.getMessage());
		}
	}
	
	private void extractVersion()
	{
		try
		{
			// We make sure that the version number can be parse as an integrity check on the 
			// diagram file, but we do not store the information.
			Version.parse(extractString(PROPERTY_VERSION));
		}
		catch(IllegalArgumentException exception)
		{
			throw new DeserializationException(Category.STRUCTURAL, "Cannot parse version number");
		}
	}
	
	private void extractDiagram()
	{
		try
		{
			aContext = new DeserializationContext(new Diagram(DiagramType.fromName(extractString(PROPERTY_DIAGRAM))));
		}
		catch(IllegalArgumentException exception)
		{
			throw new DeserializationException(Category.STRUCTURAL, "Invalid diagram type: " + extractString(PROPERTY_DIAGRAM));
		}
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
				Class<?> nodeClass = Class.forName(PREFIX_NODES + object.getString(PROPERTY_TYPE));
				Node node = (Node) nodeClass.getDeclaredConstructor().newInstance();
				node.moveTo(new Point(object.getInt(PROPERTY_X), object.getInt(PROPERTY_Y)));
				for( Property property : node.properties() )
				{
					property.set(object.get(property.name().external()));
				}
				aContext.addNode(node, object.getInt(PROPERTY_ID));
			}
			catch(ReflectiveOperationException | JsonException exception)
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
				aContext.diagram().addRootNode(node);
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
			if( object.hasProperty(PROPERTY_CHILDREN) )
			{
				Node node = aContext.getNode(object.getInt(PROPERTY_ID));
				JsonArray children = object.getJsonArray(PROPERTY_CHILDREN);
				for( int j = 0; j < children.size(); j++ )
				{
					int childNodeId = children.getInt(j);
					if( !aContext.idExists(childNodeId))
					{
						throw new DeserializationException(Category.STRUCTURAL, "Invalid node id found in children nodes");
					}
					Node childNode = aContext.getNode(childNodeId);
					if( !node.allowsAsChild(childNode) )
					{
						throw new DeserializationException(Category.STRUCTURAL, "Invalid parent-child relation");
					}
					node.addChild(aContext.getNode(childNodeId));
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
				Class<?> edgeClass = Class.forName(PREFIX_EDGES + object.getString(PROPERTY_TYPE));
				Edge edge = (Edge) edgeClass.getDeclaredConstructor().newInstance();

				for( Property property : edge.properties() )
				{
					property.set(object.get(property.name().external()));
				}
				int startNodeId = object.getInt(PROPERTY_START);
				int endNodeId = object.getInt(PROPERTY_END);
				if( !aContext.idExists(startNodeId) || !aContext.idExists(endNodeId))
				{
					throw new DeserializationException(Category.STRUCTURAL, "At least one edge vertex cannot be found");
				}
				edge.connect(aContext.getNode(startNodeId), aContext.getNode(endNodeId));
				aContext.diagram().addEdge(edge);
			}
			catch (ReflectiveOperationException exception)
			{
				throw new DeserializationException(Category.STRUCTURAL, "Cannot instantiate serialized object", exception);
			}
		}
	}
}
