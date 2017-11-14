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
/**
 * 
 */
package ca.mcgill.cs.stg.jetuml.framework;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.Stack;

import ca.mcgill.cs.stg.jetuml.commands.Command;
import ca.mcgill.cs.stg.jetuml.commands.CompoundCommand;
import ca.mcgill.cs.stg.jetuml.commands.MoveCommand;
import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.graph.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.DependencyEdge;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class TestMoveTracker
{
	private MoveTracker aMoveTracker;
	private SelectionList aSelection;
	private ClassDiagramGraph aGraph;
	private ClassNode aNode1; // Initial bounds: [x=150.0,y=150.0,w=100.0,h=60.0]
	private ClassNode aNode2; // Initial bounds: [x=400.0,y=400.0,w=100.0,h=60.0]
	private DependencyEdge aEdge1;
	private Field aCommandsField; 
	private Field aDxField;
	private Field aDyField;
	private Field aNodeField;
	
	@Before
	public void setUp() throws NoSuchFieldException, SecurityException
	{
		aMoveTracker = new MoveTracker();
		aSelection = new SelectionList();
		aGraph = new ClassDiagramGraph();
		aNode1 = new ClassNode();
		aNode1.translate(150, 150);
		aNode2 = new ClassNode();
		aNode2.translate(400, 400);
		aEdge1 = new DependencyEdge();
		aGraph.restoreEdge(aEdge1, aNode1, aNode1);
		aCommandsField = CompoundCommand.class.getDeclaredField("aCommands");
		aCommandsField.setAccessible(true);
		aDxField = MoveCommand.class.getDeclaredField("aDX");
		aDxField.setAccessible(true);
		aDyField = MoveCommand.class.getDeclaredField("aDY");
		aDyField.setAccessible(true);
		aNodeField = MoveCommand.class.getDeclaredField("aNode");
		aNodeField.setAccessible(true);
	}

	@Test
	public void moveSingleObjectFourTimes()
	{
		aSelection.add(aNode1);
		aMoveTracker.startTrackingMove(aSelection);
		aNode1.translate(20, 20);
		aNode1.translate(0, 200);
		aNode1.translate(50, 50);
		CompoundCommand command = aMoveTracker.endTrackingMove(aGraph);
		assertEquals(1, command.size());
		MoveCommand mc = (MoveCommand) getChildCommands(command).pop();
		assertEquals(70, getDx(mc), 0.0001);
		assertEquals(270, getDy(mc), 0.0001);
		
		// No change in selection, move only X
		aMoveTracker.startTrackingMove(aSelection);
		aNode1.translate(200, 0);
		command = aMoveTracker.endTrackingMove(aGraph);
		assertEquals(1, command.size());
		mc = (MoveCommand) getChildCommands(command).pop();
		assertEquals(200, getDx(mc), 0.0001);
		assertEquals(0, getDy(mc), 0.0001);
		
		// No change in selection, move only Y
		aMoveTracker.startTrackingMove(aSelection);
		aNode1.translate(0, 200);
		command = aMoveTracker.endTrackingMove(aGraph);
		assertEquals(1, command.size());
		mc = (MoveCommand) getChildCommands(command).pop();
		assertEquals(0, getDx(mc), 0.0001);
		assertEquals(200, getDy(mc), 0.0001);
		
		// No change in selection, null move
		aMoveTracker.startTrackingMove(aSelection);
		aNode1.translate(0, 0);
		command = aMoveTracker.endTrackingMove(aGraph);
		assertEquals(0, command.size());
	}
	
	@Test
	public void moveNodesAndEdges()
	{
		aSelection.add(aNode1);
		aSelection.add(aNode2);
		aSelection.add(aEdge1);
		aMoveTracker.startTrackingMove(aSelection);
		aNode1.translate(20, 20);
		aNode2.translate(20, 20);
		CompoundCommand command = aMoveTracker.endTrackingMove(aGraph);
		assertEquals(2, command.size());
		MoveCommand mc = (MoveCommand) getChildCommands(command).pop();
		assertEquals(20, getDx(mc), 0.0001);
		assertEquals(20, getDy(mc), 0.0001);
		assertTrue(aNode2 == getNode(mc));
		mc = (MoveCommand) getChildCommands(command).pop();
		assertEquals(20, getDx(mc), 0.0001);
		assertEquals(20, getDy(mc), 0.0001);
		assertTrue(aNode1 == getNode(mc));
		
		// Second identical move
		aMoveTracker.startTrackingMove(aSelection);
		aNode1.translate(20, 20);
		aNode2.translate(20, 20);
		command = aMoveTracker.endTrackingMove(aGraph);
		assertEquals(2, command.size());
		mc = (MoveCommand) getChildCommands(command).pop();
		assertEquals(20, getDx(mc), 0.0001);
		assertEquals(20, getDy(mc), 0.0001);
		assertTrue(aNode2 == getNode(mc));
		mc = (MoveCommand) getChildCommands(command).pop();
		assertEquals(20, getDx(mc), 0.0001);
		assertEquals(20, getDy(mc), 0.0001);
		assertTrue(aNode1 == getNode(mc));
	}
	
	@SuppressWarnings("unchecked")
	private Stack<Command> getChildCommands(CompoundCommand pCommand)
	{
		try
		{
			return (Stack<Command>)aCommandsField.get(pCommand);
		}
		catch( Exception pException )
		{
			fail();
			return null;
		}
	}
	
	private int getDx(MoveCommand pCommand)
	{
		try
		{
			return (int)aDxField.get(pCommand);
		}
		catch( Exception pException )
		{
			fail();
			return 0;
		}
	}
	
	private int getDy(MoveCommand pCommand)
	{
		try
		{
			return (int)aDyField.get(pCommand);
		}
		catch( Exception pException )
		{
			fail();
			return 0;
		}
	}
	
	private Node getNode(MoveCommand pCommand)
	{
		try
		{
			return (Node)aNodeField.get(pCommand);
		}
		catch( Exception pException )
		{
			fail();
			return null;
		}
	}
}
