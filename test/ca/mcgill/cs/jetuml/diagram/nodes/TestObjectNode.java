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
package ca.mcgill.cs.jetuml.diagram.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;

public class TestObjectNode
{
	private ObjectNode aObject1;
	private ObjectNode aObject2;
	private FieldNode aField1;
	private FieldNode aField2;
	private Diagram aDiagram;
	
	
	@BeforeEach
	public void setup()
	{
		aDiagram = new Diagram(DiagramType.OBJECT);
		aObject1 = new ObjectNode();
		aObject2 = new ObjectNode();
		aField1 = new FieldNode();
		aField2 = new FieldNode();
	}
	
	@Test
	public void testTranslate_NoFields()
	{
		assertEquals(0, aObject1.position().getX());
		assertEquals(0, aObject1.position().getY());
		aObject1.translate(100, 200);
		assertEquals(100, aObject1.position().getX());
		assertEquals(200, aObject1.position().getY());
	}
	
	@Test
	public void testTranslate_WithFields()
	{
		aObject1.addChild(aField1);
		aObject1.addChild(aField2);
		assertEquals(0, aObject1.position().getX());
		assertEquals(0, aObject1.position().getY());
		assertEquals(0, aField1.position().getX());
		assertEquals(0, aField1.position().getY());
		assertEquals(0, aField2.position().getX());
		assertEquals(0, aField2.position().getY());
		aObject1.translate(100, 200);
		assertEquals(100, aObject1.position().getX());
		assertEquals(200, aObject1.position().getY());
		assertEquals(100, aField1.position().getX());
		assertEquals(200, aField1.position().getY());
		assertEquals(100, aField2.position().getX());
		assertEquals(200, aField2.position().getY());
	}
	
	@Test
	public void testClone_NoFields()
	{
		aObject1.setName("Test");
		aObject1.attach(aDiagram);
		ObjectNode clone = aObject1.clone();
		assertNotSame(aObject1, clone);
		assertEquals(aObject1.getName(), clone.getName());
		assertSame(aObject1.getDiagram().get(), clone.getDiagram().get());
	}
	
	@Test
	public void testClone_WithFields()
	{
		aObject1.setName("Test");
		aObject1.attach(aDiagram);
		aObject1.addChild(aField1);
		aObject1.addChild(aField2);
		aField1.attach(aDiagram);
		aField2.attach(aDiagram);
		ObjectNode clone = aObject1.clone();
		assertNotSame(aObject1, clone);
		assertEquals(aObject1.getName(), clone.getName());
		assertSame(aObject1.getDiagram().get(), clone.getDiagram().get());
		assertNotSame(aObject1.getChildren().get(0), clone.getChildren().get(0));
		assertNotSame(aObject1.getChildren().get(1), clone.getChildren().get(1));
		assertSame(aObject1.getChildren().get(0).getDiagram().get(), clone.getChildren().get(0).getDiagram().get());
		assertSame(aObject1.getChildren().get(1).getDiagram().get(), clone.getChildren().get(1).getDiagram().get());
	}
	
	@Test
	public void testAddChild()
	{
		aObject1.addChild(aField1);
		assertEquals( 1, aObject1.getChildren().size());
		assertSame( aObject1, aField1.getParent());
		assertSame( aField1, aObject1.getChildren().get(0));
		
		aObject1.addChild(aField2);
		assertEquals( 2, aObject1.getChildren().size());
		assertSame( aObject1, aField1.getParent());
		assertSame( aObject1, aField2.getParent());
		assertSame( aField1, aObject1.getChildren().get(0));
		assertSame( aField2, aObject1.getChildren().get(1));
		
		// Move a field from one object to another
		aObject2.addChild(aField1);
		assertEquals( 1, aObject1.getChildren().size());
		assertSame( aObject1, aField2.getParent());
		assertSame( aField2, aObject1.getChildren().get(0));
		
		assertEquals( 1, aObject2.getChildren().size());
		assertSame( aObject2, aField1.getParent());
		assertSame( aField1, aObject2.getChildren().get(0));
	}
	
	@Test
	public void testAddChild_Int_ChildNode()
	{
		aObject1.addChild(aField1);
		assertEquals( 1, aObject1.getChildren().size());
		assertEquals( aObject1, aField1.getParent());
		assertEquals( aField1, aObject1.getChildren().get(0));
		
		aObject1.addChild(0, aField2);
		assertEquals( 2, aObject1.getChildren().size());
		assertSame(aField2, aObject1.getChildren().get(0));
		assertSame(aField1, aObject1.getChildren().get(1));
		
		FieldNode field3 = new FieldNode();
		aObject1.addChild(1, field3);
		assertEquals( 3, aObject1.getChildren().size());
		assertSame(aField2, aObject1.getChildren().get(0));
		assertSame(field3, aObject1.getChildren().get(1));
		assertSame(aField1, aObject1.getChildren().get(2));
	}
	
	@Test
	public void testRemoveChild()
	{
		aObject1.addChild(aField1);
		aObject1.addChild(aField2);
		
		aObject1.removeChild(aField1);
		assertEquals( 1, aObject1.getChildren().size());
		assertEquals( aField2, aObject1.getChildren().get(0));
	}
}
