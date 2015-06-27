package ca.mcgill.cs.stg.jetuml.commands;

import ca.mcgill.cs.stg.jetuml.graph.ChildNode;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.ObjectNode;
import ca.mcgill.cs.stg.jetuml.graph.PackageNode;
import ca.mcgill.cs.stg.jetuml.graph.ParentNode;

/**
 * Stores the attachment between a child and parent node.
 * @author EJBQ
 *
 */
public class AttachDetachChildCommand implements Command
{
	private ParentNode aParent;
	private ChildNode aChild;
	private Graph aGraph;
	private boolean aAdding; //true for adding, false for deleting
	private int aIndex;
	
	/**
	 * Gives us the pieces we need and sets up the command.
	 * @param pIndex The index of the parent the child belongs to
	 * @param pGraph The panel that we are changing the children of
	 * @param pParent The parent node
	 * @param pChild The child node
	 * @param pAdding True when adding, false when removing
	 */
	public AttachDetachChildCommand(Graph pGraph, int pIndex, ParentNode pParent, ChildNode pChild, boolean pAdding)
	{
		aIndex = pIndex;
		aGraph = pGraph;
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
	 * Delete the child attachment.
	 */
	private void delete() 
	{
		if(aParent instanceof ObjectNode)
		{
			((ObjectNode) aParent).removeChild(aChild);
		}
		else if (aParent instanceof PackageNode)
		{
			((PackageNode) aParent).removeChild(aChild);
		}
		aGraph.layout();
	}
	
	/**
	 * Add the child attachment.
	 */
	private void add() 
	{
		if(aParent instanceof ObjectNode)
		{
			((ObjectNode) aParent).addChild(aIndex, aChild);
		}
		else if (aParent instanceof PackageNode)
		{
			((PackageNode) aParent).addChild(aIndex, aChild);
		}
		aGraph.layout();
	}
	
}