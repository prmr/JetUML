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
package ca.mcgill.cs.jetuml.commands;

import java.util.Stack;

/**
 * Holds multiple commands to be executed or undone.
 */
public class CompoundCommand implements Command
{
	private Stack<Command> aCommands;

	/**
	 * Creates a new CompoundCommand.
	 */
	public CompoundCommand()
	{
		aCommands = new Stack<Command>();
	}

	/**
	 * Adds a command to the stack to be performed.
	 * @param pCommand The command to be added
	 */
	public void add(Command pCommand)
	{
		aCommands.push(pCommand);
	}

	/**
	 * Returns the number of commands in the compound command.
	 * @return the size of the stack
	 */
	public int size()
	{
		return aCommands.size();
	}

	/**
	 * Undoes each command on the stack.
	 * Puts them in a temporary stack and pops them to retain the order
	 */
	public void undo()
	{
		Stack<Command> temp = new Stack<Command>();
		while(!aCommands.empty())
		{
			Command c = aCommands.pop();
			c.undo();
			temp.push(c);
		}
		aCommands = temp;
	}

	/**
	 * Executes each command on the stack.
	 * Puts them in a temporary stack and pops them to retain the order
	 */
	public void execute()
	{
		Stack<Command> temp = new Stack<Command>();
		while(!aCommands.empty())
		{
			Command c = aCommands.pop();
			c.execute();
			temp.push(c);
		}
		aCommands = temp;
	}
}
