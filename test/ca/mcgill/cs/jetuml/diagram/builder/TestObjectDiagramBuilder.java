/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.diagram.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.ObjectDiagram;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;

public class TestObjectDiagramBuilder
{
	private ObjectDiagram aDiagram;
	private ObjectDiagramBuilder aBuilder;
	private ObjectNode aObjectNode1;
	private FieldNode aFieldNode1;
	private FieldNode aFieldNode2;
	
	/**
	 * Load JavaFX toolkit and environment.
	 */
	@BeforeClass
	@SuppressWarnings("unused")
	public static void setupClass()
	{
		JavaFXLoader loader = JavaFXLoader.instance();
	}
	
	@Before
	public void setUp()
	{
		aDiagram = new ObjectDiagram();
		aBuilder = new ObjectDiagramBuilder(aDiagram);
		aObjectNode1 = new ObjectNode();
		aFieldNode1 = new FieldNode();
		aFieldNode1.setName("Field1");
		aFieldNode2 = new FieldNode();
		aFieldNode1.setName("Field2");
	}		
		
	@Test
	public void testCreateRemoveElementsOperationFirstOfTwo()
	{
		aDiagram.addRootNode(aObjectNode1);
		aObjectNode1.addChild(aFieldNode1);
		aObjectNode1.addChild(aFieldNode2);
		assertEquals(2, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(1));

		DiagramOperation operation = aBuilder.createRemoveElementsOperation(Arrays.asList(aFieldNode1));
		operation.execute();
		
		assertEquals(1, aObjectNode1.getChildren().size());
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(0));
		
		operation.undo();
		
		assertEquals(2, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(1));
	}
	
	@Test
	public void testCreateRemoveElementsOperationSecondOfTwo()
	{
		aDiagram.addRootNode(aObjectNode1);
		aObjectNode1.addChild(aFieldNode1);
		aObjectNode1.addChild(aFieldNode2);
		assertEquals(2, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(1));

		DiagramOperation operation = aBuilder.createRemoveElementsOperation(Arrays.asList(aFieldNode2));
		operation.execute();
		
		assertEquals(1, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		
		operation.undo();
		
		assertEquals(2, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(1));
	}
	
	@Test
	public void testCreateRemoveElementsOperationTwoOfTwoInOrder()
	{
		aDiagram.addRootNode(aObjectNode1);
		aObjectNode1.addChild(aFieldNode1);
		aObjectNode1.addChild(aFieldNode2);
		assertEquals(2, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(1));

		DiagramOperation operation = aBuilder.createRemoveElementsOperation(Arrays.asList(aFieldNode1, aFieldNode2));
		operation.execute();
		
		assertEquals(0, aObjectNode1.getChildren().size());
		
		operation.undo();
		
		assertEquals(2, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(1));
	}
	
	@Test
	public void testCreateRemoveElementsOperationTwoOfTwoInReverseOrder()
	{
		aDiagram.addRootNode(aObjectNode1);
		aObjectNode1.addChild(aFieldNode1);
		aObjectNode1.addChild(aFieldNode2);
		assertEquals(2, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(1));

		DiagramOperation operation = aBuilder.createRemoveElementsOperation(Arrays.asList(aFieldNode2, aFieldNode1));
		operation.execute();
		
		assertEquals(0, aObjectNode1.getChildren().size());
		
		operation.undo();
		
		assertEquals(2, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(1));
	}
}
