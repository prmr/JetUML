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

package ca.mcgill.cs.jetuml.diagram.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestDiagramOperationProcessor
{
	private StringBuilder aBuilder;
	private DiagramOperationProcessor aProcessor = new DiagramOperationProcessor();
	
	@BeforeEach
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
	
	@Test
	public void testHasUnsavedOperations_Empty()
	{
		assertFalse(aProcessor.hasUnsavedOperations());
	}
	
	@Test
	public void testHasUnsavedOperations_False_OperationsNoSave()
	{
		aProcessor.executeNewOperation(createOperation('A'));
		assertTrue(aProcessor.hasUnsavedOperations());
		aProcessor.executeNewOperation(createOperation('B'));
		aProcessor.executeNewOperation(createOperation('C'));
		assertTrue(aProcessor.hasUnsavedOperations());
	}
	
	@Test
	public void testHasUnsavedOperations_False_WithSave()
	{
		aProcessor.executeNewOperation(createOperation('A'));
		aProcessor.executeNewOperation(createOperation('B'));
		aProcessor.executeNewOperation(createOperation('C'));
		assertTrue(aProcessor.hasUnsavedOperations());
		aProcessor.diagramSaved();
		assertFalse(aProcessor.hasUnsavedOperations());
	}
	
	@Test
	public void testHasUnsavedOperations_True_WithSave()
	{
		aProcessor.executeNewOperation(createOperation('A'));
		aProcessor.executeNewOperation(createOperation('B'));
		aProcessor.executeNewOperation(createOperation('C'));
		assertTrue(aProcessor.hasUnsavedOperations());
		aProcessor.diagramSaved();
		assertFalse(aProcessor.hasUnsavedOperations());
		aProcessor.executeNewOperation(createOperation('D'));
		assertTrue(aProcessor.hasUnsavedOperations());
	}
	
	@Test
	public void testDiagramSaved_Empty()
	{
		aProcessor.diagramSaved();
		assertFalse(aProcessor.hasUnsavedOperations());
	}
	
	@Test
	public void testDiagramSaved_UndoEverything()
	{
		aProcessor.executeNewOperation(createOperation('A'));
		aProcessor.executeNewOperation(createOperation('B'));
		aProcessor.executeNewOperation(createOperation('C'));
		assertTrue(aProcessor.hasUnsavedOperations());
		aProcessor.undoLastExecutedOperation();
		assertTrue(aProcessor.hasUnsavedOperations());
		aProcessor.undoLastExecutedOperation();
		assertTrue(aProcessor.hasUnsavedOperations());
		aProcessor.undoLastExecutedOperation();
		assertFalse(aProcessor.hasUnsavedOperations());
	}
	
	@Test
	public void testDiagramSaved_UndoToSynchPoint()
	{
		aProcessor.executeNewOperation(createOperation('A'));
		aProcessor.diagramSaved();
		assertFalse(aProcessor.hasUnsavedOperations());
		aProcessor.executeNewOperation(createOperation('B'));
		assertTrue(aProcessor.hasUnsavedOperations());
		aProcessor.executeNewOperation(createOperation('C'));
		assertTrue(aProcessor.hasUnsavedOperations());
		aProcessor.undoLastExecutedOperation();
		assertTrue(aProcessor.hasUnsavedOperations());
		aProcessor.undoLastExecutedOperation();
		assertFalse(aProcessor.hasUnsavedOperations());
	}
	
	
	/*
	 * The processor undoes one more operation than what was saved,
	 * so technically the diagram is modified. 
	 */
	@Test
	public void testDiagramSaved_UndoPastSynchPoint()
	{
		aProcessor.executeNewOperation(createOperation('A'));
		aProcessor.executeNewOperation(createOperation('B'));
		aProcessor.executeNewOperation(createOperation('C'));
		aProcessor.diagramSaved();
		aProcessor.executeNewOperation(createOperation('D'));
		aProcessor.undoLastExecutedOperation();
		assertFalse(aProcessor.hasUnsavedOperations());
		aProcessor.undoLastExecutedOperation();
		assertTrue(aProcessor.hasUnsavedOperations());
	}
	
	/*
	 * The processor undoes more operations than what was saved,
	 * so technically the diagram is modified even if we reach 
	 * the bottom of the stack.
	 */
	@Test
	public void testDiagramSaved_UndoPastSynchPointToEmpty()
	{
		aProcessor.executeNewOperation(createOperation('A'));
		aProcessor.executeNewOperation(createOperation('B'));
		aProcessor.executeNewOperation(createOperation('C'));
		aProcessor.diagramSaved();
		aProcessor.undoLastExecutedOperation();
		aProcessor.undoLastExecutedOperation();
		aProcessor.undoLastExecutedOperation();
		assertTrue(aProcessor.hasUnsavedOperations());
	}
	
	@Test
	public void testDiagramSaved_RedoFromSynchPoint()
	{
		aProcessor.executeNewOperation(createOperation('A'));
		aProcessor.executeNewOperation(createOperation('B'));
		aProcessor.executeNewOperation(createOperation('C'));
		aProcessor.diagramSaved();
		aProcessor.executeNewOperation(createOperation('D'));
		aProcessor.undoLastExecutedOperation();
		assertFalse(aProcessor.hasUnsavedOperations());
		aProcessor.redoLastUndoneOperation();
		assertTrue(aProcessor.hasUnsavedOperations());
	}
	
	@Test
	public void testDiagramSaved_RedoToSynchPoint()
	{
		aProcessor.executeNewOperation(createOperation('A'));
		aProcessor.executeNewOperation(createOperation('B'));
		aProcessor.executeNewOperation(createOperation('C'));
		aProcessor.diagramSaved();
		aProcessor.executeNewOperation(createOperation('D'));
		aProcessor.undoLastExecutedOperation();
		aProcessor.undoLastExecutedOperation();
		assertTrue(aProcessor.hasUnsavedOperations());
		aProcessor.redoLastUndoneOperation();
		assertFalse(aProcessor.hasUnsavedOperations());
	}
}
