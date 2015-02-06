package ca.mcgill.cs.stg.jetuml.framework;

import java.util.Stack;

public class UndoManager {
	private Stack<Command> aPastCommands;
	private Stack<Command> aUndoneCommands;
	
	void add(Command pCommand)
	{
		aUndoneCommands.clear();
		aPastCommands.add(pCommand);
	}
	
	void undoCommand()
	{
		if(aPastCommands.empty())
		{
			return;
		}
		Command toUndo = aPastCommands.pop();
		toUndo.undo();
	}
	
	void redoCommand()
	{
		if (aUndoneCommands.empty())
		{
			return;
		}
		Command toRedo = aUndoneCommands.pop();
		//toRedo.execute();
		
	}
	
}
