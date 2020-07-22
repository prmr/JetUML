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

import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.Node;

/**
 * Common data and implementation for the different flavors of 
 * package nodes. Package nodes have at least a name and a parent.
 */
public class AbstractPackageNode extends AbstractNode
{
	private String aName = "";
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
	protected void buildProperties()
	{
		super.buildProperties();
		properties().add("name", () -> aName, pName -> aName = (String)pName);
	}

	@Override
	public boolean hasParent()
	{
		return aContainer.isPresent();
	}
}
