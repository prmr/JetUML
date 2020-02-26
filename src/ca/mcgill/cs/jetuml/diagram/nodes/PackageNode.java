/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2019 by the contributors of the JetUML project.
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.Node;

/**
 * A package node in a UML diagram.
 */
public final class PackageNode extends AbstractNode
{
	private String aName = "";
	private String aContents = "";
	private ArrayList<Node> aContainedNodes = new ArrayList<>();
	private Optional<Node> aContainer = Optional.empty();
	
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
		getChildren().forEach(child -> child.translate(pDeltaX, pDeltaY));
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
		for( Node child : aContainedNodes )
		{
			// We can't use addChild(...) here because of the interaction with the original parent.
			Node clonedChild = child.clone();
			clonedChild.link(cloned);
			cloned.aContainedNodes.add(clonedChild);
		}
		return cloned;
	}
	
	@Override
	public Node getParent()
	{
		assert hasParent();
		return aContainer.get();
	}

	@Override
	public void link(Node pNode)
	{
		assert pNode instanceof PackageNode || pNode == null;
		aContainer = Optional.of(pNode);
	}
	
	@Override
	public void unlink()
	{
		assert hasParent();
		aContainer = Optional.empty();
	}

	@Override
	public List<Node> getChildren()
	{
		return Collections.unmodifiableList(aContainedNodes); 
	}

	@Override
	public void addChild(int pIndex, Node pNode)
	{
		assert pNode != null;
		assert pIndex >=0 && pIndex <= aContainedNodes.size();
		if(pNode.hasParent())
		{
			pNode.getParent().removeChild(pNode);
		}
		aContainedNodes.add(pIndex, pNode);
		pNode.link(this);
	}

	@Override
	public void addChild(Node pNode)
	{
		assert pNode != null;
		addChild(aContainedNodes.size(), pNode);
	}

	@Override
	public void removeChild(Node pNode)
	{
		assert getChildren().contains(pNode);
		assert pNode.getParent() == this;
		aContainedNodes.remove(pNode);
		pNode.unlink();
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

	@Override
	public boolean hasParent()
	{
		return aContainer.isPresent();
	}
	
	@Override
	public boolean allowsChildren()
	{
		return true;
	}
}
