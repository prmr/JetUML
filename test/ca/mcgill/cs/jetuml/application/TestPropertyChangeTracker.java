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
package ca.mcgill.cs.jetuml.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.builder.CompoundOperation;
import ca.mcgill.cs.jetuml.diagram.builder.DiagramOperation;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;

public class TestPropertyChangeTracker
{
	private Object aTracker;
	private ClassNode aNode;
	private Field aOperationsField;
	
	public TestPropertyChangeTracker()
	{
		aNode = new ClassNode();
		try
		{
			aOperationsField = CompoundOperation.class.getDeclaredField("aOperations");
			aOperationsField.setAccessible(true);
			Constructor<?> constructor = Class.forName("ca.mcgill.cs.jetuml.gui.PropertyEditorDialog$PropertyChangeTracker")
					.getDeclaredConstructor(DiagramElement.class);
			constructor.setAccessible(true);
			aTracker = constructor.newInstance(aNode);
			
		}
		catch( ReflectiveOperationException exception )
		{
			fail();
		}
	}
	
	private void startTracking() throws ReflectiveOperationException
	{
		Method method = Class.forName("ca.mcgill.cs.jetuml.gui.PropertyEditorDialog$PropertyChangeTracker")
			.getDeclaredMethod("startTracking");
		method.setAccessible(true);
		method.invoke(aTracker);
	}
	
	private CompoundOperation stopTracking() throws ReflectiveOperationException
	{
		Method method = Class.forName("ca.mcgill.cs.jetuml.gui.PropertyEditorDialog$PropertyChangeTracker")
			.getDeclaredMethod("stopTracking");
		method.setAccessible(true);
		return (CompoundOperation) method.invoke(aTracker);
	}
	
	@Test
	void testNoChanges() throws ReflectiveOperationException
	{
		startTracking();
		CompoundOperation operation = stopTracking();
		assertTrue(operation.isEmpty());
	}
	
	@Test
	void testOneChangeString() throws ReflectiveOperationException
	{
		startTracking();
		aNode.setName("Foo");
		CompoundOperation operation = stopTracking();
		
		assertEquals(1, getOperations(operation).size());
		
		operation.undo();
		assertEquals("", aNode.getName());
		operation.execute();
		assertEquals("Foo", aNode.getName());
	}
	
	@Test
	void testTwoChanges() throws ReflectiveOperationException
	{
		startTracking();
		aNode.setAttributes("Bar");
		aNode.setName("Foo");
		CompoundOperation command = stopTracking();
		List<DiagramOperation> operations = getOperations(command);
		
		assertEquals(2, operations.size());
		
		operations.get(0).undo();
		assertEquals("Bar", aNode.getAttributes());
		assertEquals("", aNode.getName());
		operations.get(0).execute();
		assertEquals("Bar", aNode.getAttributes());
		assertEquals("Foo", aNode.getName());
		
		operations.get(1).undo();
		assertEquals("", aNode.getAttributes());
		assertEquals("Foo", aNode.getName());
		operations.get(1).execute();
		assertEquals("Bar", aNode.getAttributes());
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
