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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.builder.CompoundOperation;
import ca.mcgill.cs.jetuml.diagram.builder.DiagramOperation;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestPropertyChangeTracker
{
	private PropertyChangeTracker aTracker;
	private ClassNode aNode;
	private Field aOperationsField;
	
	/**
	  * Load JavaFX toolkit and environment.
	  */
	 @BeforeAll
	 public static void setupClass()
	 {
		 JavaFXLoader.load();
	 }
	
	public TestPropertyChangeTracker()
	{
		try
		{
			aOperationsField = CompoundOperation.class.getDeclaredField("aOperations");
			aOperationsField.setAccessible(true);
		}
		catch( ReflectiveOperationException pException )
		{
			fail();
		}
	}
	
	@BeforeEach
	public void setup()
	{
		aNode = new ClassNode();
		aTracker = new PropertyChangeTracker(aNode);
	}
	
	@Test
	public void testNoChanges()
	{
		aTracker.startTracking();
		CompoundOperation operation = aTracker.stopTracking();
		assertTrue(operation.isEmpty());
	}
	
	@Test
	public void testOneChangeInteger()
	{
		aTracker.startTracking();
		aNode.moveTo(new Point(0,10));
		CompoundOperation operation = aTracker.stopTracking();
		assertEquals(1, getOperations(operation).size());
		
		operation.undo();
		assertEquals(new Point(0,0), aNode.position());
		operation.execute();
		assertEquals(new Point(0,10), aNode.position());
	}
	
	@Test
	public void testOneChangeString()
	{
		aTracker.startTracking();
		aNode.setName("Foo");
		CompoundOperation operation = aTracker.stopTracking();
		
		assertEquals(1, getOperations(operation).size());
		
		operation.undo();
		assertEquals("", aNode.getName());
		operation.execute();
		assertEquals("Foo", aNode.getName());
	}
	
	@Test
	public void testTwoChanges()
	{
		aTracker.startTracking();
		aNode.moveTo(new Point(0,10));
		aNode.setName("Foo");
		CompoundOperation command = aTracker.stopTracking();
		List<DiagramOperation> operations = getOperations(command);
		
		assertEquals(2, operations.size());
		
		operations.get(0).undo();
		assertEquals(new Point(0,0), aNode.position());
		assertEquals("Foo", aNode.getName());
		operations.get(0).execute();
		assertEquals(new Point(0,10), aNode.position());
		assertEquals("Foo", aNode.getName());
		
		operations.get(1).undo();
		assertEquals(new Point(0,10), aNode.position());
		assertEquals("", aNode.getName());
		operations.get(1).execute();
		assertEquals(new Point(0,10), aNode.position());
		assertEquals("Foo", aNode.getName());
		
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
