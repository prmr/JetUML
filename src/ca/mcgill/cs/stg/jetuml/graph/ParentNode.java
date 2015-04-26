package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.geom.Point2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that supplies convenience implementations for 
 * a number of methods in the Node interface.
 */
public abstract class ParentNode extends RectangularNode
{
	private ArrayList<ParentNode> aChildren;
	private ParentNode aParent;
	
	/**
     * Constructs a node with no parents or children.
	 */
	public ParentNode()
	{
		aChildren = new ArrayList<>();
		aParent = null;
	}

	@Override
	public ParentNode clone()
	{
//		try
//		{
			ParentNode cloned = (ParentNode) super.clone();
			cloned.aChildren = new ArrayList<ParentNode>();
			return cloned;
//		}
//		catch(CloneNotSupportedException exception)
//		{
//			return null;
//		}
	}

	@Override
	public void removeNode(Graph pGraph, Node pNode)
	{
		if(pNode == aParent)
		{
			aParent = null;
		}
		if(pNode instanceof ParentNode && ((ParentNode)pNode).getParent() == this)
		{
			aChildren.remove(pNode);
		}
	}
	
	@Override
	public boolean addNode(Node pNode, Point2D pPoint)
	{
		return false;
	}

	/**
     * Gets the parent of this node.
     * @return the parent node, or null if the node has no parent
	 */
	public ParentNode getParent() 
   	{ return aParent; }

	/**
     * Sets the parent of this node.
     * @param pNode the parent node, or null if the node has no parent
	 */
	public void setParent(ParentNode pNode) 
	{ aParent = pNode; }

	/**
     * Gets the children of this node.
     * @return an unmodifiable list of the children
	 */
	public List<ParentNode> getChildren() 
	{ return aChildren; }

	/**
     * Adds a child node.
     * @param pIndex the position at which to add the child
     * @param pNode the child node to add
	 */
	public void addChild(int pIndex, ParentNode pNode) 
	{
		ParentNode oldParent = pNode.getParent();
		if (oldParent != null)
		{
			oldParent.removeChild(pNode);
		}
		aChildren.add(pIndex, pNode);
		pNode.setParent(this);
	}

	/**
	 * Adds a node at the end of the list.
	 * @param pNode The node to add.
	 */
	public void addChild(ParentNode pNode)
	{
		addChild(aChildren.size(), pNode);
	}

	/**
     * Removes a child node.
     * @param pNode the child to remove.
	 */
	public void removeChild(ParentNode pNode)
	{
		if (pNode.getParent() != this)
		{
			return;
		}
		aChildren.remove(pNode);
		pNode.setParent(null);
	}
   
	/**
     *  Adds a persistence delegate to a given encoder that
     * encodes the child nodes of this node.
     * @param pEncoder the encoder to which to add the delegate
     */
	public static void setPersistenceDelegate(Encoder pEncoder)
	{
      pEncoder.setPersistenceDelegate(ParentNode.class, new DefaultPersistenceDelegate()
         {
            protected void initialize(Class<?> pType, Object pOldInstance, Object pNewInstance, Encoder pOut) 
            {
            	super.initialize(pType, pOldInstance, pNewInstance, pOut);
            	for(ParentNode node : ((ParentNode) pOldInstance).getChildren())
            	{
            		pOut.writeStatement( new Statement(pOldInstance, "addChild", new Object[]{ node }) );            
               }
            }
         });
   }
}

