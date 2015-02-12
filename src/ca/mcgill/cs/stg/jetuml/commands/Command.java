package ca.mcgill.cs.stg.jetuml.commands;

/*
 * Contains something that was done in the model.
 * This is used for redoing and undoing commands by 
 * the graph listener and the undo manager
 */
public interface Command 
{
	public void undo();
	
	public void execute();
	
}