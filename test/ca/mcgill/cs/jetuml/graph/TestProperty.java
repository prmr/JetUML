package ca.mcgill.cs.jetuml.graph;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestProperty
{
	@Test
	public void testProperty()
	{
		class Stub { String aValue = "value"; }
		Stub stub = new Stub();
		Property property = new Property("test", () -> stub.aValue, newval -> stub.aValue = (String) newval, true);
		assertEquals("test", property.getName());
		assertEquals("value", property.get());
		assertTrue(property.isVisible());
		
		property.set("foo");
		assertEquals("foo", property.get());
	}
}
