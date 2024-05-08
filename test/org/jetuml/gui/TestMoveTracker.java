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
package org.jetuml.gui;

import static java.util.Arrays.asList;
import static org.jetuml.testutils.CollectionAssertions.assertThat;
import static org.jetuml.testutils.CollectionAssertions.hasSize;
import static org.jetuml.testutils.CollectionAssertions.isEmpty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.builder.CompoundOperation;
import org.jetuml.diagram.builder.DiagramOperation;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.geom.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestMoveTracker
{
	// A stub that returns a 40x60 rectangle with origin at the node's position
	private static final Function<Node, Rectangle> BOUND_CALCULATOR_STUB =
			(node) -> new Rectangle(node.position().x(), node.position().y(), 40, 60);
	
	private MoveTracker aTracker = new MoveTracker(BOUND_CALCULATOR_STUB);
	private Diagram aDiagram = new Diagram(DiagramType.CLASS);;
	private ClassNode aNode1; // Initial bounds: [x=150.0,y=150.0,w=100.0,h=60.0]
	private ClassNode aNode2; // Initial bounds: [x=400.0,y=400.0,w=100.0,h=60.0]
	private DependencyEdge aEdge1;
	private Field aOperationsField;
	
	@BeforeEach
	void setup() throws ReflectiveOperationException
	{
		aNode1 = new ClassNode();
		aNode1.translate(150, 150);
		aNode2 = new ClassNode();
		aNode2.translate(400, 400);
		aEdge1 = new DependencyEdge();
		aEdge1.connect(aNode1, aNode1);
		aDiagram.addEdge(aEdge1);
		aOperationsField = CompoundOperation.class.getDeclaredField("aOperations");
		aOperationsField.setAccessible(true);
	}

	@Test
	void moveSingleObjectFourTimes()
	{
		aTracker.start(asList(aNode1));
		aNode1.translate(20, 20);
		aNode1.translate(0, 200);
		aNode1.translate(50, 50);
		CompoundOperation operation = aTracker.stop();
		
		assertThat(getOperations(operation), hasSize, 1);
		
		operation.undo();
		assertEquals(150, aNode1.position().x());
		assertEquals(150, aNode1.position().y());
		operation.execute();
		assertEquals(220, aNode1.position().x());
		assertEquals(420, aNode1.position().y());
		
		// No change in selection, move only X
		aTracker.start(asList(aNode1));
		aNode1.translate(200, 0);
		operation = aTracker.stop();
		assertThat(getOperations(operation), hasSize, 1);
		operation.undo();
		assertEquals(220, aNode1.position().x());
		assertEquals(420, aNode1.position().y());
		operation.execute();
		assertEquals(420, aNode1.position().x());
		assertEquals(420, aNode1.position().y());
		
		// No change in selection, move only Y
		aTracker.start(asList(aNode1));
		aNode1.translate(0, 200);
		operation = aTracker.stop();
		assertThat(getOperations(operation), hasSize, 1);
		operation.undo();
		assertEquals(420, aNode1.position().x());
		assertEquals(420, aNode1.position().y());
		operation.execute();
		assertEquals(420, aNode1.position().x());
		assertEquals(620, aNode1.position().y());
		
		// No change in selection, null move
		aTracker.start(asList(aNode1));
		aNode1.translate(0, 0);
		operation = aTracker.stop();
		assertThat(getOperations(operation), isEmpty );
	}
	
	@Test
	void moveNodesAndEdges()
	{
		aTracker.start(asList(aNode1, aNode2, aEdge1));
		aNode1.translate(20, 20);
		aNode2.translate(20, 20);
		CompoundOperation operation = aTracker.stop();
		List<DiagramOperation> operations = getOperations(operation);
		assertThat(operations, hasSize, 2);
		
		operations.get(0).undo();
		assertEquals(150, aNode1.position().x());
		assertEquals(150, aNode1.position().y());
		assertEquals(420, aNode2.position().x());
		assertEquals(420, aNode2.position().y());
		operations.get(0).execute();
		assertEquals(170, aNode1.position().x());
		assertEquals(170, aNode1.position().y());
		assertEquals(420, aNode2.position().x());
		assertEquals(420, aNode2.position().y());
		
		operations.get(1).undo();
		assertEquals(170, aNode1.position().x());
		assertEquals(170, aNode1.position().y());
		assertEquals(400, aNode2.position().x());
		assertEquals(400, aNode2.position().y());
		operations.get(1).execute();
		assertEquals(170, aNode1.position().x());
		assertEquals(170, aNode1.position().y());
		assertEquals(420, aNode2.position().x());
		assertEquals(420, aNode2.position().y());

		// Second identical move
		aTracker.start(asList(aNode1, aNode2, aEdge1));
		aNode1.translate(20, 20);
		aNode2.translate(20, 20);
		operation = aTracker.stop();
		
		operations = getOperations(operation);
		assertThat(operations, hasSize, 2);
		
		operations.get(0).undo();
		assertEquals(170, aNode1.position().x());
		assertEquals(170, aNode1.position().y());
		assertEquals(440, aNode2.position().x());
		assertEquals(440, aNode2.position().y());
		operations.get(0).execute();
		assertEquals(190, aNode1.position().x());
		assertEquals(190, aNode1.position().y());
		assertEquals(440, aNode2.position().x());
		assertEquals(440, aNode2.position().y());
		
		operations.get(1).undo();
		assertEquals(190, aNode1.position().x());
		assertEquals(190, aNode1.position().y());
		assertEquals(420, aNode2.position().x());
		assertEquals(420, aNode2.position().y());
		operations.get(1).execute();
		assertEquals(190, aNode1.position().x());
		assertEquals(190, aNode1.position().y());
		assertEquals(440, aNode2.position().x());
		assertEquals(440, aNode2.position().y());
	}
	
	@SuppressWarnings("unchecked")
	private List<DiagramOperation> getOperations(CompoundOperation pOperation)
	{
		try
		{
			return (List<DiagramOperation>)aOperationsField.get(pOperation);
		}
		catch( ReflectiveOperationException pException )
		{
			fail();
			return null;
		}
	}
}
