package ca.mcgill.cs.jetuml.graph.edges;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.mcgill.cs.jetuml.graph.Properties;

public class TestCallEdge
{
	@Test
	public void testGetWithProperty()
	{
		CallEdge edge = new CallEdge();
		assertFalse(edge.isSignal());
		edge.setSignal(true);
		assertTrue(edge.isSignal());
		Properties properties = edge.properties();
		assertTrue((boolean) properties.get("signal"));
		properties.set("signal", false);
		assertFalse((boolean) properties.get("signal"));
		assertFalse(edge.isSignal());
		
		properties.set("middleLabel", "Foo");
		assertEquals("Foo", edge.getMiddleLabel());
	}
	
	@Test
	public void testGetWithPropertyAndClone()
	{
		CallEdge edge = new CallEdge();
		CallEdge clone = (CallEdge) edge.clone();
		
		Properties properties = edge.properties();
		properties.set("middleLabel", "Foo");
		
		assertEquals("Foo", edge.properties().get("middleLabel"));
		assertEquals("", clone.properties().get("middleLabel"));
	}
}
