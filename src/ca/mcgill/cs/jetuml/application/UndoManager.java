/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2017 by the contributors of the JetUML project.
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

import java.util.Stack;

import ca.mcgill.cs.jetuml.commands.Command;
import ca.mcgill.cs.jetuml.commands.CompoundCommand;

/**
 * Performs the undoing and redoing of commands on a graph.
 * @author EJBQ
 *
 */
public class UndoManager 
{
	private Stack<Command> aPastCommands; //the commands that have been input and can be undone
	private Stack<Command> aUndoneCommands; //the commands that have been undone and can be redone
	private Stack<CompoundCommand> aTrackingCommands; //used for many commands coming at once
	private boolean aHoldChanges = false; //turned on while undoing or redoing to prevent duplication
	
	/**
	 * Creates a new UndoManager with the GraphPanel.
	 * These should be assigned one per panel.
	 */
	public UndoManager()
	{
		aPastCommands = new Stack<Command>();
		aUndoneCommands = new Stack<Command>();
		aTrackingCommands = new Stack<CompoundCommand>();
	}

	/**
	 * Adds a command to the stack to be undone.
	 * Wipes the redone command if there is anything there.
	 * Will not add the command if changes are being held, which occurs 
	 * when we are in the middle of executing a command.
	 * @param pCommand The command to be added
	 */
	public void add(Command pCommand)
	{
		if(!aHoldChanges)
		{
			if(!aUndoneCommands.empty())
			{
				aUndoneCommands.clear();
			}
			if(!aTrackingCommands.empty())
			{
				aTrackingCommands.peek().add(pCommand);
			}
			else
			{
				aPastCommands.push(pCommand);
			}
		}
	}

	/**
	 * Undoes a command.
	 * Holds changes so no new commands are added during this.
	 * Adds the command to the redone stack.
	 */
	public void undoCommand()
	{
		if(aPastCommands.empty())
		{
			return;
		}
		aHoldChanges = true;
		Command toUndo = aPastCommands.pop();
		toUndo.undo();
		aUndoneCommands.push(toUndo);
		aHoldChanges = false;
	}

	/**
	 * Pops most recent undone command and executes it.
	 * Holds changes so no new commands are added during this.
	 * Adds the command to the redone stack.
	 */
	public void redoCommand()
	{
		aHoldChanges = true;
		if (aUndoneCommands.empty())
		{
			return;
		}
		Command toRedo = aUndoneCommands.pop();
		toRedo.execute();
		aPastCommands.push(toRedo);
		aHoldChanges = false;
	}

	/**
	 * Creates a compound command that all coming commands will be added to.
	 * Used to perform many commands at once
	 */
	public void startTracking()
	{
		aTrackingCommands.push(new CompoundCommand());
	}

	/**
	 * Finishes off the compound command and adds it to the stack.
	 */
	public void endTracking()
	{
		if(!aTrackingCommands.empty())
		{
			CompoundCommand compoundCommand = aTrackingCommands.pop(); 
			if(compoundCommand.size() > 0)
			{
				add(compoundCommand);
			}
		}
	}

}
