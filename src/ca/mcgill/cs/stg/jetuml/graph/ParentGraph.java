package ca.mcgill.cs.stg.jetuml.graph;

/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
 *
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;
import java.util.ArrayList;
import java.util.Collection;

import ca.mcgill.cs.stg.jetuml.framework.GraphModificationListener;
import ca.mcgill.cs.stg.jetuml.framework.Grid;

/**
 *  A graph consisting of selectable nodes and edges.
 */
public abstract class ParentGraph extends Graph
{

	/**
	 * Adds a node to the graph so that the top left corner of
	 * the bounding rectangle is at the given point.
	 * @param pNode the node to add
	 * @param pPoint the desired location
	 * @return True if the node was added.
	 */
	public boolean add(Node pNode, Point2D pPoint)
	{
		aModListener.startCompoundListening();

		if(!super.add(pNode, pPoint))
		{
			aModListener.endCompoundListening();
			return false;
		}
		
		boolean accepted = false;
		for(int i = aNodes.size() - 1; i >= 0 && !accepted; i--)
		{
			Node parent = aNodes.get(i);
			if (parent == pNode)
			{
				continue;
			}
			if (parent.contains(pPoint) && parent.addNode(pNode, pPoint))
			{
				if(pNode instanceof ParentNode && parent instanceof ParentNode)
				{
					ParentNode curNode = (ParentNode) pNode;
					ParentNode parentParent = (ParentNode) parent;
					aModListener.childAttached(this, parentParent.getChildren().indexOf(pNode), parentParent, curNode);
				}
				accepted = true;
			}
		}
		aModListener.endCompoundListening();
		return true;
	}


	/**
	 * Removes a node and all edges that start or end with that node.
	 * @param pNode the node to remove
	 * @return false if node was already deleted, true if deleted properly
	 */
	public boolean removeNode(Node pNode)
	{
		if(aNodesToBeRemoved.contains(pNode))
		{
			return false;
		}
		aModListener.startCompoundListening();
		// notify nodes of removals
		for(int i = 0; i < aNodes.size(); i++)
		{
			Node n2 = aNodes.get(i);
			if(n2 instanceof ParentNode && pNode instanceof ParentNode)
			{
				ParentNode curNode = (ParentNode) n2;
				ParentNode parentParent = (ParentNode) pNode;
				if(curNode.getParent()!= null && curNode.getParent().equals(pNode))
				{
					aModListener.childDetached(this, parentParent.getChildren().indexOf(curNode), parentParent, curNode);
				}
			}
		}
		
		super.removeNode(pNode);
		
		/*Remove the children too @JoelChev*/
		if(pNode instanceof ParentNode)
		{
			for(Node childNode: ((ParentNode)pNode).getChildren())
			{
				removeNode(childNode);
			}
		}
		aModListener.endCompoundListening();
		return true;
	}
}


