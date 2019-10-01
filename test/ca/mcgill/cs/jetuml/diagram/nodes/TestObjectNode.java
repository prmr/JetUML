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
package ca.mcgill.cs.jetuml.diagram.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestObjectNode
{
	private ObjectNode aObject1;
	private ObjectNode aObject2;
	private FieldNode aField1;
	private FieldNode aField2;
	
	@BeforeEach
	public void setup()
	{
		aObject1 = new ObjectNode();
		aObject2 = new ObjectNode();
		aField1 = new FieldNode();
		aField2 = new FieldNode();
	}
	
	@Test
	public void testAddChild()
	{
		aObject1.addChild(aField1);
		assertEquals( 1, aObject1.getChildren().size());
		assertEquals( aObject1, aField1.getParent());
		assertEquals( aField1, aObject1.getChildren().get(0));
		
		aObject1.addChild(aField2);
		assertEquals( 2, aObject1.getChildren().size());
		assertEquals( aObject1, aField1.getParent());
		assertEquals( aObject1, aField2.getParent());
		assertEquals( aField1, aObject1.getChildren().get(0));
		assertEquals( aField2, aObject1.getChildren().get(1));
		
		// Move a field from one object to another
		aObject2.addChild(aField1);
		assertEquals( 1, aObject1.getChildren().size());
		assertEquals( aObject1, aField2.getParent());
		assertEquals( aField2, aObject1.getChildren().get(0));
		
		assertEquals( 1, aObject2.getChildren().size());
		assertEquals( aObject2, aField1.getParent());
		assertEquals( aField1, aObject2.getChildren().get(0));
	}
	
	@Test
	public void testRemoveChild()
	{
		aObject1.addChild(aField1);
		aObject1.addChild(aField2);
		
		aObject1.removeChild(aField1);
		assertEquals( 1, aObject1.getChildren().size());
		assertEquals( aField2, aObject1.getChildren().get(0));
		
		FieldNode field3 = new FieldNode();
		aObject1.removeChild(field3);
		assertEquals( 1, aObject1.getChildren().size());
		aObject2.addChild(field3);
		assertEquals( 1, aObject1.getChildren().size());
	}
	
	@Test
	public void testTranslateNoFields()
	{
		assertEquals(0, aObject1.position().getX());
		assertEquals(0, aObject1.position().getY());
		aObject1.translate(100, 200);
		assertEquals(100, aObject1.position().getX());
		assertEquals(200, aObject1.position().getY());
	}
	
	@Test
	public void testTranslateWithFields()
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
}
