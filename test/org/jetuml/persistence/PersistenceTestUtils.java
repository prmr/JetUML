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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.Properties;
import org.jetuml.diagram.Property;
import org.jetuml.diagram.PropertyName;
import org.jetuml.persistence.json.JsonArray;
import org.jetuml.persistence.json.JsonObject;

/**
 * Utilities to facilitate writing tests for the persistence
 * classes.
 */
public final class PersistenceTestUtils
{
	private PersistenceTestUtils() {}
	
	/**
	 * Creates a properties object with keys as even arguments and values as odd arguments.
	 */
	static Properties build(Object... pInput)
	{
		Properties properties = new Properties();
		for( int i = 0; i < pInput.length; i+=2 )
		{
			final int j = i;
			properties.add((PropertyName)pInput[i], () -> pInput[j+1], p -> {});
		}
		return properties;
	}
	
	static void assertHasKeys(JsonObject pObject, String... pKeys)
	{
		for( String key : pKeys )
		{
			assertTrue(pObject.hasProperty(key));
		}
	}
	
	/*
	 * Returns all the nodes in the diagram, both the root nodes
	 * and all their children.
	 */
	public static List<Node> getAllNodes(Diagram pDiagram)
	{
		List<Node> result = new ArrayList<>();
		for( Node node : pDiagram.rootNodes() )
		{
			result.addAll(getAllNodes(node));
		}
		return result;
	}
	
	/*
	 * Returns pNode and all its children.
	 */
	private static List<Node> getAllNodes(Node pNode)
	{
		List<Node> result = new ArrayList<>();
		result.add(pNode);
		for(Node child : pNode.getChildren() )
		{
			result.addAll(getAllNodes(child));
		}
		return result;
	}
	
	/*
	 * Finds the object in an array with the specified properties
	 */
	static JsonObject find(JsonArray pArray, String pType, Properties pProperties)
	{
		JsonObject found = null;
		for( int i = 0; i < pArray.size(); i++ )
		{
			boolean match = true;
			JsonObject object = pArray.getJsonObject(i);
			for( Property property : pProperties )
			{
				if( !object.hasProperty(property.name().external()))
				{
					match = false;
				}
				else
				{
					if(!object.get(property.name().external()).equals(property.get()))
					{
						match = false;
					}
				}
			}
			if( match && object.get("type").equals(pType) )
			{
				found = object;
				break;
			}
		}
		assertNotNull(found);
		return found;
	}
	
	static Node findRootNode(Diagram pDiagram, Class<?> pClass, Properties pProperties)
	{
		for( Node node : pDiagram.rootNodes() )
		{
			if( node.getClass() == pClass )
			{
				boolean match = true;
				Properties nodeProperties = node.properties();
				for( Property property : pProperties )
				{
					if( !nodeProperties.get(property.name()).get().equals(property.get()))
					{
						match = false;
						break;
					}
				}
				if( match )
				{
					return node;
				}
			}
		}
		fail("Expected node not found");
		return null;
	}
	
	static Node findRootNode(Diagram pDiagram, Class<?> pClass, int pX)
	{
		for( Node node : pDiagram.rootNodes() )
		{
			if( node.getClass() == pClass && node.position().x() == pX )
			{
				return node;
			}
		}
		fail("Expected node not found");
		return null;
	}
	
	static Edge findEdge(Diagram pDiagram, Class<?> pClass, Properties pProperties)
	{
		for( Edge edge : pDiagram.edges() )
		{
			if( edge.getClass() == pClass )
			{
				boolean match = true;
				Properties edgeProperties = edge.properties();
				for( Property property : pProperties )
				{
					if( !edgeProperties.get(property.name()).get().equals(property.get()))
					{
						match = false;
						break;
					}
				}
				if( match )
				{
					return edge;
				}
			}
		}
		fail("Expected edge not found");
		return null;
	}
}
