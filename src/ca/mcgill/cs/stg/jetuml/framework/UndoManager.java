package ca.mcgill.cs.stg.jetuml.framework;

import java.util.Stack;

import ca.mcgill.cs.stg.jetuml.commands.Command;
import ca.mcgill.cs.stg.jetuml.commands.CompoundCommand;

/**
 * Performs the undoing and redoing of commands on a graph.
 * @author EJBQ
 *
 */
public class UndoManager 
{
	private Stack<Command> aPastCommands; //the commands that have been input and can be undone
	private Stack<Command> aUndoneCommands; //the commands that have been undone and can be redone
	private Stack<CompoundCommand> aTrackingCommands; //used for many commands coming at once
	private boolean aTracking; //turned on to allow many things to be changed in one command
	private boolean aHoldChanges = false; //turned on while undoing or redoing to prevent duplication
	private GraphPanel aGraphPanel;
	private int aMaxUndone = 30;
	
	/**
	 * Creates a new UndoManager with the GraphPanel.
	 * These should be assigned one per panel.
	 * @param pPanel The panel that our changes will be made to 
	 */
	public UndoManager(GraphPanel pPanel)
	{
		aGraphPanel = pPanel;
		aPastCommands = new Stack<Command>();
		aUndoneCommands = new Stack<Command>();
		aTrackingCommands = new Stack<CompoundCommand>();
		aTracking = false;
	}

	/**
	 * Adds a command to the stack to be undone.
	 * Wipes the redone command if there is anything there.
	 * Will not add the command if changes are being held, which occurs 
	 * when we are in the middle of executing a command.
	 * @param pCommand The command to be added
	 */
	public void add(Command pCommand)
	{
		if(!aHoldChanges)
		{
			if(!aUndoneCommands.empty())
			{
				aUndoneCommands.clear();
			}
			if(aTracking && !aTrackingCommands.empty())
			{
				aTrackingCommands.peek().add(pCommand);
			}
			else
			{
				aPastCommands.push(pCommand);
			}
		}
	}

	/**
	 * Undoes a command.
	 * Holds changes so no new commands are added during this.
	 * Adds the command to the redone stack.
	 */
	public void undoCommand()
	{
		if(aPastCommands.empty())
		{
			return;
		}
		aHoldChanges = true;
		Command toUndo = aPastCommands.pop();
		toUndo.undo();
		aUndoneCommands.push(toUndo);
		aHoldChanges = false;
		aGraphPanel.repaint();
	}

	/**
	 * Pops most recent undone command and executes it.
	 * Holds changes so no new commands are added during this.
	 * Adds the command to the redone stack.
	 */
	void redoCommand()
	{
		aHoldChanges = true;
		if (aUndoneCommands.empty())
		{
			return;
		}
		Command toRedo = aUndoneCommands.pop();
		toRedo.execute();
		aPastCommands.push(toRedo);
		aHoldChanges = false;
		aGraphPanel.repaint();
	}

	/**
	 * Creates a compound command that all coming commands will be added to.
	 * Used to perform many commands at once
	 */
	public void startTracking()
	{
		aTracking = true;
		aTrackingCommands.push(new CompoundCommand());
	}

	/**
	 * Finishes off the compound command and adds it to the stack.
	 */
	public void endTracking()
	{
		if(!aTrackingCommands.empty())
		{
			CompoundCommand cc = aTrackingCommands.pop(); 
			if(cc.size() > 0)
			{
				add(cc);
			}
		}
		if(!aTrackingCommands.empty())
		{
			aTracking = false;
		}
	}

}
