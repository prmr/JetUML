/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2021 by McGill University.
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
package org.jetuml.viewers;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.jetuml.geom.Point;
import org.jetuml.rendering.RenderingFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestDiagramViewer
{
	private Diagram aDiagram = new Diagram(DiagramType.CLASS);
	
	@BeforeEach
	void setup()
	{
		RenderingFacade.prepareFor(aDiagram);
	}
	
	@Test
	void testNodeAt_NoneShallow()
	{
		aDiagram.addRootNode(new ClassNode());
		assertTrue(RenderingFacade.nodeAt(aDiagram, new Point(100,100)).isEmpty());
	}
	
	@Test
	void testNodeAt_NoneDeep()
	{
		PackageNode p1 = new PackageNode();
		PackageNode p2 = new PackageNode();
		ClassNode c = new ClassNode();
		p2.addChild(c);
		p1.addChild(p2);
		aDiagram.addRootNode(p1);
		assertTrue(RenderingFacade.nodeAt(aDiagram, new Point(100,100)).isEmpty());
	}
	
	@Test
	void testNodeAt_FoundLevel1WhenNoOther()
	{
		ClassNode node = new ClassNode();
		aDiagram.addRootNode(node);
		assertSame(node, RenderingFacade.nodeAt(aDiagram, new Point(20,20)).get());
	}
	
	@Test
	void testNodeAt_FoundLevel1WhenOther()
	{
		PackageNode p1 = new PackageNode();
		ClassNode node = new ClassNode();
		node.translate(10, 10);
		p1.addChild(node);
		aDiagram.addRootNode(p1);
		assertSame(p1, RenderingFacade.nodeAt(aDiagram, new Point(5,5)).get());
	}
	
	@Test
	void testNodeAt_FoundLevel2WhenNoOther()
	{
		PackageNode p1 = new PackageNode();
		ClassNode node = new ClassNode();
		node.translate(10, 10);
		p1.addChild(node);
		aDiagram.addRootNode(p1);
		assertSame(node, RenderingFacade.nodeAt(aDiagram, new Point(15,15)).get());
	}
	
	@Test
	void testNodeAt_FoundLevel2WhenOther()
	{
		PackageNode p1 = new PackageNode();
		PackageNode p2 = new PackageNode();
		ClassNode node = new ClassNode();
		p1.addChild(p2);
		p1.translate(10, 10);
		p2.addChild(node);
		node.translate(20, 20);
		p1.addChild(node);
		aDiagram.addRootNode(p1);
		assertSame(p2, RenderingFacade.nodeAt(aDiagram, new Point(15,15)).get());
	}
}
