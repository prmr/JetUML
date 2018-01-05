/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.Stack;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.commands.ChangePropertyCommand;
import ca.mcgill.cs.jetuml.commands.Command;
import ca.mcgill.cs.jetuml.commands.CompoundCommand;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.graph.Property;
import ca.mcgill.cs.jetuml.graph.nodes.ClassNode;

public class TestPropertyChangeTracker
{
	private PropertyChangeTracker aTracker;
	private ClassNode aNode;
	private Field aCommandsField;
	private Field aOldValueField;
	private Field aNewValueField;
	private Field aPropertyField;
	
	public TestPropertyChangeTracker()
	{
		try
		{
			aCommandsField = CompoundCommand.class.getDeclaredField("aCommands");
			aCommandsField.setAccessible(true);
			aOldValueField = ChangePropertyCommand.class.getDeclaredField("aOldValue");
			aOldValueField.setAccessible(true);
			aNewValueField = ChangePropertyCommand.class.getDeclaredField("aNewValue");
			aNewValueField.setAccessible(true);
			aPropertyField = ChangePropertyCommand.class.getDeclaredField("aProperty");
			aPropertyField.setAccessible(true);
		}
		catch( Exception pException )
		{
			fail();
		}
	}
	
	@SuppressWarnings("unchecked")
	private Stack<Command> commands(CompoundCommand pCommand)
	{
		try
		{
			return (Stack<Command>) aCommandsField.get(pCommand);
		}
		catch( Exception pException )
		{
			fail();
			return null;
		}
	}
	
	private Property getProperty(ChangePropertyCommand pCommand)
	{
		try
		{
			return (Property) aPropertyField.get(pCommand);
		}
		catch( Exception pException )
		{
			fail();
			return null;
		}
	}
	
	private Object getOldValue(ChangePropertyCommand pCommand)
	{
		try
		{
			return (Object) aOldValueField.get(pCommand);
		}
		catch( Exception pException )
		{
			fail();
			return null;
		}
	}
	
	private Object getNewValue(ChangePropertyCommand pCommand)
	{
		try
		{
			return (Object) aNewValueField.get(pCommand);
		}
		catch( Exception pException )
		{
			fail();
			return null;
		}
	}
	
	@Before
	public void setup()
	{
		aNode = new ClassNode();
		aTracker = new PropertyChangeTracker(aNode);
	}
	
	@Test
	public void testNoChanges()
	{
		aTracker.startTracking();
		CompoundCommand command = aTracker.stopTracking();
		assertEquals(0, command.size());
	}
	
	@Test
	public void testOneChangeInteger()
	{
		aTracker.startTracking();
		aNode.moveTo(new Point(0,10));
		CompoundCommand command = aTracker.stopTracking();
		assertEquals(1, command.size());
		ChangePropertyCommand inner = (ChangePropertyCommand) commands(command).pop();
		Property property = getProperty(inner);
		assertEquals("y", property.getName());
		assertEquals(0, getOldValue(inner));
		assertEquals(10, getNewValue(inner));
	}
	
	@Test
	public void testOneChangeString()
	{
		aTracker.startTracking();
		aNode.setName("Foo");
		CompoundCommand command = aTracker.stopTracking();
		assertEquals(1, command.size());
		ChangePropertyCommand inner = (ChangePropertyCommand) commands(command).pop();
		Property property = getProperty(inner);
		assertEquals("name", property.getName());
		assertEquals("", getOldValue(inner));
		assertEquals("Foo", getNewValue(inner));
	}
	
	@Test
	public void testTwoChanges()
	{
		aTracker.startTracking();
		aNode.moveTo(new Point(0,10));
		aNode.setName("Foo");
		CompoundCommand command = aTracker.stopTracking();
		assertEquals(2, command.size());
		ChangePropertyCommand inner = (ChangePropertyCommand) commands(command).pop();
		Property property = getProperty(inner);
		assertEquals("name", property.getName());
		assertEquals("", getOldValue(inner));
		assertEquals("Foo", getNewValue(inner));
		inner = (ChangePropertyCommand) commands(command).pop();
		property = getProperty(inner);
		assertEquals("y", property.getName());
		assertEquals(0, getOldValue(inner));
		assertEquals(10, getNewValue(inner));
	}
}
