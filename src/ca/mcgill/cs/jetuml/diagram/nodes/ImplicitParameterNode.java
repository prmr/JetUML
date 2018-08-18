/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2018 by the contributors of the JetUML project.
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

import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.nodes.ImplicitParameterNodeView;
import ca.mcgill.cs.jetuml.views.nodes.NodeView;

/**
 * An implicit parameter node in a sequence diagram. The 
 * visual portion of this node includes the top rectangle (object) and
 * its vertical life line. The ImplicitParamterNode's creator is the
 * CallNode that is the source of a <<creates>> edge that leads to 
 * this node, or null if this node is node created as part of the 
 * sequence.
 */
public final class ImplicitParameterNode extends NamedNode implements ParentNode
{
	private List<ChildNode> aCallNodes = new ArrayList<>();

	@Override
	protected NodeView generateView()
	{
		return new ImplicitParameterNodeView(this);
	}

	@Override
	public ImplicitParameterNode clone()
	{
		ImplicitParameterNode cloned = (ImplicitParameterNode) super.clone();
		cloned.aCallNodes = new ArrayList<>();
		for( ChildNode child : aCallNodes )
		{
			// We can't use addChild(...) here because of the interaction with the original parent.
			ChildNode clonedChild = (ChildNode) child.clone();
			clonedChild.setParent(cloned);
			cloned.aCallNodes.add(clonedChild);
		}
		return cloned;
	}
	
	@Override
	public List<ChildNode> getChildren()
	{
		return aCallNodes;
	}

	@Override
	public void addChild(int pIndex, ChildNode pNode)
	{
		addChild(pNode);
	}
	
	@Override
	public void addChild(ChildNode pNode)
	{
		ParentNode oldParent = pNode.getParent();
		if (oldParent != null)
		{
			oldParent.removeChild(pNode);
		}
		aCallNodes.add(pNode);
		pNode.setParent(this);
	}

	@Override
	public void removeChild(ChildNode pNode)
	{
		if (pNode.getParent() != this)
		{
			return;
		}
		aCallNodes.remove(pNode);
		pNode.setParent(null);
	}
	
	/**
	 * @return The bounds of the top rectangle.
	 */
	public Rectangle getTopRectangle()
	{
		return ((ImplicitParameterNodeView)view()).getTopRectangle();
	}
}
