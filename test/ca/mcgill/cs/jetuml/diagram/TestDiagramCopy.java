/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2019 by the contributors of the JetUML project.
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.diagram;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;

/**
 * For testing method copy of class Diagram.
 */
public class TestDiagramCopy
{
	private Diagram aClassDiagram;
	
	@BeforeEach
	public void setUp()
	{
		aClassDiagram = new Diagram(DiagramType.CLASS);
	}
	
	@Test
	public void test_empty()
	{
		assertFalse(aClassDiagram.copy().edges().iterator().hasNext());
		assertFalse(aClassDiagram.copy().rootNodes().iterator().hasNext());
	}
	
	@Test
	public void test_NoEdges()
	{
		aClassDiagram.addRootNode(new ClassNode());
		Diagram copy = aClassDiagram.copy();
		assertFalse(aClassDiagram.copy().edges().iterator().hasNext());
		assertTrue(copy.rootNodes().iterator().hasNext());
		Node node = copy.rootNodes().iterator().next();
		assertNotSame(aClassDiagram.rootNodes().iterator().next(), node);
	}
}
