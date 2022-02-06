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
package ca.mcgill.cs.jetuml.views;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.viewers.DiagramViewer;

public class TestDiagramViewer
{
	private DiagramViewer aViewer = new DiagramViewer();
	
	@Test
	public void testNodeAt_NoneShallow()
	{
		Diagram diagram = new Diagram(DiagramType.CLASS);
		diagram.addRootNode(new ClassNode());
		assertTrue(aViewer.nodeAt(diagram, new Point(100,100)).isEmpty());
	}
	
	@Test
	public void testNodeAt_NoneDeep()
	{
		Diagram diagram = new Diagram(DiagramType.CLASS);
		PackageNode p1 = new PackageNode();
		PackageNode p2 = new PackageNode();
		ClassNode c = new ClassNode();
		p2.addChild(c);
		p1.addChild(p2);
		diagram.addRootNode(p1);
		assertTrue(aViewer.nodeAt(diagram, new Point(100,100)).isEmpty());
	}
	
	@Test
	public void testNodeAt_FoundLevel1WhenNoOther()
	{
		Diagram diagram = new Diagram(DiagramType.CLASS);
		ClassNode node = new ClassNode();
		diagram.addRootNode(node);
		assertSame(node, aViewer.nodeAt(diagram, new Point(20,20)).get());
	}
	
	@Test
	public void testNodeAt_FoundLevel1WhenOther()
	{
		Diagram diagram = new Diagram(DiagramType.CLASS);
		PackageNode p1 = new PackageNode();
		ClassNode node = new ClassNode();
		node.translate(10, 10);
		p1.addChild(node);
		diagram.addRootNode(p1);
		assertSame(p1, aViewer.nodeAt(diagram, new Point(5,5)).get());
	}
	
	@Test
	public void testNodeAt_FoundLevel2WhenNoOther()
	{
		Diagram diagram = new Diagram(DiagramType.CLASS);
		PackageNode p1 = new PackageNode();
		ClassNode node = new ClassNode();
		node.translate(10, 10);
		p1.addChild(node);
		diagram.addRootNode(p1);
		assertSame(node, aViewer.nodeAt(diagram, new Point(15,15)).get());
	}
	
	@Test
	public void testNodeAt_FoundLevel2WhenOther()
	{
		Diagram diagram = new Diagram(DiagramType.CLASS);
		PackageNode p1 = new PackageNode();
		PackageNode p2 = new PackageNode();
		ClassNode node = new ClassNode();
		p1.addChild(p2);
		p1.translate(10, 10);
		p2.addChild(node);
		node.translate(20, 20);
		p1.addChild(node);
		diagram.addRootNode(p1);
		assertSame(p2, aViewer.nodeAt(diagram, new Point(15,15)).get());
	}
}
