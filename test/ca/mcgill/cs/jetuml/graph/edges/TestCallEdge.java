package ca.mcgill.cs.jetuml.graph.edges;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestCallEdge
{
	@Test
	public void testGetWithProperty()
	{
		CallEdge edge = new CallEdge();
		assertFalse(edge.isSignal());
		edge.setSignal(true);
		assertTrue(edge.isSignal());
		assertTrue((boolean) edge.properties().get("signal").get());
		edge.properties().get("signal").set(false);
		assertFalse((boolean) edge.properties().get("signal").get());
		assertFalse(edge.isSignal());
		
		edge.properties().get("middleLabel").set("Foo");
		assertEquals("Foo", edge.getMiddleLabel());
	}
	
	@Test
	public void testGetWithPropertyAndClone()
	{
		CallEdge edge = new CallEdge();
		CallEdge clone = (CallEdge) edge.clone();
		
		edge.properties().get("middleLabel").set("Foo");
		
		assertEquals("Foo", edge.properties().get("middleLabel").get());
		assertEquals("", clone.properties().get("middleLabel").get());
	}
}
