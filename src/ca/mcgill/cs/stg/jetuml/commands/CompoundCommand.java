package ca.mcgill.cs.stg.jetuml.commands;

import java.util.Stack;

public class CompoundCommand implements Command{
	private Stack<Command> aCommands;
	private int aSize;
	
	public CompoundCommand()
	{
		aCommands = new Stack<Command>();
	}
	
	public void add(Command pCommand)
	{
		aCommands.push(pCommand);
		aSize++;
	}
	
	public int size()
	{
		return aSize;
	}
	
	/**
	 * Undoes each command on the stack
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
		aCommands.push(temp.pop());
	}
	
	/**
	 * Executes each command on the stack
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
		aCommands.push(temp.pop());
	}
}
