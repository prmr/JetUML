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
package org.jetuml.rendering.edges;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.geom.Line;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.DiagramRenderer;
import org.junit.jupiter.api.Test;

public class NoteEdgeRendererTest {

	private NoteNode aNoteNode = new NoteNode(); // Bounds [x=0.0,y=0.0,w=60.0,h=40.0]
	private PointNode aPointNode = new PointNode();
	private NoteEdge aNoteEdge = new NoteEdge();
	private Diagram aDiagram = new Diagram(DiagramType.CLASS);;
	private DiagramRenderer aRenderer = DiagramType.newRendererInstanceFor(aDiagram);

	NoteEdgeRendererTest() {
		// Bounds[x=100,y=20,w=0.0,h=0.0]
		aPointNode.translate(100, 20);
	}

	@Test
	void testBasicConnection() {
		aNoteEdge.connect(aNoteNode, aPointNode);
		assertTrue(aNoteEdge.start() == aNoteNode);
		assertTrue(aNoteEdge.end() == aPointNode);
		aNoteEdge.connect(aPointNode, aNoteNode);
		assertTrue(aNoteEdge.start() == aPointNode);
		assertTrue(aNoteEdge.end() == aNoteNode);
	}

	@Test
	void testBoundsCalculation() {
		aNoteEdge.connect(aNoteNode, aPointNode);
		assertEquals(new Rectangle(59, 19, 42, 2), aRenderer.getBounds(aNoteEdge));

		Line connectionPoints = aRenderer.getConnectionPoints(aNoteEdge);
		assertEquals(60, connectionPoints.x1());
		assertEquals(20, connectionPoints.y1());
		assertEquals(100, connectionPoints.x2());
		assertEquals(20, connectionPoints.y2());

		aPointNode.translate(20, 0);
		assertEquals(new Rectangle(59, 19, 62, 2), aRenderer.getBounds(aNoteEdge));

		connectionPoints = aRenderer.getConnectionPoints(aNoteEdge);
		assertEquals(60, connectionPoints.x1());
		assertEquals(20, connectionPoints.y1());
		assertEquals(120, connectionPoints.x2());
		assertEquals(20, connectionPoints.y2());

		aPointNode.translate(0, 20); // Now at x=120, y = 40

		// The edge should intersect the note edge at x=58, y=24
		// (basic correspondence of proportions between triangles)
		// yielding bounds of [x=58,y=24,width=62,height=16]
		assertEquals(new Rectangle(58, 25, 62, 15), aRenderer.getBounds(aNoteEdge));

		connectionPoints = aRenderer.getConnectionPoints(aNoteEdge);
		assertEquals(60, connectionPoints.x1());
		assertEquals(27, connectionPoints.y1());
		assertEquals(120, connectionPoints.x2());
		assertEquals(40, connectionPoints.y2());
	}
}
