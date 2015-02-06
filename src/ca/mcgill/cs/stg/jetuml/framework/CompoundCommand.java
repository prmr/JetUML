package ca.mcgill.cs.stg.jetuml.framework;

import java.util.Stack;

public class CompoundCommand implements Command{
	private Stack<Command> aCommands;
	
	public void CompundCommand()
	{
	}
	
	public void add(Command pCommand)
	{
		aCommands.push(pCommand);
	}
	
	public void undo()
	{
		for(Command c : aCommands)
		{
			c.undo();
		}
	}
	
	public void execute()
	{
		for(Command c: aCommands)
		{
			c.execute();
		}
	}
}
