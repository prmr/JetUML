/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/

package ca.mcgill.cs.jetuml.diagram.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.mcgill.cs.jetuml.diagram.Node;

/**
 * An implicit parameter node in a sequence diagram. The 
 * visual portion of this node includes the top rectangle (object) and
 * its vertical life line. The ImplicitParamterNode's creator is the
 * CallNode that is the source of a "creates" edge that leads to 
 * this node, or null if this node is node created as part of the 
 * sequence.
 */
public final class ImplicitParameterNode extends NamedNode
{
	private List<Node> aCallNodes = new ArrayList<>();

	@Override
	public ImplicitParameterNode clone()
	{
		ImplicitParameterNode cloned = (ImplicitParameterNode) super.clone();
		cloned.aCallNodes = new ArrayList<>();
		for( Node child : aCallNodes )
		{
			// We can't use addChild(...) here because of the interaction with the original parent.
			Node clonedChild = child.clone();
			clonedChild.link(cloned);
			cloned.aCallNodes.add(clonedChild);
		}
		return cloned;
	}
	
	@Override
	public List<Node> getChildren()
	{
		return Collections.unmodifiableList(aCallNodes);
	}

	@Override
	public void addChild(int pIndex, Node pNode)
	{
		addChild(pNode);
	}
	
	@Override
	public void addChild(Node pNode)
	{
		if(pNode.hasParent())
		{
			pNode.getParent().removeChild(pNode);
		}
		aCallNodes.add(pNode);
		pNode.link(this);
	}

	@Override
	public void removeChild(Node pNode)
	{
		assert getChildren().contains(pNode);
		assert pNode.getParent() == this;
		aCallNodes.remove(pNode);
		pNode.unlink();
	}
	
	@Override
	public boolean allowsChildren()
	{
		return true;
	}
}
