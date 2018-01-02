package ca.mcgill.cs.jetuml.graph.nodes;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.graph.Properties;

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
	public void testGetProperties()
	{
		Properties properties = aNode.properties();
		
		assertEquals("Actor", properties.get("name"));
		assertEquals(0, properties.get("x"));
		assertEquals(0, properties.get("y"));
		
		aNode.setName("Foo");
		aNode.translate(10, 20);
		properties = aNode.properties();
		assertEquals("Foo", properties.get("name"));
		assertEquals(10, properties.get("x"));
		assertEquals(20, properties.get("y"));
	}
}
