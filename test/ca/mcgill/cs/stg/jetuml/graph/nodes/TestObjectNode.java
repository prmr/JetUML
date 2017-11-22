/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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
package ca.mcgill.cs.stg.jetuml.graph.nodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Direction;
import ca.mcgill.cs.stg.jetuml.geom.Point;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;

public class TestObjectNode
{
	private ObjectNode aObject1;
	private ObjectNode aObject2;
	private FieldNode aField1;
	private FieldNode aField2;
	
	@Before
	public void setup()
	{
		aObject1 = new ObjectNode();
		aObject2 = new ObjectNode();
		aField1 = new FieldNode();
		aField2 = new FieldNode();
	}
	
	@Test
	public void testDefault()
	{
		assertEquals(new Rectangle(0, 0, 80, 60), aObject1.getBounds());
		assertEquals(0,aObject1.getChildren().size());
		assertEquals(new Point(80,30), aObject1.view().getConnectionPoint(Direction.EAST));
		assertEquals(new Point(0,30), aObject1.view().getConnectionPoint(Direction.WEST));
		assertEquals(new Point(40,0), aObject1.view().getConnectionPoint(Direction.NORTH));
		assertEquals(new Point(40,60), aObject1.view().getConnectionPoint(Direction.SOUTH));
		assertEquals("", aObject1.getName().toString());
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
	
	@Test 
	public void testClone()
	{
		MultiLineString o1 = new MultiLineString();
		o1.setText("o1");
		aObject1.setName(o1);
		ObjectNode clone = aObject1.clone();
		assertEquals(new Rectangle(0, 0, 80, 60), clone.getBounds());
		assertEquals(0,clone.getChildren().size());
		assertEquals(new Point(80,30), clone.view().getConnectionPoint(Direction.EAST));
		assertEquals(new Point(0,30), clone.view().getConnectionPoint(Direction.WEST));
		assertEquals(new Point(40,0), clone.view().getConnectionPoint(Direction.NORTH));
		assertEquals(new Point(40,60), clone.view().getConnectionPoint(Direction.SOUTH));
		assertEquals("o1", clone.getName().toString());
		
		MultiLineString f1 = new MultiLineString();
		f1.setText("f1");
		aField1.setName(f1);
		MultiLineString f2 = new MultiLineString();
		f2.setText("f2");
		aField2.setName(f2);
		
		aObject1.addChild(aField1);
		aObject1.addChild(aField2);
		
		clone = aObject1.clone();
		assertEquals(2, clone.getChildren().size());
		
		FieldNode cf1 = (FieldNode) clone.getChildren().get(0);
		FieldNode cf2 = (FieldNode) clone.getChildren().get(1);

		assertEquals("f1", cf1.getName().toString());
		assertEquals("f2", cf2.getName().toString());
		assertFalse( cf1 == aField1 );
		assertFalse( cf2 == aField2 );
	}
}
