package ca.mcgill.cs.jetuml.graph.nodes;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.graph.nodes.ActorNode;
import ca.mcgill.cs.jetuml.persistence.Properties;

import static org.junit.Assert.*;

/**
 * @author Martin P. Robillard
 */
public class TestActorNode
{
	private ActorNode aNode;
	
	@Before
	public void setup()
	{
		aNode = new ActorNode();
	}
	
	@Test
	public void testGetPropertiesDefault()
	{
		Properties properties = aNode.properties();
		assertEquals("Actor", properties.get("name"));
		assertEquals(0, properties.get("x"));
		assertEquals(0, properties.get("y"));
	}
}
