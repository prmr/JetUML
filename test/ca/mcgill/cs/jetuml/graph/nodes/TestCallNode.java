package ca.mcgill.cs.jetuml.graph.nodes;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.graph.Properties;

/**
 * @author Martin P. Robillard
 */
public class TestCallNode
{
	private CallNode aNode;
	
	@Before
	public void setup()
	{
		aNode = new CallNode();
	}
	
	@Test
	public void testGetProperties()
	{
		Properties properties = aNode.properties();
		
		assertEquals(false, properties.get("openBottom"));
		assertEquals(0, properties.get("x"));
		assertEquals(0, properties.get("y"));
		
		aNode.setOpenBottom(true);
		aNode.translate(10, 20);
		properties = aNode.properties();
		assertEquals(true, properties.get("openBottom"));
		assertEquals(10, properties.get("x"));
		assertEquals(20, properties.get("y"));
	}
}
