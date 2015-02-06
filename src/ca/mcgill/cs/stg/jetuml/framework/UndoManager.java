package ca.mcgill.cs.stg.jetuml.framework;

import java.util.Stack;

public class UndoManager {
	private Stack<Command> aPastCommands;
	private Stack<Command> aRedoneCommands;
	
	void addCommand(Command pCommand)
	{
		aRedoneCommands.clear();
		aPastCommands.add(pCommand);
	}
	
	void undoCommand()
	{
		if(aPastCommands.empty())
		{
			return;
		}
	}
	
	void redoCommand()
	{
		if (aRedoneCommands.empty())
		{
			return;
		}
		Command toRedo = aRedoneCommands.pop();
		//toRedo.execute();
		
	}
	
}
