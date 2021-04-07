/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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

package ca.mcgill.cs.jetuml.diagram.builder.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestConstraintSet
{
	private Set<String> aMessages;
	private DependencyEdge aEdge1;
	
	private Constraint constraint(String pMessage, boolean pReturn)
	{
		return (Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint, Diagram pDiagram)->
		{
			aMessages.add(pMessage);
			return pReturn;
		};
	}
	
	@BeforeEach
	public void setUp()
	{
		aMessages = new HashSet<>();
		aEdge1 = new DependencyEdge();
	}
	
	@Test
	public void testEmpty()
	{
		ConstraintSet constraints = new ConstraintSet();
		assertTrue(constraints.satisfied(aEdge1, new ClassNode(), new ClassNode(), new Point(0,0), new Point(0,0), new Diagram(DiagramType.CLASS)));
	}

	@Test
	public void testSatisfiedAllFalse()
	{
		ConstraintSet set1 = new ConstraintSet(constraint("X", false), constraint("Y", false), constraint("Z", false));
		assertFalse(set1.satisfied(aEdge1, new ClassNode(), new ClassNode(), new Point(0,0), new Point(0,0), new Diagram(DiagramType.CLASS)));
	}
	
	@Test
	public void testSatisfiedSomeFalse()
	{
		ConstraintSet set1 = new ConstraintSet(constraint("X", true), constraint("Y", true), constraint("Z", false));
		assertFalse(set1.satisfied(aEdge1, new ClassNode(), new ClassNode(), new Point(0,0), new Point(0,0), new Diagram(DiagramType.CLASS)));
	}
	
	@Test
	public void testSatisfiedTrue()
	{
		ConstraintSet set1 = new ConstraintSet(constraint("X", true), constraint("Y", true), constraint("Z", true));
		assertTrue(set1.satisfied(aEdge1, new ClassNode(), new ClassNode(), new Point(0,0), new Point(0,0), new Diagram(DiagramType.CLASS)));
	}
}
