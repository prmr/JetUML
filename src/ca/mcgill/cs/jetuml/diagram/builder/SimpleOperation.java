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
package ca.mcgill.cs.jetuml.diagram.builder;

/**
 * Represents a simple (non-compound) operation. The operation
 * does no validation of the input method, so any code that 
 * constructs a SimpleOperation is responsible to ensure that,
 * when executed or undone, the operation will be valid.
 */
public class SimpleOperation implements DiagramOperation
{
	private final Runnable aOperation;
	private final Runnable aReverse;
	
	/**
	 * Creates an operation.
	 * 
	 * @param pOperation The code to run when the operation is
	 * executed.
	 * @param pReverse The code to run when the operation is
	 * undone.
	 * @pre pOperation != null
	 * @pre pReverse != null
	 */
	public SimpleOperation(Runnable pOperation, Runnable pReverse)
	{
		assert pOperation != null && pReverse != null;
		aOperation = pOperation;
		aReverse = pReverse;
	}

	@Override
	public void execute()
	{
		aOperation.run();
	}

	@Override
	public void undo()
	{
		aReverse.run();
	}
}
