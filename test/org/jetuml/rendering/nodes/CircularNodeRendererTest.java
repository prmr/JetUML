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
package org.jetuml.rendering.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.InitialStateNode;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.junit.jupiter.api.Test;

public class CircularNodeRendererTest {

	private FinalStateNode aFinal = new FinalStateNode();
	private InitialStateNode aInitial = new InitialStateNode();
	private final CircularStateNodeRenderer aFinalRenderer = new CircularStateNodeRenderer(
			DiagramType.newRendererInstanceFor(new Diagram(DiagramType.STATE)), true);
	private final CircularStateNodeRenderer aInitialRenderer = new CircularStateNodeRenderer(
			DiagramType.newRendererInstanceFor(new Diagram(DiagramType.STATE)), false);

	@Test
	void testGetBounds_NoName() {
		assertEquals(new Rectangle(0, 0, 20, 20), aFinalRenderer.getBounds(aFinal));
		assertEquals(new Rectangle(0, 0, 20, 20), aInitialRenderer.getBounds(aInitial));
	}

	@Test
	void testGetConnectionPoint_North() {
		assertEquals(new Point(10, 0), aFinalRenderer.getConnectionPoint(aFinal, Direction.NORTH));
	}

	@Test
	void testGetConnectionPoint_East() {
		assertEquals(new Point(20, 10), aFinalRenderer.getConnectionPoint(aFinal, Direction.EAST));
	}

	@Test
	void testGetConnectionPoint_South() {
		assertEquals(new Point(10, 20), aFinalRenderer.getConnectionPoint(aFinal, Direction.SOUTH));
	}

	@Test
	void testGetConnectionPoint_West() {
		assertEquals(new Point(0, 10), aFinalRenderer.getConnectionPoint(aFinal, Direction.WEST));
	}

	@Test
	void testGetConnectionPoint_NE() {
		assertEquals(new Point(19, 7), aFinalRenderer.getConnectionPoint(aFinal, Direction.fromAngle(70)));
	}

	@Test
	void testGetConnectionPoint_SE() {
		assertEquals(new Point(19, 13), aFinalRenderer.getConnectionPoint(aFinal, Direction.fromAngle(110)));
	}

	@Test
	void testGetConnectionPoint_SW() {
		assertEquals(new Point(3, 17), aFinalRenderer.getConnectionPoint(aFinal, Direction.fromAngle(225)));
	}

	@Test
	void testGetConnectionPoint_NW() {
		assertEquals(new Point(2, 4), aFinalRenderer.getConnectionPoint(aFinal, Direction.fromAngle(305)));
	}
}
