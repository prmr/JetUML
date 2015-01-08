/*
Violet - A program for editing UML diagrams.

Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.horstmann.violet.framework;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *  A class that supplies convenience implementations for 
 *  a number of methods in the Node interface.
 */
public abstract class AbstractNode implements Node
{
	public static final int SHADOW_GAP = 4;
	
	private static final long serialVersionUID = -8705997187128842393L;
	private static final Color SHADOW_COLOR = Color.LIGHT_GRAY;
	
	private ArrayList<Node> aChildren;
	private Node aParent;
	
	/**
     * Constructs a node with no parents or children.
	 */
	public AbstractNode()
	{
		aChildren = new ArrayList<>();
		aParent = null;
	}

	@Override
	public AbstractNode clone()
	{
		try
		{
			AbstractNode cloned = (AbstractNode) super.clone();
			cloned.aChildren = new ArrayList<Node>(aChildren.size());
			for (int i = 0; i < aChildren.size(); i++)
			{
				Node n = (Node)aChildren.get(i);
				cloned.aChildren.set(i, n.clone());
				n.setParent(cloned);
			}
			return cloned;
		}
		catch(CloneNotSupportedException exception)
		{
			return null;
		}
	}

	@Override
	public void translate(double pDeltaX, double pDeltaY)
	{
		for(Node node : aChildren)
		{
			node.translate(pDeltaX, pDeltaY);
		}
	}

	@Override
	public boolean addEdge(Edge pEdge, Point2D pPoint1, Point2D pPoint2)
	{
		return pEdge.getEnd() != null;
	}

	@Override
	public void removeEdge(Graph pGraph, Edge pEdge)
	{}

	@Override
	public void removeNode(Graph pGraph, Node pEdge)
	{
		if(pEdge == aParent)
		{
			aParent = null;
		} 
		if(pEdge.getParent() == this)
		{
			aChildren.remove(pEdge);
		}
	}

	@Override
	public void layout(Graph pGraph, Graphics2D pGraphics2D, Grid pGrid)
	{}

	@Override
	public boolean addNode(Node pNode, Point2D pPoint)
	{
		return false;
	}

	@Override
	public Node getParent()
	{ return aParent; }

	@Override
	public void setParent(Node pNode) 
	{ aParent = pNode; }

	@Override
	public List<Node> getChildren() 
	{ return aChildren; }

	@Override
	public void addChild(int pIndex, Node pNode) 
	{
		Node oldParent = pNode.getParent();
		if(oldParent != null)
		{
			oldParent.removeChild(pNode);
		}
		aChildren.add(pIndex, pNode);
		pNode.setParent(this);
	}

	/**
	 * Add a child node.
	 * @param pNode The node to add.
	 */
	protected void addChild(Node pNode)
	{
		addChild(aChildren.size(), pNode);
	}

	@Override
	public void removeChild(Node pNode)
	{
		if(pNode.getParent() != this)
		{
			return;
		}
		aChildren.remove(pNode);
		pNode.setParent(null);
   }

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		Shape shape = getShape();
		if(shape == null)
		{
			return;
		}
      
		Color oldColor = pGraphics2D.getColor();
		pGraphics2D.translate(SHADOW_GAP, SHADOW_GAP);      
		pGraphics2D.setColor(SHADOW_COLOR);
		pGraphics2D.fill(shape);
		pGraphics2D.translate(-SHADOW_GAP, -SHADOW_GAP);
		pGraphics2D.setColor(pGraphics2D.getBackground());
		pGraphics2D.fill(shape);      
		pGraphics2D.setColor(oldColor);
	}
   
	private Shape getShape() 
	{ return null; }   
   
	/**
     * Adds a persistence delegate to a given encoder that
     * encodes the child nodes of this node.
     * @param pEncoder the encoder to which to add the delegate
     */
	public static void setPersistenceDelegate(Encoder pEncoder)
	{
      pEncoder.setPersistenceDelegate(AbstractNode.class, new DefaultPersistenceDelegate()
         {
            protected void initialize(Class<?> pType, Object pOldInstance, Object pNewInstance, Encoder pOut) 
            {
               super.initialize(pType, pOldInstance, pNewInstance, pOut);
               Node n = (Node) pOldInstance;
               for(Node c : n.getChildren())
               {
            	   pOut.writeStatement(new Statement(pOldInstance, "addChild", new Object[]{ c }) );            
               }
            }
         });
	}
}

