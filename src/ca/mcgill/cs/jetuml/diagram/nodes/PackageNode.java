/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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
import ca.mcgill.cs.jetuml.views.nodes.NodeView;
import ca.mcgill.cs.jetuml.views.nodes.PackageNodeView;

/**
 *   A package node in a UML diagram.
 */
public final class PackageNode extends AbstractNode implements ParentNode, ChildNode
{
	private String aName = "";
	private String aContents = "";
	private ArrayList<ChildNode> aContainedNodes = new ArrayList<>();
	private ParentNode aContainer;
	
	@Override
	protected NodeView generateView()
	{
		return new PackageNodeView(this);
	}
	
	/**
     * Sets the name property value.
     * @param pName the class name
	 */
	public void setName(String pName)
	{
		aName = pName;
	}

	/**
     * Gets the name property value.
     * @return the class name
	 */
	public String getName()
	{
		return aName;
	}

	/**
     * Sets the contents property value.
     * @param pContents the contents of this class
	 */
	public void setContents(String pContents)
	{
		aContents = pContents;
	}
	
	@Override
	public void translate(int pDeltaX, int pDeltaY)
	{
		super.translate(pDeltaX, pDeltaY);
		
		for(Node childNode : getChildren())
        {
        	childNode.translate(pDeltaX, pDeltaY);
        }   
	}

	/**
     * Gets the contents property value.
     * @return the contents of this class
	 */
	public String getContents()
	{
		return aContents;
	}

	@Override
	public PackageNode clone()
	{
		PackageNode cloned = (PackageNode) super.clone();
		cloned.aContainedNodes = new ArrayList<>();
		for( ChildNode child : aContainedNodes )
		{
			// We can't use addChild(...) here because of the interaction with the original parent.
			ChildNode clonedChild = (ChildNode) child.clone();
			clonedChild.setParent(cloned);
			cloned.aContainedNodes.add(clonedChild);
		}
		return cloned;
	}
	
	@Override
	public ParentNode getParent()
	{
		return aContainer;
	}

	@Override
	public void setParent(ParentNode pNode)
	{
		assert pNode instanceof PackageNode || pNode == null;
		aContainer = pNode;
	}

	@Override
	public List<ChildNode> getChildren()
	{
		return aContainedNodes; // TODO there should be a remove operation on PackageNode
	}

	@Override
	public void addChild(int pIndex, ChildNode pNode)
	{
		assert pNode != null;
		assert pIndex >=0 && pIndex <= aContainedNodes.size();
		ParentNode oldParent = pNode.getParent();
		if (oldParent != null)
		{
			oldParent.removeChild(pNode);
		}
		aContainedNodes.add(pIndex, pNode);
		pNode.setParent(this);
	}

	@Override
	public void addChild(ChildNode pNode)
	{
		assert pNode != null;
		addChild(aContainedNodes.size(), pNode);
	}

	@Override
	public void removeChild(ChildNode pNode)
	{
		aContainedNodes.remove(pNode);
		pNode.setParent(null);
	}
	
	@Override
	public boolean requiresParent()
	{
		return false;
	}
	
	@Override
	protected void buildProperties()
	{
		super.buildProperties();
		properties().add("name", () -> aName, pName -> aName = (String)pName);
		properties().add("contents", () -> aContents, pContents -> aContents = (String)pContents);
	}
}
