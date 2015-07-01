package ca.mcgill.cs.stg.jetuml.commands;

import java.util.Stack;

/**
 * Holds multiple commands to be executed or undone.
 * @author EJBQ
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
