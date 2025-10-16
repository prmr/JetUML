/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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
package org.jetuml.diagram.edges;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.PropertyName;
import org.junit.jupiter.api.Test;

public class CallEdgeTest {
	
	private final CallEdge aCallEdge = new CallEdge();

	@Test
	void testGetWithProperty() {
		assertFalse(aCallEdge.isSignal());
		aCallEdge.setSignal(true);
		assertTrue(aCallEdge.isSignal());
		assertTrue((boolean) aCallEdge.properties().get(PropertyName.SIGNAL).get());
		aCallEdge.properties().get(PropertyName.SIGNAL).set(false);
		assertFalse((boolean) aCallEdge.properties().get(PropertyName.SIGNAL).get());
		assertFalse(aCallEdge.isSignal());

		aCallEdge.properties().get(PropertyName.MIDDLE_LABEL).set("Foo");
		assertEquals("Foo", aCallEdge.getMiddleLabel());
	}

	@Test
	void testGetWithPropertyAndClone() {
		CallEdge clone = (CallEdge) aCallEdge.clone();

		aCallEdge.properties().get(PropertyName.MIDDLE_LABEL).set("Foo");

		assertEquals("Foo", aCallEdge.properties().get(PropertyName.MIDDLE_LABEL).get());
		assertEquals("", clone.properties().get(PropertyName.MIDDLE_LABEL).get());
	}
}
