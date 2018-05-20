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
package ca.mcgill.cs.jetuml.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.Stack;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.commands.AddNodeCommand;
import ca.mcgill.cs.jetuml.commands.Command;
import ca.mcgill.cs.jetuml.commands.CompoundCommand;
import ca.mcgill.cs.jetuml.diagrams.ClassDiagram;
import ca.mcgill.cs.jetuml.graph.nodes.ClassNode;

public class TestUndoManager
{
	private UndoManager aUndoManager;
	private AddNodeCommand aCommand1;
	private AddNodeCommand aCommand2;
	private AddNodeCommand aCommand3;
	private AddNodeCommand aCommand4;
	private AddNodeCommand aCommand5;
	private Field aPastCommands;
	private Field aUndoneCommands;
	private Field aTrackingCommands;

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
	public void setup() throws Exception
	{
		aUndoManager = new UndoManager();
		aCommand1 = new AddNodeCommand(new ClassDiagram(), new ClassNode());
		aCommand2 = new AddNodeCommand(new ClassDiagram(), new ClassNode());
		aCommand3 = new AddNodeCommand(new ClassDiagram(), new ClassNode());
		aCommand4 = new AddNodeCommand(new ClassDiagram(), new ClassNode());
		aCommand5 = new AddNodeCommand(new ClassDiagram(), new ClassNode());
		aPastCommands = UndoManager.class.getDeclaredField("aPastCommands");
		aPastCommands.setAccessible(true);
		aUndoneCommands = UndoManager.class.getDeclaredField("aUndoneCommands");
		aUndoneCommands.setAccessible(true);
		aTrackingCommands = UndoManager.class.getDeclaredField("aTrackingCommands");
		aTrackingCommands.setAccessible(true);
	}
	
	@Test
	public void testBasicAdd()
	{
		aUndoManager.add(aCommand1);
		assertTrue(getPastCommands().get(0) == aCommand1);
		assertEquals(0, getUndoneCommands().size());
		assertEquals(0, getTrackingCommands().size());
		aUndoManager.add(aCommand2);
		assertEquals(2, getPastCommands().size());
		assertTrue(getPastCommands().pop() == aCommand2);
		assertTrue(getPastCommands().pop() == aCommand1);
		assertEquals(0, getUndoneCommands().size());
		assertEquals(0, getTrackingCommands().size());
	}
	
	@Test
	public void testAddWithTracking()
	{
		aUndoManager.add(aCommand1);
		aUndoManager.startTracking();
		assertTrue(getPastCommands().get(0) == aCommand1);
		assertEquals(0,getUndoneCommands().size());
		assertEquals(1,getTrackingCommands().size());
		aUndoManager.add(aCommand2);
		assertEquals(1, getPastCommands().size());
		assertTrue(getPastCommands().get(0) == aCommand1);
		assertEquals(0,getUndoneCommands().size());
		assertEquals(1,getTrackingCommands().size());
		CompoundCommand cc = (CompoundCommand)getTrackingCommands().peek();
		assertEquals(1, cc.size());
		aUndoManager.add(aCommand3);
		assertEquals(1, getPastCommands().size());
		assertTrue(getPastCommands().get(0) == aCommand1);
		assertEquals(0,getUndoneCommands().size());
		assertEquals(1,getTrackingCommands().size());
		cc = (CompoundCommand)getTrackingCommands().peek();
		assertEquals(2, cc.size());
		aUndoManager.endTracking();
		assertEquals(2, getPastCommands().size());
		cc = (CompoundCommand)getPastCommands().pop();
		assertEquals(2, cc.size());
		assertTrue(getPastCommands().pop() == aCommand1);
		assertEquals(0, getTrackingCommands().size());
		assertEquals(0, getUndoneCommands().size());
	}
	
	@Test
	public void testAddWithTracking3Levels()
	{
		aUndoManager.startTracking();
		aUndoManager.add(aCommand1);
		aUndoManager.add(aCommand2);
		aUndoManager.startTracking();
		aUndoManager.add(aCommand3);
		aUndoManager.add(aCommand4);
		aUndoManager.add(aCommand5);
		aUndoManager.startTracking();
		assertEquals(0, getPastCommands().size());
		assertEquals(0, getUndoneCommands().size());
		assertEquals(3, getTrackingCommands().size());
		aUndoManager.endTracking();
		assertEquals(0, getPastCommands().size());
		assertEquals(0, getUndoneCommands().size());
		assertEquals(2, getTrackingCommands().size());
		CompoundCommand cc = (CompoundCommand)getTrackingCommands().peek();
		assertEquals(3, cc.size());
		aUndoManager.endTracking(); // The command is added to the now current tracking command.
		assertEquals(0, getPastCommands().size());
		assertEquals(0, getUndoneCommands().size());
		assertEquals(1, getTrackingCommands().size());
		cc = (CompoundCommand)getTrackingCommands().peek();
		assertEquals(3, cc.size());
		aUndoManager.endTracking();
		assertEquals(1, getPastCommands().size());
		assertEquals(0, getUndoneCommands().size());
		assertEquals(0, getTrackingCommands().size());
		cc = (CompoundCommand)getPastCommands().peek();
		assertEquals(3, cc.size());
		aUndoManager.endTracking();
		assertEquals(1, getPastCommands().size());
		assertEquals(0, getUndoneCommands().size());
		assertEquals(0, getTrackingCommands().size());
		cc = (CompoundCommand)getPastCommands().peek();
		assertEquals(3, cc.size());
	}
	
	@SuppressWarnings("unchecked")
	private Stack<Command> getPastCommands()
	{
		try
		{
			return (Stack<Command>)aPastCommands.get(aUndoManager);
		}
		catch( IllegalAccessException exception )
		{
			fail();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private Stack<Command> getUndoneCommands()
	{
		try
		{
			return (Stack<Command>)aUndoneCommands.get(aUndoManager);
		}
		catch( IllegalAccessException exception )
		{
			fail();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private Stack<Command> getTrackingCommands()
	{
		try
		{
			return (Stack<Command>)aTrackingCommands.get(aUndoManager);
		}
		catch( IllegalAccessException exception )
		{
			fail();
			return null;
		}
	}
}
