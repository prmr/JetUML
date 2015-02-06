package ca.mcgill.cs.stg.jetuml.framework;

import java.util.Stack;

public class UndoManager {
	private Stack<Command> aPastCommands;
	private Stack<Command> aUndoneCommands;
	private CompoundCommand aTrackingCommand; //used for many commands coming at once
	private boolean aTracking;
	private boolean areUndoneCommands = false;
	private boolean holdChanges = false;
	
	public UndoManager()
	{
		aPastCommands = new Stack<Command>();
		aUndoneCommands = new Stack<Command>();
	}
	
	public void add(Command pCommand)
	{
		if(areUndoneCommands)
		{
			aUndoneCommands.clear();
		}
		if(!holdChanges)
		{
			if(aTracking)
			{
				aTrackingCommand.add(pCommand);
			}
			else
			{
				aPastCommands.push(pCommand);
			}
		}
	}
	
	public void undoCommand()
	{
		if(aPastCommands.empty())
		{
			return;
		}
		Command toUndo = aPastCommands.pop();
		toUndo.undo();
		aUndoneCommands.push(toUndo);
		areUndoneCommands = true;
	}
	
	/**
	 * Pops most recent undone command and executes it
	 */
	void redoCommand()
	{
		if (!areUndoneCommands)
		{
			return;
		}
		Command toRedo = aUndoneCommands.pop();
		toRedo.execute();
		aPastCommands.push(toRedo);
		if(aUndoneCommands.empty())
		{
			areUndoneCommands = false;
		}
	}
	
	/**
	 * Creates a compound command that all coming commands will be added to
	 * Used to many commands at once
	 */
	public void startTracking()
	{
		aTracking = true;
		aTrackingCommand = new CompoundCommand();
	}
	
	/**
	 * Finishes off the compound command and adds it to the stack
	 */
	public void endTracking()
	{
		aTracking = false;
		aPastCommands.add(aTrackingCommand);
		areUndoneCommands = false;
		aUndoneCommands.clear();
	}
	
}
