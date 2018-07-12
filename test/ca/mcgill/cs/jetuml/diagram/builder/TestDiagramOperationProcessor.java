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

package ca.mcgill.cs.jetuml.diagram.builder;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestDiagramOperationProcessor
{
	private StringBuilder aBuilder;
	private DiagramOperationProcessor aProcessor = new DiagramOperationProcessor();
	
	@Before
	public void setUp()
	{
		aBuilder = new StringBuilder();
	}
	
	/*
	 * Creates a SimpleOperation that adds a single character
	 * to the builder.
	 */
	private SimpleOperation createOperation(char pChar)
	{
		return new SimpleOperation(
				()-> aBuilder.append(pChar),
				()-> aBuilder.deleteCharAt(aBuilder.length()-1));
	}
	
	@Test
	public void testEmpty()
	{
		assertFalse(aProcessor.canUndo());
		assertFalse(aProcessor.canRedo());
	}
	
	@Test
	public void testExecuteUndoRedoOne()
	{
		aProcessor.executeNewOperation(createOperation('A'));
		assertTrue(aProcessor.canUndo());
		assertFalse(aProcessor.canRedo());
		assertEquals("A", aBuilder.toString());
		
		aProcessor.undoLastExecutedOperation();
		assertFalse(aProcessor.canUndo());
		assertTrue(aProcessor.canRedo());
		assertEquals("", aBuilder.toString());
		
		aProcessor.redoLastUndoneOperation();
		assertFalse(aProcessor.canRedo());
		assertTrue(aProcessor.canUndo());
		assertEquals("A", aBuilder.toString());
	}
	
	@Test
	public void testExecuteUndoRedoThree()
	{
		aProcessor.executeNewOperation(createOperation('A'));
		aProcessor.executeNewOperation(createOperation('B'));
		aProcessor.executeNewOperation(createOperation('C'));
		assertTrue(aProcessor.canUndo());
		assertFalse(aProcessor.canRedo());
		assertEquals("ABC", aBuilder.toString());
		
		aProcessor.undoLastExecutedOperation();
		assertTrue(aProcessor.canUndo());
		assertTrue(aProcessor.canRedo());
		assertEquals("AB", aBuilder.toString());
		
		aProcessor.redoLastUndoneOperation();
		assertTrue(aProcessor.canUndo());
		assertFalse(aProcessor.canRedo());
		assertEquals("ABC", aBuilder.toString());
		
		aProcessor.undoLastExecutedOperation();
		assertTrue(aProcessor.canUndo());
		assertTrue(aProcessor.canRedo());
		assertEquals("AB", aBuilder.toString());
		
		aProcessor.undoLastExecutedOperation();
		assertTrue(aProcessor.canUndo());
		assertTrue(aProcessor.canRedo());
		assertEquals("A", aBuilder.toString());
		
		aProcessor.redoLastUndoneOperation();
		assertTrue(aProcessor.canUndo());
		assertTrue(aProcessor.canRedo());
		assertEquals("AB", aBuilder.toString());
		
		aProcessor.undoLastExecutedOperation();
		assertTrue(aProcessor.canUndo());
		assertTrue(aProcessor.canRedo());
		assertEquals("A", aBuilder.toString());
		
		aProcessor.undoLastExecutedOperation();
		assertFalse(aProcessor.canUndo());
		assertTrue(aProcessor.canRedo());
		assertEquals("", aBuilder.toString());
		
		aProcessor.redoLastUndoneOperation();
		assertTrue(aProcessor.canUndo());
		assertTrue(aProcessor.canRedo());
		assertEquals("A", aBuilder.toString());
		
		aProcessor.redoLastUndoneOperation();
		assertTrue(aProcessor.canUndo());
		assertTrue(aProcessor.canRedo());
		assertEquals("AB", aBuilder.toString());
		
		aProcessor.redoLastUndoneOperation();
		assertTrue(aProcessor.canUndo());
		assertFalse(aProcessor.canRedo());
		assertEquals("ABC", aBuilder.toString());
	}
}
