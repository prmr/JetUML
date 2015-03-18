package ca.mcgill.cs.stg.jetuml.commands;

/*
 * Contains something that was done in the model.
 * This is used for redoing and undoing commands by 
 * the graph listener and the undo manager
 */
/**
 * @author EJBQ
 * An interface to allo for commands to be undoable and redoable.
 *
 */
public interface Command 
{
	/**
	 * A method to allow for the undoing of actions.
	 */
	void undo();
	
	/**
	 *  A method to allow actions to be executed.
	 */
	void execute();
	
}