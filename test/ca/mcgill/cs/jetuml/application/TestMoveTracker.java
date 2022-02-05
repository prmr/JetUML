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
package ca.mcgill.cs.jetuml.application;

import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.assertThat;
import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.hasSize;
import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.isEmpty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.builder.CompoundOperation;
import ca.mcgill.cs.jetuml.diagram.builder.DiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.builder.DiagramOperation;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.gui.SelectionModel;

public class TestMoveTracker
{
	private Object aMoveTracker;
	private SelectionModel aSelection;
	private Diagram aDiagram;
	private ClassNode aNode1; // Initial bounds: [x=150.0,y=150.0,w=100.0,h=60.0]
	private ClassNode aNode2; // Initial bounds: [x=400.0,y=400.0,w=100.0,h=60.0]
	private DependencyEdge aEdge1;
	private Field aOperationsField;
	private DiagramBuilder aBuilder;
	
	private static Object createMoveTracker() throws ReflectiveOperationException
	{
		Constructor<?> constructor = Class.forName("ca.mcgill.cs.jetuml.gui.DiagramCanvasController$MoveTracker")
				.getDeclaredConstructor();
		constructor.setAccessible(true);
		return constructor.newInstance();
	}
	
	private void startTrackingMove(Iterable<DiagramElement> pSelectedElements) throws ReflectiveOperationException
	{
		Method method = Class.forName("ca.mcgill.cs.jetuml.gui.DiagramCanvasController$MoveTracker")
			.getDeclaredMethod("startTrackingMove", Iterable.class);
		method.setAccessible(true);
		method.invoke(aMoveTracker, pSelectedElements);
	}
	
	private CompoundOperation endTrackingMove(DiagramBuilder pDiagramBuilder) throws ReflectiveOperationException
	{
		Method method = Class.forName("ca.mcgill.cs.jetuml.gui.DiagramCanvasController$MoveTracker")
			.getDeclaredMethod("endTrackingMove", DiagramBuilder.class);
		method.setAccessible(true);
		return (CompoundOperation) method.invoke(aMoveTracker, pDiagramBuilder);
	}
	
	@BeforeEach
	void setup() throws ReflectiveOperationException
	{
		aMoveTracker = createMoveTracker();
		aSelection = new SelectionModel( () -> {} );
		aDiagram = new Diagram(DiagramType.CLASS);
		aNode1 = new ClassNode();
		aNode1.translate(150, 150);
		aNode2 = new ClassNode();
		aNode2.translate(400, 400);
		aEdge1 = new DependencyEdge();
		aEdge1.connect(aNode1, aNode1, aDiagram);
		aDiagram.addEdge(aEdge1);
		aOperationsField = CompoundOperation.class.getDeclaredField("aOperations");
		aOperationsField.setAccessible(true);
		aBuilder = DiagramType.newBuilderInstanceFor(aDiagram);
	}

	@Test
	void moveSingleObjectFourTimes() throws ReflectiveOperationException
	{
		aSelection.addToSelection(aNode1);
		startTrackingMove(aSelection);
		aNode1.translate(20, 20);
		aNode1.translate(0, 200);
		aNode1.translate(50, 50);
		CompoundOperation operation = endTrackingMove(aBuilder);
		
		assertThat(getOperations(operation), hasSize, 1);
		
		operation.undo();
		assertEquals(150, aNode1.position().getX());
		assertEquals(150, aNode1.position().getY());
		operation.execute();
		assertEquals(220, aNode1.position().getX());
		assertEquals(420, aNode1.position().getY());
		
		// No change in selection, move only X
		startTrackingMove(aSelection);
		aNode1.translate(200, 0);
		operation = endTrackingMove(aBuilder);
		assertThat(getOperations(operation), hasSize, 1);
		operation.undo();
		assertEquals(220, aNode1.position().getX());
		assertEquals(420, aNode1.position().getY());
		operation.execute();
		assertEquals(420, aNode1.position().getX());
		assertEquals(420, aNode1.position().getY());
		
		// No change in selection, move only Y
		startTrackingMove(aSelection);
		aNode1.translate(0, 200);
		operation = endTrackingMove(aBuilder);
		assertThat(getOperations(operation), hasSize, 1);
		operation.undo();
		assertEquals(420, aNode1.position().getX());
		assertEquals(420, aNode1.position().getY());
		operation.execute();
		assertEquals(420, aNode1.position().getX());
		assertEquals(620, aNode1.position().getY());
		
		// No change in selection, null move
		startTrackingMove(aSelection);
		aNode1.translate(0, 0);
		operation = endTrackingMove(aBuilder);
		assertThat(getOperations(operation), isEmpty );
	}
	
	@Test
	void moveNodesAndEdges() throws ReflectiveOperationException
	{
		aSelection.addToSelection(aNode1);
		aSelection.addToSelection(aNode2);
		aSelection.addToSelection(aEdge1);
		startTrackingMove(aSelection);
		aNode1.translate(20, 20);
		aNode2.translate(20, 20);
		CompoundOperation operation = endTrackingMove(aBuilder);
		List<DiagramOperation> operations = getOperations(operation);
		assertThat(operations, hasSize, 2);
		
		operations.get(0).undo();
		assertEquals(150, aNode1.position().getX());
		assertEquals(150, aNode1.position().getY());
		assertEquals(420, aNode2.position().getX());
		assertEquals(420, aNode2.position().getY());
		operations.get(0).execute();
		assertEquals(170, aNode1.position().getX());
		assertEquals(170, aNode1.position().getY());
		assertEquals(420, aNode2.position().getX());
		assertEquals(420, aNode2.position().getY());
		
		operations.get(1).undo();
		assertEquals(170, aNode1.position().getX());
		assertEquals(170, aNode1.position().getY());
		assertEquals(400, aNode2.position().getX());
		assertEquals(400, aNode2.position().getY());
		operations.get(1).execute();
		assertEquals(170, aNode1.position().getX());
		assertEquals(170, aNode1.position().getY());
		assertEquals(420, aNode2.position().getX());
		assertEquals(420, aNode2.position().getY());

		// Second identical move
		startTrackingMove(aSelection);
		aNode1.translate(20, 20);
		aNode2.translate(20, 20);
		operation = endTrackingMove(aBuilder);
		
		operations = getOperations(operation);
		assertThat(operations, hasSize, 2);
		
		operations.get(0).undo();
		assertEquals(170, aNode1.position().getX());
		assertEquals(170, aNode1.position().getY());
		assertEquals(440, aNode2.position().getX());
		assertEquals(440, aNode2.position().getY());
		operations.get(0).execute();
		assertEquals(190, aNode1.position().getX());
		assertEquals(190, aNode1.position().getY());
		assertEquals(440, aNode2.position().getX());
		assertEquals(440, aNode2.position().getY());
		
		operations.get(1).undo();
		assertEquals(190, aNode1.position().getX());
		assertEquals(190, aNode1.position().getY());
		assertEquals(420, aNode2.position().getX());
		assertEquals(420, aNode2.position().getY());
		operations.get(1).execute();
		assertEquals(190, aNode1.position().getX());
		assertEquals(190, aNode1.position().getY());
		assertEquals(440, aNode2.position().getX());
		assertEquals(440, aNode2.position().getY());
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
