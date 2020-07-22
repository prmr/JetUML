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
package ca.mcgill.cs.jetuml.diagram.edges;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
