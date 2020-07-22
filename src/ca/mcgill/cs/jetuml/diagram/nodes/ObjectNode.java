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
 *  An object node in an object diagram.
 */
public final class ObjectNode extends NamedNode
{
	private ArrayList<Node> aFields = new ArrayList<>();

	/* 
 	 * Translate the children as well. 
	 * 
	 * @see ca.mcgill.cs.jetuml.diagram.nodes.AbstractNode2#translate(int, int)
	 */
	@Override
	public void translate(int pDeltaX, int pDeltaY)
	{
		super.translate(pDeltaX, pDeltaY);
		getChildren().forEach(child -> child.translate(pDeltaX, pDeltaY));
	}    

	@Override
	public ObjectNode clone()
	{
		ObjectNode cloned = (ObjectNode) super.clone();
		cloned.aFields = new ArrayList<>();
		
		for( Node child : aFields )
		{
			// We can't use addChild(...) here because of the interaction with the original parent.
			Node clonedChild = child.clone();
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
		aFields.add(pIndex, pNode);
		pNode.link(this);
	}

	@Override
	public List<Node> getChildren()
	{
		return Collections.unmodifiableList(aFields); 
	}

	@Override
	public void removeChild(Node pNode)
	{
		assert getChildren().contains(pNode);
		assert pNode.getParent() == this;
		aFields.remove(pNode);
		pNode.unlink();
	}
	
	@Override
	public boolean allowsChildren()
	{
		return true;
	}
}
