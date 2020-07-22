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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Responsible for executing and undoing operations, and managing the collection 
 * of previously executed and undone operations. Can also compute whether a 
 * diagram has unsaved modifications.
 */
public class DiagramOperationProcessor
{
	private final List<DiagramOperation> aExecutedOperations = new ArrayList<>();
	private final List<DiagramOperation> aUndoneOperations = new ArrayList<>();
	private Optional<DiagramOperation> aLastSavedOperation = Optional.empty();
	
	/**
	 * Executes pOperation and adds it to the list of executed
	 * operations.
	 * 
	 * @param pOperation The operation to execute.
	 * @pre pOperation != null;
	 */
	public void executeNewOperation(DiagramOperation pOperation)
	{
		assert pOperation != null;
		pOperation.execute();
		aExecutedOperations.add(pOperation);
	}
	
	/**
	 * @return True if the diagram has operations that have not been saved yet.
	 */
	public boolean hasUnsavedOperations()
	{
		if( aLastSavedOperation.isPresent() )
		{
			if( aExecutedOperations.isEmpty() )
			{
				return true;
			}
			else
			{
				return aLastSavedOperation.get() != peek();
			}
		}
		else
		{
			return !aExecutedOperations.isEmpty();
		}
	}
	
	private DiagramOperation peek()
	{
		return aExecutedOperations.get(aExecutedOperations.size()-1);
	}
	
	/**
	 * Indicates that the diagram managed by this processor has been saved.
	 */
	public void diagramSaved()
	{
		aLastSavedOperation = Optional.empty();
		if( aExecutedOperations.size() > 0 )
		{
			aLastSavedOperation = Optional.of(peek());
		}
	}
	
	/**
	 * Adds pOperation to the list of already executed operations,
	 * without first executing it. 
	 * 
	 * @param pOperation The operation to store.
	 * @pre pOperation != null
	 */
	public void storeAlreadyExecutedOperation(DiagramOperation pOperation)
	{
		assert pOperation != null;
		aExecutedOperations.add(pOperation);
	}
	
	/**
	 * Undoes the last executed operation, and adds it to the list
	 * of undone operations.
	 * @pre canUndo()
	 */
	public void undoLastExecutedOperation()
	{
		assert canUndo();
		DiagramOperation operation = aExecutedOperations.remove(aExecutedOperations.size() - 1);
		operation.undo();
		aUndoneOperations.add(operation);
	}
	
	/**
	 * Re-executes the last undone operation, and adds it to the list 
	 * of executes operations.
	 * @pre canRedo();
	 */
	public void redoLastUndoneOperation()
	{
		assert canRedo();
		DiagramOperation operation = aUndoneOperations.remove(aUndoneOperations.size() - 1);
		operation.execute();
		aExecutedOperations.add(operation);
	}

	/**
	 * @return True if there is at least one operation to undo.
	 */
	public boolean canUndo()
	{
		return !aExecutedOperations.isEmpty();
	}
	
	/**
	 * @return True if there is at least one operation to redo.
	 */
	public boolean canRedo()
	{
		return !aUndoneOperations.isEmpty();
	}
}
