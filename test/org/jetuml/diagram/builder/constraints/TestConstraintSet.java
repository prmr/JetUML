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

package org.jetuml.diagram.builder.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.geom.Point;
import org.jetuml.rendering.DiagramRenderer;
import org.junit.jupiter.api.Test;

public class TestConstraintSet
{
	private DependencyEdge aEdge1 = new DependencyEdge();
	private DiagramRenderer aRenderer = DiagramType.newRendererInstanceFor(new Diagram(DiagramType.CLASS));
	
	private Constraint createStubConstraint(boolean pReturn)
	{
		return (Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint, DiagramRenderer pRenderer)->
		{
			return pReturn;
		};
	}
	
	@Test
	void testEmpty()
	{
		ConstraintSet constraints = new ConstraintSet();
		assertTrue(constraints.satisfied(aEdge1, new ClassNode(), new ClassNode(), new Point(0,0), new Point(0,0), aRenderer));
	}

	@Test
	void testSatisfiedAllFalse()
	{
		ConstraintSet set1 = new ConstraintSet(createStubConstraint(false), createStubConstraint(false), createStubConstraint(false));
		assertFalse(set1.satisfied(aEdge1, new ClassNode(), new ClassNode(), new Point(0,0), new Point(0,0), aRenderer));
	}
	
	@Test
	void testSatisfiedSomeFalse()
	{
		ConstraintSet set1 = new ConstraintSet(createStubConstraint(true), createStubConstraint(true), createStubConstraint(false));
		assertFalse(set1.satisfied(aEdge1, new ClassNode(), new ClassNode(), new Point(0,0), new Point(0,0), aRenderer));
	}
	
	@Test
	void testSatisfiedTrue()
	{
		ConstraintSet set1 = new ConstraintSet(createStubConstraint(true), createStubConstraint(true), createStubConstraint(true));
		assertTrue(set1.satisfied(aEdge1, new ClassNode(), new ClassNode(), new Point(0,0), new Point(0,0), aRenderer));
	}
}
