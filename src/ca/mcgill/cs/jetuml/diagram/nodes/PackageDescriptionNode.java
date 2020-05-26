/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by the contributors of the JetUML project.
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

import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.Node;

/**
 * A package description node in a UML diagram.
 */
public final class PackageDescriptionNode extends AbstractNode
{
	private String aName = "";
	private String aContents = "";
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
	
	/**
     * Gets the contents property value.
     * @return the contents of this class
	 */
	public String getContents()
	{
		return aContents;
	}

	@Override
	public PackageDescriptionNode clone()
	{
		return (PackageDescriptionNode) super.clone();
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
		assert pNode instanceof PackageDescriptionNode || pNode == null;
		aContainer = Optional.of(pNode);
	}
	
	@Override
	public void unlink()
	{
		assert hasParent();
		aContainer = Optional.empty();
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
}
