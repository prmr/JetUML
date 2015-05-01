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

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Graphs that supports hierarchical parent-child relations
 * between its nodes.
 */
public abstract class HierarchicalGraph extends Graph
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
				if(pNode instanceof HierarchicalNode && parent instanceof HierarchicalNode)
				{
					HierarchicalNode curNode = (HierarchicalNode) pNode;
					HierarchicalNode parentParent = (HierarchicalNode) parent;
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
			if(n2 instanceof HierarchicalNode && pNode instanceof HierarchicalNode)
			{
				HierarchicalNode curNode = (HierarchicalNode) n2;
				HierarchicalNode parentParent = (HierarchicalNode) pNode;
				if(curNode.getParent()!= null && curNode.getParent().equals(pNode))
				{
					aModListener.childDetached(this, parentParent.getChildren().indexOf(curNode), parentParent, curNode);
				}
			}
		}
		/*Remove the children too @JoelChev*/
		if(pNode instanceof HierarchicalNode)
		{
			ArrayList<HierarchicalNode> children = new ArrayList<HierarchicalNode>(((HierarchicalNode) pNode).getChildren());
			//We create a shallow clone so deleting children does not affect the loop
			for(Node childNode: children)
			{
				removeNode(childNode);
			}
		}
		super.removeNode(pNode);
		aModListener.endCompoundListening();
		return true;
	}
}


