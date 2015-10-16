/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 by the contributors of the JetUML project.
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
package ca.mcgill.cs.stg.jetuml.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.framework.Direction;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;

public class TestImplicitParameterNode
{
	private ImplicitParameterNode aObject1;
	private ImplicitParameterNode aObject2;
	private CallNode aCall1;
	private CallNode aCall2;
	
	@Before
	public void setup()
	{
		aObject1 = new ImplicitParameterNode();
		aObject2 = new ImplicitParameterNode();
		aCall1 = new CallNode();
		aCall2 = new CallNode();
	}
	
	@Test
	public void testDefault()
	{
		assertEquals(new Rectangle2D.Double(0, 0, 80, 120), aObject1.getBounds());
		assertEquals(0,aObject1.getChildren().size());
		assertEquals(new Point2D.Double(80,30), aObject1.getConnectionPoint(Direction.EAST));
		assertEquals(new Point2D.Double(0,30), aObject1.getConnectionPoint(Direction.WEST));
		assertEquals(new Point2D.Double(0,30), aObject1.getConnectionPoint(Direction.NORTH));
		assertEquals(new Point2D.Double(0,30), aObject1.getConnectionPoint(Direction.SOUTH));
		assertEquals("", aObject1.getName().toString());
		assertEquals(new Rectangle2D.Double(0, 0, 80, 60), aObject1.getShape().getBounds());
	}
	
	@Test
	public void testAddChild()
	{
		aObject1.addChild(aCall1);
		assertEquals( 1, aObject1.getChildren().size());
		assertEquals( aObject1, aCall1.getParent());
		assertEquals( aCall1, aObject1.getChildren().get(0));
		
		aObject1.addChild(aCall2);
		assertEquals( 2, aObject1.getChildren().size());
		assertEquals( aObject1, aCall1.getParent());
		assertEquals( aObject1, aCall2.getParent());
		assertEquals( aCall1, aObject1.getChildren().get(0));
		assertEquals( aCall2, aObject1.getChildren().get(1));
		
		// Move a field from one object to another
		aObject2.addChild(aCall1);
		assertEquals( 1, aObject1.getChildren().size());
		assertEquals( aObject1, aCall2.getParent());
		assertEquals( aCall2, aObject1.getChildren().get(0));
		
		assertEquals( 1, aObject2.getChildren().size());
		assertEquals( aObject2, aCall1.getParent());
		assertEquals( aCall1, aObject2.getChildren().get(0));
	}
	
	@Test
	public void testRemoveChild()
	{
		aObject1.addChild(aCall1);
		aObject1.addChild(aCall2);
		
		aObject1.removeChild(aCall1);
		assertEquals( 1, aObject1.getChildren().size());
		assertEquals( aCall2, aObject1.getChildren().get(0));
	}
	
	@Test 
	public void testClone()
	{
		MultiLineString o1 = new MultiLineString();
		o1.setText("o1");
		aObject1.setName(o1);
		ImplicitParameterNode clone = aObject1.clone();
		assertEquals(new Rectangle2D.Double(0, 0, 80, 120), clone.getBounds());
		assertEquals(0,clone.getChildren().size());
		assertEquals(new Point2D.Double(80,30), aObject1.getConnectionPoint(Direction.EAST));
		assertEquals(new Point2D.Double(0,30), aObject1.getConnectionPoint(Direction.WEST));
		assertEquals(new Point2D.Double(0,30), aObject1.getConnectionPoint(Direction.NORTH));
		assertEquals(new Point2D.Double(0,30), aObject1.getConnectionPoint(Direction.SOUTH));
		assertEquals("o1", clone.getName().toString());
		assertEquals(new Rectangle2D.Double(0, 0, 80, 60), clone.getShape().getBounds());
		
		aObject1.addChild(aCall1);
		aObject1.addChild(aCall2);
		
		clone = aObject1.clone();
		assertEquals(2, clone.getChildren().size());
		
		CallNode cf1 = (CallNode) clone.getChildren().get(0);
		CallNode cf2 = (CallNode) clone.getChildren().get(1);

		assertFalse( cf1 == aCall1 );
		assertFalse( cf2 == aCall2 );
	}
}
