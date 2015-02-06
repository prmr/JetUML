package ca.mcgill.cs.stg.jetuml.framework;

import java.util.Stack;

public class CompoundCommand implements Command{
	private Stack<Command> aCommands;
	
	public CompoundCommand()
	{
		aCommands = new Stack<Command>();
	}
	
	public void add(Command pCommand)
	{
		aCommands.push(pCommand);
	}
	
	public void undo()
	{
		while(!aCommands.empty())
		{
			Command c = aCommands.pop();
			c.undo();
		}
	}
	
	public void execute()
	{
		while(!aCommands.empty())
		{
			Command c = aCommands.pop();
			c.execute();
		}
	}
}
