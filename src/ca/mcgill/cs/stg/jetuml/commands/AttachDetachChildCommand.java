package ca.mcgill.cs.stg.jetuml.commands;

import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.graph.Node;

/**
 * Stores the attachment between a child and parent node.
 * @author EJBQ
 *
 */
public class AttachDetachChildCommand implements Command
{
	private Node aParent;
	private Node aChild;
	private GraphPanel aGraphPanel;
	private double aX;
	private double aY;
	private boolean aAdding; //true for adding, false for deleting
	
	/**
	 * Gives us the pieces we need and sets up the command.
	 * @param pGraphPanel The panel that we are changing the children of
	 * @param pParent The parent node
	 * @param pChild The child node
	 * @param pAdding True when adding, false when removing
	 */
	public AttachDetachChildCommand(GraphPanel pGraphPanel, Node pParent, Node pChild, boolean pAdding)
	{
		aGraphPanel = pGraphPanel;
		aParent = pParent;
		aChild = pChild;
		aAdding = pAdding;
	}
	
	/**
	 * Undoes the command and adds/deletes the attachment.
	 */
	public void undo() 
	{
		if(aAdding)
		{
			delete();
		}
		else
		{
			add();
		}
	}

	/**
	 * Performs the command and adds/deletes the attachment.
	 */
	public void execute() 
	{
		if(aAdding)
		{
			add();
		}
		else
		{
			delete();
		}
	}

	/**
	 * TODO : Delete the child attachment.
	 */
	private void delete() 
	{
		aGraphPanel.layoutGraph();
		aGraphPanel.repaint();

	}
	
	/**
	 * TODO : Add the child attachment.
	 */
	private void add() 
	{
		aGraphPanel.layoutGraph();
		aGraphPanel.repaint();
	}
	
}