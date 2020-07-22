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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestImplicitParameterNode
{
	private ImplicitParameterNode aObject1;
	private ImplicitParameterNode aObject2;
	private CallNode aCall1;
	private CallNode aCall2;
	
	@BeforeEach
	public void setup()
	{
		aObject1 = new ImplicitParameterNode();
		aObject2 = new ImplicitParameterNode();
		aCall1 = new CallNode();
		aCall2 = new CallNode();
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
}
