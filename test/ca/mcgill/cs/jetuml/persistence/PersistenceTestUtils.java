package ca.mcgill.cs.jetuml.persistence;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.Properties;

/**
 * Utilities to facilitate writing tests for the persistence
 * classes.
 * 
 * @author Martin P. Robillard
 *
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
			properties.put((String)pInput[i], () -> pInput[j+1], p -> {});
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
			for( String key : pProperties )
			{
				if( !object.has(key))
				{
					match = false;
				}
				else
				{
					if(!object.get(key).equals(pProperties.get(key)))
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
	
	static Node findRootNode(Graph pGraph, Class<?> pClass, Properties pProperties)
	{
		for( Node node : pGraph.getRootNodes() )
		{
			if( node.getClass() == pClass )
			{
				boolean match = true;
				Properties nodeProperties = node.properties();
				for( String key : pProperties )
				{
					if( !nodeProperties.get(key).equals(pProperties.get(key)))
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
	
	static Edge findEdge(Graph pGraph, Class<?> pClass, Properties pProperties)
	{
		for( Edge edge : pGraph.getEdges() )
		{
			if( edge.getClass() == pClass )
			{
				boolean match = true;
				Properties edgeProperties = edge.properties();
				for( String key : pProperties )
				{
					if( !edgeProperties.get(key).equals(pProperties.get(key)))
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
