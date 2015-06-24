package ca.mcgill.cs.stg.jetuml.graph;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Nodes that can be involved in a hieratchical parent-child
 * relation.
 */
public abstract class HNode extends RectangularNode implements HierarchicalNode
{
	private ArrayList<HierarchicalNode> aChildren;
	private HierarchicalNode aParent;
	
	/**
     * Constructs a node with no parents or children.
	 */
	public HNode()
	{
		aChildren = new ArrayList<>();
		aParent = null;
	}

	@Override
	public HNode clone()
	{
		HNode cloned = (HNode) super.clone();
		cloned.aChildren = new ArrayList<HierarchicalNode>();
		return cloned;
	}

	/**
     * Gets the parent of this node.
     * @return the parent node, or null if the node has no parent
	 */
	public HierarchicalNode getParent() 
   	{ return aParent; }

	/**
     * Sets the parent of this node.
     * @param pNode the parent node, or null if the node has no parent
	 */
	public void setParent(HierarchicalNode pNode) 
	{ aParent = pNode; }

	/**
     * Gets the children of this node.
     * @return an unmodifiable list of the children
	 */
	public List<HierarchicalNode> getChildren() 
	{ return aChildren; }

	/**
     * Adds a child node.
     * @param pIndex the position at which to add the child
     * @param pNode the child node to add
	 */
	public void addChild(int pIndex, HierarchicalNode pNode) 
	{
		HierarchicalNode oldParent = pNode.getParent();
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
	public void addChild(HierarchicalNode pNode)
	{
		addChild(aChildren.size(), pNode);
	}

	/**
     * Removes a child node.
     * @param pNode the child to remove.
	 */
	public void removeChild(HierarchicalNode pNode)
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
      pEncoder.setPersistenceDelegate(HNode.class, new DefaultPersistenceDelegate()
         {
            protected void initialize(Class<?> pType, Object pOldInstance, Object pNewInstance, Encoder pOut) 
            {
            	super.initialize(pType, pOldInstance, pNewInstance, pOut);
            	for(HierarchicalNode node : ((HierarchicalNode) pOldInstance).getChildren())
            	{
            		pOut.writeStatement( new Statement(pOldInstance, "addChild", new Object[]{ node }) );            
               }
            }
         });
   }
}

