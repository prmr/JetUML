/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
package ca.mcgill.cs.jetuml.persistence;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.Properties;
import ca.mcgill.cs.jetuml.diagram.Property;

/**
 * Utilities to facilitate writing tests for the persistence
 * classes.
 */
final class PersistenceTestUtils
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
			properties.add((String)pInput[i], () -> pInput[j+1], p -> {});
		}
		return properties;
	}
	
	static void assertHasKeys(JSONObject pObject, String... pKeys)
	{
		for( String key : pKeys )
		{
			assertTrue(pObject.has(key));
		}
	}
	
	/*
	 * Finds the object in an array with the specified properties
	 */
	static JSONObject find(JSONArray pArray, Properties pProperties)
	{
		JSONObject found = null;
		for( int i = 0; i < pArray.length(); i++ )
		{
			boolean match = true;
			JSONObject object = pArray.getJSONObject(i);
			for( Property property : pProperties )
			{
				if( !object.has(property.getName()))
				{
					match = false;
				}
				else
				{
					if(!object.get(property.getName()).equals(property.get()))
					{
						match = false;
					}
				}
			}
			if( match )
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
					if( !nodeProperties.get(property.getName()).get().equals(property.get()))
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
					if( !edgeProperties.get(property.getName()).get().equals(property.get()))
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
