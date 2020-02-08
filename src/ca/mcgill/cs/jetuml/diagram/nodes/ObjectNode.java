/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2019 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.diagram.nodes;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.jetuml.diagram.Node;

/**
 *  An object node in an object diagram.
 */
public final class ObjectNode extends NamedNode implements ParentNode
{
	private ArrayList<ChildNode> aFields = new ArrayList<>();

	/* 
 	 * Translate the children as well. 
	 * 
	 * @see ca.mcgill.cs.jetuml.diagram.nodes.AbstractNode2#translate(int, int)
	 */
	@Override
	public void translate(int pDeltaX, int pDeltaY)
	{
		super.translate(pDeltaX, pDeltaY);
		for(Node child : getChildren())
		{
			child.translate(pDeltaX, pDeltaY);
		}   
	}    

	@Override
	public ObjectNode clone()
	{
		ObjectNode cloned = (ObjectNode) super.clone();
		cloned.aFields = new ArrayList<>();
		
		for( ChildNode child : aFields )
		{
			// We can't use addChild(...) here because of the interaction with the original parent.
			ChildNode clonedChild = (ChildNode) child.clone();
			clonedChild.link(cloned);
			cloned.aFields.add(clonedChild);
		}
		return cloned;
	}

	@Override
	public void addChild(Node pNode)
	{
		addChild(aFields.size(), pNode);
	}
	
	@Override
	public void addChild(int pIndex, Node pNode)
	{
		if( pNode.hasParent() )
		{
			pNode.getParent().removeChild(pNode);
		}
		aFields.add(pIndex, (ChildNode)pNode);
		pNode.link(this);
	}

	@Override
	public List<ChildNode> getChildren()
	{
		return aFields; // TODO there should be a remove operation on ObjectNode
	}

	@Override
	public void removeChild(Node pNode)
	{
		assert pNode != null;
		if(pNode.getParent() != this)
		{
			return;
		}
		aFields.remove(pNode);
		pNode.unlink();
	}
}
